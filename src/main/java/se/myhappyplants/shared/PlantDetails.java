package se.myhappyplants.shared;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Class with additional details about a plant for the user
 */

public class PlantDetails implements Serializable {

    private static final long serialVersionUID = 123456789L;
    private String familyName;
    private String scientificName;
    private String description;
    private String recommended_watering_frequency;
    private List<String> sunlight;


    public PlantDetails(String familyName, String description, String recommended_watering_frequency, List<String> sunlight, String scientificName) {
        this.familyName = familyName;
        this.description = description;
        this.recommended_watering_frequency = recommended_watering_frequency;
        this.sunlight = sunlight;
        this.scientificName = scientificName;
    }

    public PlantDetails(String familyName, String description, String recommended_watering_frequency, List<String> sunlight) {
        this.familyName = familyName;
        this.description = description;
        this.recommended_watering_frequency = recommended_watering_frequency;
        this.sunlight = sunlight;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getDescription() {
        return description;
    }

    public String getRecommended_watering_frequency() {
        return recommended_watering_frequency;
    }

    public List<String> getSunlight() {
        return sunlight;
    }

    public String getScientificName() {
        return scientificName;
    }

    @Override
    public String toString() {
        return "PlantDetails{" +
                "familyName='" + familyName + '\'' +
                ", description='" + description + '\'' +
                ", recommended_watering_frequency='" + recommended_watering_frequency + '\'' +
                ", sunlight=" + sunlight +
                '}';
    }
}
