package se.myhappyplants.client.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import se.myhappyplants.client.controller.SearchTabPaneController;

import se.myhappyplants.client.model.ImageLibrary;
import se.myhappyplants.shared.Plant;
import se.myhappyplants.shared.PlantDetails;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Class that initialize and sets up the search plant pane
 * Created by: Linn Borgström, Eric Simonsson, Susanne Vikström, 2021-04-21
 * Updated by: Linn Borgström, 2021-04-30
 */
public class SearchPlantPane extends Pane implements PlantPane {
    private ImageView image;
    private Label commonName;
    private Label scientificName;
    private Button infoButton;
    private Button addButton;
    private Button wishlistButton;

    private Plant plant; // when we create an object of this class the plant object is from the search and has the ID, commonName and image
    private SearchTabPaneController searchTabPaneController;
    private ListView listView;
    private ImageView imgViewPlusSign;
    private ImageView imgViewWishlistSign;
    private boolean gotInfoOnPlant;
    private boolean extended;

    /**
     * Constructor to initialize some variables and sets off the initialization
     *
     * @param searchTabPaneController
     * @param imgPath
     * @param plant
     */
    public SearchPlantPane(SearchTabPaneController searchTabPaneController, Image image, Plant plant) {
        this.searchTabPaneController = searchTabPaneController;
        this.plant = plant;
        initImage(image);
        initCommonName();
        initScientificName();
        initInfoButton();
        initAddButton();
        initWishlistButton();
        initImgViewPlusSign();
        initImgViewWishlistSign();
        initListView();
        initEventHandlerForInfo();
    }

    /**
     * Method to initialize the image
     *
     * @param imgPath
     */
    private void initImage(Image img) {
        if (img.isError()) {
            System.err.println("Error loading image in SearchPlantPane initImage: " + plant.getImageURL());
        }
        this.image = new ImageView();
        image.setFitHeight(50.0);
        image.setFitWidth(50.0);
        image.setLayoutY(3.0);
        image.setPickOnBounds(true);
        image.setPreserveRatio(true);
        image.setImage(img);
    }

    /**
     * Method to initialize the common name label
     */
    private void initCommonName() {
        this.commonName = new Label(plant.getCommonName());
        commonName.setLayoutX(60.0);
        commonName.setLayoutY(20.0);
        commonName.prefHeight(17.0);
        commonName.prefWidth(264.0);
    }

    /**
     * Method to initialize scientific name label
     */
    private void initScientificName() {
        this.scientificName = new Label(plant.getScientificName());
        scientificName.setLayoutX(280.0);
        scientificName.setLayoutY(20.0);
        scientificName.prefHeight(17.0);
        scientificName.prefWidth(254.0);
    }

    /**
     * Method to initialize the info button
     */
    private void initInfoButton() {
        this.infoButton = new Button("More info");
        infoButton.setLayoutX(500.0);
        infoButton.setLayoutY(16.0);
        infoButton.setMnemonicParsing(false);
    }

    /**
     * Method to initialize the add button
     */
    private void initAddButton() {
        this.addButton = new Button();
        addButton.setLayoutX(650.0);
        addButton.setLayoutY(16.0);
        addButton.setMnemonicParsing(false);
        addButton.setOnAction(action -> searchTabPaneController.addPlantToCurrentUserLibrary(plant));
    }

    /**
     * Method to initialize the wishlist button
     */
    private void initWishlistButton() {
        this.wishlistButton = new Button();
        wishlistButton.setLayoutX(700.0);
        wishlistButton.setLayoutY(16.0);
        wishlistButton.setMnemonicParsing(false);
        wishlistButton.setOnAction(action -> searchTabPaneController.addPlantToCurrentUserWishlist(plant));
    }

    /**
     * Method to initialize the plus sign to the add button
     */
    private void initImgViewPlusSign() {
        this.imgViewPlusSign = new ImageView(ImageLibrary.getPlusSign());
        imgViewPlusSign.setFitHeight(16);
        imgViewPlusSign.setFitWidth(15);
        addButton.setGraphic(imgViewPlusSign);
    }

    /**
     * Method to initialize the wishlist sign to the wishlist button
     */
    private void initImgViewWishlistSign() {
        this.imgViewWishlistSign = new ImageView(ImageLibrary.getWishlistSign());
        imgViewWishlistSign.setFitHeight(16);
        imgViewWishlistSign.setFitWidth(15);
        wishlistButton.setGraphic(imgViewWishlistSign);
    }

    /**
     * Method for what happens when a user presses the more info button
     */
    public void initEventHandlerForInfo() {
        EventHandler onPress = new EventHandler() {
            @Override
            public void handle(Event event) {
                infoButton.setDisable(true);
                commonName.setDisable(true);
                System.out.println("Pressed more info button, got info on plant?: " + gotInfoOnPlant);
                if (!extended) {
                    if (!gotInfoOnPlant) {
                        System.out.println("Getting info on plant and calling searchTabPaneController.getPlantDetails(plant)");
                        PlantDetails plantDetails = searchTabPaneController.getPlantDetails(plant);
                        ObservableList<String> plantInfo = FXCollections.observableArrayList();
                        plantInfo.add("Scientific name: " + plant.getScientificName());
                        plantInfo.add("Family: " + plantDetails.getFamilyName());
                        plantInfo.add("Light: " + plantDetails.getSunlight());
                        plantInfo.add("Water: " + plantDetails.getRecommended_watering_frequency());
                        plantInfo.add("Description: " + plantDetails.getDescription());
                        listView.setItems(plantInfo);

                        gotInfoOnPlant = true;
                    }
                    extendPaneMoreInfoPlant();
                } else {
                    retractPane();
                }
            }
        };

        commonName.setOpacity(1.0);
        commonName.setOnMouseClicked(onPress);
        infoButton.setOnAction(onPress);
    }

    /**
     * Method to initialize the ListView
     */
    private void initListView() {
        listView = new ListView();
        listView.setLayoutX(this.getWidth());
        listView.setLayoutY(this.getHeight() + 56.0);
        listView.setPrefWidth(740.0);
        listView.setPrefHeight(150.0);

        this.prefHeight(56.0);
        this.getChildren().addAll(image, commonName, scientificName, infoButton, addButton, wishlistButton);
    }


    /**
     * Method to update the image
     */
    public void updateImage() {
        Image img = new Image(plant.getImageURL());
        image.setImage(img);
    }

    /**
     * Getter method to get the plant
     *
     * @return
     */
    public Plant getPlant() {
        return plant;
    }

    /**
     * Method to set a default picture if the plant don't have it in the database
     *
     * @param defaultImage
     */
    public void setDefaultImage(String defaultImage) {
        Image img = new Image(defaultImage);
        image.setImage(img);
    }

    /**
     * Method to extend the pane and show more info on the plant
     */
    public void extendPaneMoreInfoPlant() {
        AtomicReference<Double> height = new AtomicReference<>(this.getHeight());
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(7.5), event -> this.setPrefHeight(height.updateAndGet(v -> (double) (v + 6.25))))
        );
        timeline.setCycleCount(32);
        timeline.play();
        timeline.setOnFinished(action -> {
            this.getChildren().addAll(listView);
            infoButton.setDisable(false);
            commonName.setDisable(false);
        });
        extended = true;
        gotInfoOnPlant = true;
    }

    /**
     * Method to collapse the pane
     */
    public void retractPane() {
        int size = listView.getItems().size();
        for (int i = 0; i < size; i++) {
            listView.getItems().remove(0);
        }

        AtomicReference<Double> height = new AtomicReference<>(this.getHeight());
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(7.5), event -> this.setPrefHeight(height.updateAndGet(v -> (double) (v - 6.25))))
        );
        timeline.setCycleCount(32);
        timeline.play();
        this.getChildren().removeAll(listView);
        timeline.setOnFinished(action -> {
            infoButton.setDisable(false);
            commonName.setDisable(false);
        });
        extended = false;
        gotInfoOnPlant = false;
    }
}

