package se.myhappyplants.shared;

import se.myhappyplants.client.model.PictureRandomizer;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class defining a plant
 * Created by: Frida Jacobsson
 * Updated by: Linn Borgström, Eric Simonson, Susanne Vikström
 */
public class Plant implements Serializable {

    private static final long serialVersionUID = 867522155232174497L;
    private String plantId;
    private String commonName;
    private String scientificName;
    private String imageURL;
    private String nickname;
    private int users_watering_frequency;
    private Date lastWatered;

    /**
     * Creates a plant object from information gathered from Perenual species-list endpoint
     * @param id
     * @param commonName
     * @param imageURL
     */

    public Plant(String id, String commonName, String scientificName, String imageURL) {
        this.plantId = id;
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.imageURL = imageURL;
    }

    /**
     * Creates a plant object from information
     * in the Species database
     *
     * @param plantId        Unique plant id in Species database
     * @param commonName     Common name
     * @param scientificName Scientific name
     * @param familyName     Family name
     * @param imageURL       Image location
     */
    public Plant(String plantId, String commonName, String scientificName, String familyName, String imageURL) {
        this.plantId = plantId;
        this.commonName = commonName;
        this.scientificName = scientificName;
        this.imageURL = imageURL;
    }


    public Plant(String nickname, String plantID, Date lastWatered) {
        this.nickname = nickname;
        this.plantId = plantID;
        this.lastWatered = lastWatered;
    }

    /**
     * Creates a plant object from a users library
     * in the MyHappyPlants database
     *
     * @param nickname
     * @param plantId        Unique plant id in Species database
     * @param lastWatered    Date the plant was last watered
     * @param waterFrequency How often the plant needs water in milliseconds
     * @param imageURL       Image location
     */
    public Plant(String nickname, String plantId, Date lastWatered, int waterFrequency, String imageURL) {
        this.nickname = nickname;
        this.plantId = plantId;
        this.lastWatered = lastWatered;
        this.users_watering_frequency = waterFrequency;
        this.imageURL = imageURL;
    }

    /**
     * Creates a plant object that can be used to update
     * a users library in the MyHappyPlants database
     *
     * @param nickname
     * @param plantId     Unique plant id in Species database
     * @param lastWatered Date the plant was last watered
     * @param imageURL    Image location
     */
    public Plant(String nickname, String plantId, Date lastWatered, String imageURL) {

        this.nickname = nickname;
        this.plantId = plantId;
        this.lastWatered = lastWatered;
        this.imageURL = imageURL;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getCommonName() {
        return commonName;
    }

    public String getScientificName() {
        return scientificName;
    }

    public String getPlantId() {
        return plantId;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    /**
     * Image location for selected plant
     *
     * @return URL location of image
     */
    public String getImageURL() {
        if(imageURL == null) {
            imageURL = PictureRandomizer.getRandomPictureURL();
        }
        return imageURL.replace("https", "http");
    }

    public Date getLastWatered() {
        return lastWatered;
    }

    public void setLastWatered(LocalDate localDate) {
        this.lastWatered = Date.valueOf(localDate);
    }

    public int getUsers_watering_frequency() {
        return users_watering_frequency;
    }

    public void setUsers_watering_frequency(int users_watering_frequency) {
        this.users_watering_frequency = users_watering_frequency;
    }

    /**
     * Compares the length of time since the plant was watered
     * Returns a decimal value that can be used in a progress bar or indicator
     *
     * @return Double between 0.02 (max time elapsed) and 1.0 (min time elapsed)
     */
    public double getProgress() {
        long daysSinceLastWatered = (System.currentTimeMillis() - lastWatered.getTime()) / (1000 * 60 * 60 * 24);
        double progress = 1.0 - ((double) daysSinceLastWatered / (double) users_watering_frequency);

        if (progress <= 0.02) {
            progress = 0.02;
        } else if (progress >= 0.95) {
            progress = 1.0;
        }
        return progress;
    }

    /**
     * Converts time since last water from milliseconds
     * into days, then returns the value as
     * an explanation text
     *
     * @return Days since last water
     */
    public String getDaysUntilWater() {
        long daysSinceLastWatered = (System.currentTimeMillis() - lastWatered.getTime()) / (1000 * 60 * 60 * 24);
        int daysUntilNextWatering = users_watering_frequency - (int) daysSinceLastWatered;

        if (daysUntilNextWatering <= 0) {
            return "You need to water this plant now!";
        }

        if (daysUntilNextWatering == 1) {
            return "You need to water this plant tomorrow!";
        }

        return String.format("You need to water this plant in %d days", daysUntilNextWatering);
    }

    @Override
    public String toString() {
        return String.format("Common name: %s \tScientific name: %s ", commonName, scientificName);
    }
}