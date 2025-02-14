package se.myhappyplants.server.services;

import org.json.JSONArray;
import org.json.JSONObject;
import se.myhappyplants.shared.Plant;
import se.myhappyplants.server.PasswordsAndKeys;
import se.myhappyplants.shared.PlantDetails;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for fetching plant data from the Perenual API.
 */
public class PlantApiService {
    private final String BASE_URL = "https://perenual.com/api/species-list";
    private final String API_KEY = PasswordsAndKeys.APIToken;
    private final HttpClient httpClient;

    public PlantApiService() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public PlantDetails getPlantDetails(Plant plant) {
        int plantId = Integer.parseInt(plant.getPlantId());

        if (plantId > 3000) { // Plants with ID above 3000 require premium from the API to get detailed information
            System.out.println("Plant ID is above 3000, setting default values and not fetching details from API");
            return new PlantDetails("Unknown", "Unknown", "Unknown", List.of("Unknown"), "Unknown");
        }

        try {
            String query = String.format("https://perenual.com/api/species/details/%s?key=%s", plant.getPlantId(), API_KEY);
            JSONObject jsonResponse = fetchJsonResponse(query);
            return parsePlantDetailsFromJson(jsonResponse);
        } catch (Exception e) {
            System.err.println("Failed to fetch plant details: " + e.getMessage());
            return new PlantDetails("Unknown", "Unknown", "Unknown", List.of("Unknown"), "Unknown");
        }
    }

    public Optional<List<Plant>> getPlants(String plantSearch) {
        System.out.println("Fetching plants from API");
        try {
            String query = buildQueryUrl(plantSearch);
            JSONObject jsonResponse = fetchJsonResponse(query);
            List<Plant> plants = parsePlantsFromJson(jsonResponse);
            plants.sort(Comparator.comparing(Plant::getCommonName, String.CASE_INSENSITIVE_ORDER));
            return plants.isEmpty() ? Optional.empty() : Optional.of(plants);
        } catch (Exception e) {
            System.err.println("Failed to fetch plants: " + e.getMessage());
            return Optional.empty();
        }
    }

    private String buildQueryUrl(String plantSearch) {
        return String.format("%s?key=%s&q=%s",
                BASE_URL,
                API_KEY,
                URLEncoder.encode(plantSearch, StandardCharsets.UTF_8));
    }

    private JSONObject fetchJsonResponse(String query) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(query))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("HTTP error code: " + response.statusCode());
        }
        return new JSONObject(response.body());
    }

    private List<Plant> parsePlantsFromJson(JSONObject jsonResponse) {
        System.out.println("Parsing plants from JSON");
        JSONArray data = jsonResponse.getJSONArray("data");
        List<Plant> plants = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {
            JSONObject plantJson = data.getJSONObject(i);
            String id = String.valueOf(plantJson.getInt("id"));
            String commonName = getJsonString(plantJson, "common_name");
            String scientificName = getJsonArrayAsList(plantJson, "scientific_name").stream().findFirst().orElse("");
            String imageUrl = plantJson.getJSONObject("default_image").getString("thumbnail");
            System.out.println("Plant: " + id + " " + commonName + " " + scientificName + " " + imageUrl);
            plants.add(new Plant(id, commonName, scientificName, imageUrl));
        }
        System.out.println("Parsed " + plants.size() + " plants");
        System.out.println(plants);
        return plants;
    }

    private PlantDetails parsePlantDetailsFromJson(JSONObject plantJson) {
        String familyName = getJsonString(plantJson, "family");
        String description = getJsonString(plantJson, "description");
        List<String> sunlight = getJsonArrayAsList(plantJson, "sunlight");
        String recommendedWateringFrequency = getJsonString(plantJson, "watering");
        String scientificName = getJsonArrayAsList(plantJson, "scientific_name").stream().findFirst().orElse("");
        return new PlantDetails(familyName, description, recommendedWateringFrequency, sunlight, scientificName); // TODO: needed for plant details in user library, maybe can change
    }

    private static List<String> getJsonArrayAsList(JSONObject jsonObject, String key) {
        List<String> result = new ArrayList<>();
        if (jsonObject.has(key)) {
            JSONArray jsonArray = jsonObject.optJSONArray(key);
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    result.add(jsonArray.optString(i));
                }
            }
        }
        return result;
    }

    private String getJsonString(JSONObject json, String key) {
        return json.has(key) && !json.isNull(key) ? json.getString(key) : "";
    }
}