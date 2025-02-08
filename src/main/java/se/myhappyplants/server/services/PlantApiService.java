package se.myhappyplants.server.services;

import org.json.JSONArray;
import org.json.JSONObject;
import se.myhappyplants.shared.Plant;
import se.myhappyplants.server.PasswordsAndKeys;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

    public Optional<List<Plant>> getPlants(String plantSearch) {
        try {
            String query = buildQueryUrl(plantSearch);
            JSONObject jsonResponse = fetchJsonResponse(query);
            List<Plant> plants = parsePlantsFromJson(jsonResponse);
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
        JSONArray data = jsonResponse.getJSONArray("data");
        List<Plant> plants = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {
            JSONObject plantJson = data.getJSONObject(i);
            String id = getJsonString(plantJson, "id");
            String commonName = getJsonString(plantJson, "common_name");
            String imageUrl = plantJson.getJSONObject("default_image").getString("small_url");
            plants.add(new Plant(commonName, id, imageUrl));
        }
        return plants;
    }

    private String getJsonString(JSONObject json, String key) {
        return json.has(key) && !json.isNull(key) ? json.getString(key) : "";
    }
}