package se.myhappyplants.shared;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Class that can be used for communication between Client/Server
 * Client/Server via TCP
 * Created by: Christopher O'Driscoll
 * Updated by: Linn Borgström 2021-05-13
 */
public class Message implements Serializable {

    private MessageType messageType;
    private boolean notifications;
    private String messageText;
    private User user;
    private boolean success;
    private LocalDate date;
    private List<Plant> plantArray;
    private Plant plant;
    private String newNickname;
    private int newWateringFrequency;
    private PlantDetails plantDetails;
    private SortingOption sortingOption;


    /**
     * create a message that can be used to send a boolean value
     *
     * @param success
     */
    public Message(boolean success) {
        this.success = success;
    }

    /**
     * Creates a message which can be used to send a user
     *
     * @param messageType
     * @param user
     */
    public Message(MessageType messageType, User user) {

        this.messageType = messageType;
        this.user = user;
    }

    /**
     * Creates a message that can be used to send
     * a user and a plant object
     *
     * @param messageType
     * @param user
     * @param plant
     */
    public Message(MessageType messageType, User user, Plant plant) {
        this(messageType, user);
        this.plant = plant;
    }

    /**
     * create a message that can be used to send
     * a plant object
     *
     * @param messageType
     * @param plant
     */
    public Message(MessageType messageType, Plant plant) {
        this.messageType = messageType;
        this.plant = plant;
    }

    /**
     * Creates a message that can be used to send
     * a notification setting and a user
     *
     * @param messageType
     * @param notifications
     * @param user
     */
    public Message(MessageType messageType, boolean notifications, User user) {
        this(messageType, user);
        this.notifications = notifications;
    }

    /**
     * Creates a message that can be used to send
     * a user, a plant and a date
     *
     * @param messageType
     * @param user
     * @param plant
     * @param date
     */
    public Message(MessageType messageType, User user, Plant plant, LocalDate date) {
        this(messageType, user, plant);
        this.date = date;
    }

    /**
     * Creates a message that can be used to send
     * a user, a plant and it's new nickname
     *
     * @param messageType
     * @param user
     * @param plant
     * @param newNickname
     */
    public Message(MessageType messageType, User user, Plant plant, String newNickname) {
        this(messageType, user, plant);
        this.newNickname = newNickname;
    }

    /**
     * Creates a message that can be used to send
     * a user and the updated watering frequency
     *
     * @param messageType
     * @param user
     * @param wateringFrequency
     */
    public Message(MessageType messageType, User user, Plant plant, int wateringFrequency) {
        this(messageType, user, plant);
        this.newWateringFrequency = wateringFrequency;
    }


    /**
     * Creates a message that can be used to send
     * an array of plants
     *
     * @param plantArray
     * @param success
     */
    public Message(List<Plant> plantArray, boolean success) {
        this.plantArray = plantArray;
        this.success = success;
    }

    /**
     * Creates a message which can be used to send
     * text
     *
     * @param messageType
     * @param messageText
     */
    public Message(MessageType messageType, String messageText) {
        this.messageType = messageType;
        this.messageText = messageText;
    }

    /**
     * Creates a message which can be used to send
     * text and sorting option
     *
     * @param messageType
     * @param messageText
     */
    public Message(MessageType messageType, String messageText, SortingOption sortingOption) {
        this.messageType = messageType;
        this.messageText = messageText;
        this.sortingOption = sortingOption;
    }

    /**
     * Creates a message which can be used to send
     * a user and a boolean value
     *
     * @param user
     * @param success
     */
    public Message(User user, boolean success) {
        this.user = user;
        this.success = success;
    }


    /**
     * Creates a message that can be used to send
     * further information about a plant
     * @param plantDetails
     * @param success
     */
    public Message(PlantDetails plantDetails, boolean success) {
        this.plantDetails = plantDetails;
        this.success = success;
    }

    public String getNewNickname() {
        return newNickname;
    }

    public int getNewWateringFrequency() {
        return newWateringFrequency;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getMessageText() {
        return messageText;
    }

    public User getUser() {
        return user;
    }

    public boolean isSuccess() {
        return success;
    }

    public List<Plant> getPlantArray() {
        return plantArray;
    }

    public SortingOption getSortingOption() {
        return sortingOption;
    }

    public Plant getPlant() {
        return plant;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean getNotifications() {
        return notifications;
    }

    public PlantDetails getPlantDetails() {
        return plantDetails;
    }
}
