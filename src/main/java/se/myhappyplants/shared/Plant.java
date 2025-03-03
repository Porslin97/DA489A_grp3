package se.myhappyplants.shared;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

/**
 * Class defining a plant
 * Created by: Frida Jacobsson
 * Updated by: Linn Borgström, Eric Simonson, Susanne Vikström
 */
public class Plant implements Serializable {

    private static final long serialVersionUID = 867522155232174497L;

    private int databaseId;
    private String plantId;
    private String commonName;
    private String scientificName;
    private String imageURL;
    private String nickname;
    private int usersWateringFrequency;
    private Date lastWatered;
    private Date dateAdded;

    private boolean isFavorite;

    // for wishlist. Added because limited api calls
    private String family;
    private String light;
    private String water;
    private String description;

    private Plant(PlantBuilder builder) {
        this.databaseId = builder.databaseId;
        this.plantId = builder.plantId;
        this.commonName = builder.commonName;
        this.scientificName = builder.scientificName;
        this.imageURL = builder.imageURL;
        this.nickname = builder.nickname;
        this.usersWateringFrequency = builder.usersWateringFrequency;
        this.lastWatered = builder.lastWatered;
        this.dateAdded = builder.dateAdded;
        this.isFavorite = builder.isFavorite;
        this.family = builder.family;
        this.light = builder.light;
        this.water = builder.water;
        this.description = builder.description;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public String getPlantId() {
        return plantId;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getNickname() {
        return nickname;
    }

    public int getUsersWateringFrequency() {
        return usersWateringFrequency;
    }

    public Date getLastWatered() {
        return lastWatered;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public boolean getIsFavorite() {
        return isFavorite;
    }

    public String getFamily() {
        return family;
    }

    public String getLight() {
        return light;
    }

    public String getWater() {
        return water;
    }

    public String getDescription() {
        return description;
    }

    public double getProgress() {
        long daysSinceLastWatered = (System.currentTimeMillis() - lastWatered.getTime()) / (1000 * 60 * 60 * 24);
        double progress = 1.0 - ((double) daysSinceLastWatered / (double) usersWateringFrequency);

        if (progress <= 0.02) {
            progress = 0.02;
        } else if (progress >= 0.95) {
            progress = 1.0;
        }
        return progress;
    }

    public String getDaysUntilWater() {
        long daysSinceLastWatered = (System.currentTimeMillis() - lastWatered.getTime()) / (1000 * 60 * 60 * 24);
        int daysUntilNextWatering = usersWateringFrequency - (int) daysSinceLastWatered;

        if (daysUntilNextWatering <= 0) {
            return "You need to water this plant now!";
        }

        if (daysUntilNextWatering == 1) {
            return "You need to water this plant tomorrow!";
        }

        return String.format("You need to water this plant in %d days", daysUntilNextWatering);
    }

    public static class PlantBuilder {
        private int databaseId;
        private String plantId;
        private String commonName;
        private String scientificName;
        private String imageURL;
        private String nickname;
        private int usersWateringFrequency;
        private Date lastWatered;
        private Date dateAdded;
        private boolean isFavorite;
        private String family;
        private String light;
        private String water;
        private String description;

        public PlantBuilder() {}

        // Copy constructor to modify an existing Plant object
        public PlantBuilder(Plant existingPlant) {
            this.databaseId = existingPlant.getDatabaseId();
            this.plantId = existingPlant.getPlantId();
            this.commonName = existingPlant.getCommonName();
            this.scientificName = existingPlant.getScientificName();
            this.imageURL = existingPlant.getImageURL();
            this.nickname = existingPlant.getNickname();
            this.usersWateringFrequency = existingPlant.getUsersWateringFrequency();
            this.lastWatered = existingPlant.getLastWatered();
            this.dateAdded = existingPlant.getDateAdded();
            this.isFavorite = existingPlant.getIsFavorite();
            this.family = existingPlant.getFamily();
            this.light = existingPlant.getLight();
            this.water = existingPlant.getWater();
            this.description = existingPlant.getDescription();
        }

        public PlantBuilder setDatabaseId(int databaseId) {
            this.databaseId = databaseId;
            return this;
        }

        public PlantBuilder setPlantId(String plantId) {
            this.plantId = plantId;
            return this;
        }

        public PlantBuilder setCommonName(String commonName) {
            this.commonName = commonName;
            return this;
        }

        public PlantBuilder setScientificName(String scientificName) {
            this.scientificName = scientificName;
            return this;
        }

        public PlantBuilder setImageURL(String imageURL) {
            this.imageURL = imageURL;
            return this;
        }

        public PlantBuilder setNickname(String nickname) {
            this.nickname = nickname;
            return this;
        }

        public PlantBuilder setUsersWateringFrequency(int usersWateringFrequency) {
            this.usersWateringFrequency = usersWateringFrequency;
            return this;
        }

        public PlantBuilder setLastWatered(LocalDate lastWatered) {
            if (lastWatered.isAfter(LocalDate.now())) {
                this.lastWatered = Date.valueOf(LocalDate.now());
            }else {
                this.lastWatered = Date.valueOf(lastWatered);
            }
            return this;
        }

        public PlantBuilder setDateAdded(Date dateAdded) {
            this.dateAdded = dateAdded;
            return this;
        }

        public PlantBuilder setIsFavorite(boolean isFavorite) {
            this.isFavorite = isFavorite;
            return this;
        }

        public PlantBuilder setFamily(String family) {
            this.family = family;
            return this;
        }

        public PlantBuilder setLight(String light) {
            this.light = light;
            return this;
        }

        public PlantBuilder setWater(String water) {
            this.water = water;
            return this;
        }

        public PlantBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Plant build() {
            return new Plant(this);
        }
    }
}