package se.myhappyplants.client.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import se.myhappyplants.client.controller.MyPlantsTabPaneController;
import se.myhappyplants.client.model.BoxTitle;
import se.myhappyplants.client.model.ImageLibrary;
import se.myhappyplants.client.model.PictureRandomizerClient;
import se.myhappyplants.client.util.DialogUtils;
import se.myhappyplants.shared.PlantDetails;
import se.myhappyplants.shared.Plant;

import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;


/**
 * Simple pane that displays a DBPlant's information
 * Created by: Christopher O'Driscoll
 * Updated by: Frida Jacobsson
 */
public class LibraryPlantPane extends Pane implements PlantPane {

    private MyPlantsTabPaneController myPlantsTabPaneController;
    private Plant plant;
    private ImageView image;
    private Label nickname;
    private Label lastWateredLabel;
    private ProgressBar progressBar;
    private Button infoButton;
    private Button waterButton;
    private Button changeNicknameButton;
    private Button changePictureButton;
    private Button updateWateringFrequencyButton;
    private Button deleteButton;

    private Button favoriteButton;
    private DatePicker datePicker;
    private Button changeOKWaterButton;
    private ListView listViewMoreInfo;
    private Label daysUntilWaterlbl;

    private ImageView imgFavoriteSign;

    public boolean extended;
    private boolean gotInfoOnPlant;

    /**
     * Creates a simple pane with loading image
     * while waiting for response from database
     */
    public LibraryPlantPane() {
        Image img = new Image("Blommor/img.png");
        image = new ImageView(img);
        image.setFitHeight(45.0);
        image.setFitWidth(45.0);
        image.setLayoutX(50.0);
        image.setLayoutY(14.0);

        nickname = new Label("Your plants are being loaded from the database..");
        nickname.setLayoutX(100);
        nickname.setLayoutY(25);
        nickname.setPrefWidth(300);
        nickname.setAlignment(Pos.CENTER);

        this.getChildren().addAll(image, nickname);
    }

    /**
     * Creates a pane using information from a user's
     * plant library
     *
     * @param myPlantsTabPaneController MyPlantsTabController which contains logic for elements to use
     * @param plant                     plant object from user's library
     */
    public LibraryPlantPane(MyPlantsTabPaneController myPlantsTabPaneController, Plant plant) {
        this.myPlantsTabPaneController = myPlantsTabPaneController;
        this.plant = plant;
        this.image = new ImageView();
        Image img = new Image(plant.getImageURL());
        initImages(img);
        initNicknameLabel(plant);
        initLastWateredLabel(plant);
        initProgressBar(plant);
        initWaterButton(plant);
        initInfoButton();
        initChangeNicknameButton(plant);
        initChangePictureButton();
        initUpdateWateringFrequencyButton(plant);
        initChangeWaterOKButton(plant);
        initDatePicker();
        initDeleteButton(plant);
        initFavoriteButton();
        initImgFavoriteSign(plant);
        initListView();
    }



    /**
     * Constructor to initialize some variables and initiate library
     *
     * @param myPlantsTabPaneController
     */
    public LibraryPlantPane(MyPlantsTabPaneController myPlantsTabPaneController) {
        this.myPlantsTabPaneController = myPlantsTabPaneController;
        initEmptyLibraryLabel();
    }

    public LibraryPlantPane(boolean isLoading) {
        System.out.println("inside LibraryPlantPane constructor, isLoading: " + isLoading);
        if (isLoading) {
            initLoadingView();
        }
    }

    /**
     * Method to show the user a message that the library is empty
     */
    private void initEmptyLibraryLabel() {
        this.image = new ImageView();
        Image img = PictureRandomizerClient.getRandomPicture();
        initImages(img);
        Label lblEmptyInfo = new Label("Your library is currently empty \nClick here to search for plants to add    --------->");
        lblEmptyInfo.setLayoutX(150.0);
        lblEmptyInfo.setLayoutY(28.0);
        Button btnSearchPlants = new Button("Search for plants");
        btnSearchPlants.setOnAction(action -> myPlantsTabPaneController.getMainPaneController().changeToSearchTab());
        btnSearchPlants.setLayoutX(500.0);
        btnSearchPlants.setLayoutY(40.0);
        this.getChildren().addAll(image, lblEmptyInfo, btnSearchPlants);
    }

    private void initLoadingView() {
        this.image = new ImageView();
        Image img = PictureRandomizerClient.getRandomPicture();
        initImages(img);

        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(40, 40);
        progressIndicator.setLayoutX(150.0);
        progressIndicator.setLayoutY(20.0);

        Label lblLoadingInfo = new Label("Loading your plants...");
        lblLoadingInfo.setLayoutX(150.0);
        lblLoadingInfo.setLayoutY(70.0);

        this.getChildren().addAll(image, progressIndicator, lblLoadingInfo);
    }


    public void updateImage() {
        Image img = new Image(plant.getImageURL());
        image.setImage(img);
    }

    /**
     * Method to initiate the image View
     *
     * @param img
     */
    private void initImages(Image img) {

        image.setFitHeight(70.0);
        image.setFitWidth(70.0);
        image.setLayoutX(50.0);
        image.setLayoutY(5.0);
        image.setPickOnBounds(true);
        image.setPreserveRatio(true);
        image.setImage(img);
    }

    /**
     * Method to initiate the nickname label
     *
     * @param plant
     */
    private void initNicknameLabel(Plant plant) {
        nickname = new Label(plant.getNickname());
        nickname.setLayoutX(0);
        nickname.setLayoutY(70);
        nickname.setPrefWidth(145);
        nickname.setAlignment(Pos.CENTER);
    }

    /**
     * Method to initiate the last watered label
     *
     * @param plant
     */
    private void initLastWateredLabel(Plant plant) {
        this.lastWateredLabel = new Label();
        lastWateredLabel.setLayoutY(226);
        lastWateredLabel.setLayoutX(10);
        Date lastWateredDate = plant.getLastWatered();
        lastWateredLabel.setText("Last watered: " + lastWateredDate.toString());
    }

    /**
     * Method to initiate the progressbar
     *
     * @param plant
     */
    private void initProgressBar(Plant plant) {
        daysUntilWaterlbl = new Label();
        String daysUntilWaterText = plant.getDaysUntilWater();
        daysUntilWaterlbl.setText(daysUntilWaterText);
        daysUntilWaterlbl.setLayoutX(350.0);
        daysUntilWaterlbl.setLayoutY(5.0);

        this.progressBar = new ProgressBar(plant.getProgress());
        setColorProgressBar(plant.getProgress());
        progressBar.setLayoutX(150.0);
        progressBar.setLayoutY(28.0);
        progressBar.setPrefHeight(18.0);
        progressBar.setPrefWidth(575.0);
    }

    /**
     * Mehtosd to get the progressbar
     *
     * @return
     */
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * Method to initiate the water button
     *
     * @param plant
     */
    private void initWaterButton(Plant plant) {
        this.waterButton = new Button("Water");
        waterButton.setLayoutX(400.0);
        waterButton.setLayoutY(55.0);
        waterButton.setMnemonicParsing(false);
        waterButton.setOnAction(action -> {
            progressBar.setProgress(100);
            setColorProgressBar(100);
            myPlantsTabPaneController.changeLastWateredInDB(plant, java.time.LocalDate.now());
            setColorProgressBar(100);
        });
    }

    /**
     * Method to initiate the update watering frequency-button
     *
     * @param plant to change the nickname on
     */

    private void initUpdateWateringFrequencyButton(Plant plant) {
        this.updateWateringFrequencyButton = new Button("Update watering frequency");
        updateWateringFrequencyButton.setLayoutX(500.0);
        updateWateringFrequencyButton.setLayoutY(55.0);
        updateWateringFrequencyButton.setMnemonicParsing(false);
        updateWateringFrequencyButton.setOnAction(onPress -> {
            updateWateringFrequency(plant);
        });
    }

    /**
     * Method to initiate the info button
     */
    private void initInfoButton() {
        this.infoButton = new Button("Show info");
        infoButton.setLayoutX(150.0);
        infoButton.setLayoutY(55.0);
        infoButton.setMnemonicParsing(false);
        infoButton.setOnAction(onPress -> {
            pressInfoButton();
        });
    }


    /**
     * Method to set off what happens when a user presses the info button
     */
    public void pressInfoButton() {
        infoButton.setDisable(true);
        System.out.println("Pressed info button");
        if (!extended) {
            System.out.println("Not extended");
            if (!gotInfoOnPlant) {
                System.out.println("Not got info on plant");
                PlantDetails plantDetails = myPlantsTabPaneController.getPlantDetails(plant);
                ObservableList<String> plantInfo = FXCollections.observableArrayList();
                plantInfo.add("Last watered: " + plant.getLastWatered());
                plantInfo.add("Current watering frequency: every " + plant.getUsers_watering_frequency() + " days");
                plantInfo.add("Scientific name: " + plantDetails.getScientificName());
                plantInfo.add("Family: " + plantDetails.getFamilyName());
                plantInfo.add("Light: " + plantDetails.getSunlight());
                plantInfo.add("Water: " + plantDetails.getRecommended_watering_frequency());
                plantInfo.add("Description: " + plantDetails.getDescription());
                listViewMoreInfo.setItems(plantInfo);
                System.out.println("Got info on plant + " + plant.getNickname() + "scientific: " + plant.getScientificName() + "family: " + plantDetails.getFamilyName() + "light: " + plantDetails.getSunlight() + "water: " + plantDetails.getRecommended_watering_frequency() + "last watered: " + plant.getLastWatered());
                gotInfoOnPlant = true; // TODO: each plant should only be fetched once
            }
            expand();
        } else {
            collapse();
        }
    }

    /**
     * Method to initiate the change nickname-button
     *
     * @param plant to change the nickname on
     */
    private void initChangeNicknameButton(Plant plant) {
        this.changeNicknameButton = new Button("Change nickname");
        changeNicknameButton.setLayoutX(333.0);
        changeNicknameButton.setLayoutY(250.0);
        changeNicknameButton.setMnemonicParsing(false);
        changeNicknameButton.setOnAction(onPress -> {
            changeNickname(plant);
        });
    }

    /**
     * Method to initiate the change last watered-button
     *
     * @param plant to change the nickname on
     */
    private void initChangeWaterOKButton(Plant plant) {
        this.changeOKWaterButton = new Button("Change");
        changeOKWaterButton.setLayoutX(215.0);
        changeOKWaterButton.setLayoutY(250.0);
        changeOKWaterButton.setMnemonicParsing(false);
        changeOKWaterButton.setOnAction(onPress -> {
            changeDate(plant);
            datePicker.setPromptText("Change last watered");
        });
    }

    /**
     * Method to initiate the change picture-button
     */
    private void initChangePictureButton() {
        this.changePictureButton = new Button("Change picture");
        changePictureButton.setLayoutX(488.0);
        changePictureButton.setLayoutY(250.0);
        changePictureButton.setMnemonicParsing(false);
        changePictureButton.setOnAction(action ->
                myPlantsTabPaneController.setNewPlantPicture(this));
    }

    /**
     * Method to initiate the "date picker"
     */
    private void initDatePicker() {
        this.datePicker = new DatePicker();
        datePicker.setLayoutX(10.0);
        datePicker.setLayoutY(250.0);
        datePicker.setEditable(false);
        datePicker.setPrefWidth(200);
        datePicker.setPromptText("Change last watered");
    }

    /**
     * Method to initialize the delete button
     *
     * @param plant he plant to delete
     */
    private void initDeleteButton(Plant plant) {
        this.deleteButton = new Button("Delete plant");
        deleteButton.setLayoutX(625.0);
        deleteButton.setLayoutY(250.0);
        deleteButton.setMnemonicParsing(false);
        deleteButton.setOnAction(onPress -> {
            removePlant(plant);
        });
    }

    private void initFavoriteButton() {
        this.favoriteButton = new Button();
        favoriteButton.setLayoutX(350.0);
        favoriteButton.setLayoutY(55.0);
        favoriteButton.setMnemonicParsing(false);
        favoriteButton.setOnAction(action -> { myPlantsTabPaneController.updateFavorite(action, plant);});
    }

    private void initImgFavoriteSign(Plant plant) {
        if(plant.getIsFavorite()){
            this.imgFavoriteSign = new ImageView(ImageLibrary.getFullHeart());
        } else {
            this.imgFavoriteSign = new ImageView(ImageLibrary.getEmptyHeart());
        }

        imgFavoriteSign.setFitHeight(16);
        imgFavoriteSign.setFitWidth(15);
        favoriteButton.setGraphic(imgFavoriteSign);
    }

    /**
     * Method to initialize and set the listView with the extended information about the plants
     */
    private void initListView() {
        listViewMoreInfo = new ListView();
        listViewMoreInfo.setLayoutX(this.getWidth() + 10.0);
        listViewMoreInfo.setLayoutY(this.getHeight() + 100.0);
        listViewMoreInfo.setPrefWidth(725.0);
        listViewMoreInfo.setPrefHeight(140.0);
        this.setPrefHeight(92.0);
        this.getChildren().addAll(image, nickname, daysUntilWaterlbl, progressBar, waterButton, infoButton, updateWateringFrequencyButton, favoriteButton);
    }

    /**
     * Method for expanding tab with "more information"-buttons.
     */
    public void expand() {
        AtomicReference<Double> height = new AtomicReference<>(this.getHeight());
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(8.5), event -> this.setPrefHeight(height.updateAndGet(v -> (double) (v + 6.25))))
        );
        timeline.setCycleCount(32);
        timeline.play();
        timeline.setOnFinished(action -> {
            infoButton.setDisable(false);
            this.setPrefHeight(292.0);
            this.getChildren().addAll(listViewMoreInfo, changeNicknameButton, changePictureButton, deleteButton, datePicker, changeOKWaterButton);
        });
        extended = true;
    }

    /**
     * Method for hiding tab with "more information"-buttons.
     */
    public void collapse() {
        AtomicReference<Double> height = new AtomicReference<>(this.getHeight());
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(7.5), event -> this.setPrefHeight(height.updateAndGet(v -> (double) (v - 6.25))))
        );
        timeline.setCycleCount(32);
        timeline.play();
        this.getChildren().removeAll(listViewMoreInfo, changeNicknameButton, changePictureButton, deleteButton, datePicker, changeOKWaterButton, lastWateredLabel);
        timeline.setOnFinished(action -> {
            infoButton.setDisable(false);
            this.setPrefHeight(92.0);
        });
        extended = false;
    }

    /**
     * Changes the colour of the progress bar
     *
     * @param progress How full the progress bar is(0-1.0)
     */
    public void setColorProgressBar(double progress) {
        if (progress < 0.15) {
            progressBar.setStyle("-fx-accent: #BE4052");
        } else {
            progressBar.setStyle("-fx-accent: #2D88AA");
        }
    }

    /**
     * Shows a confirmation box when called,
     * to double check the user really
     * wants to remove the plant
     *
     * @param plant selected plant
     */
    private void removePlant(Plant plant) {
        int answer = MessageBox.askYesNo(BoxTitle.Delete, "The deleted plant can't be restored. Are you sure?");
        if (answer == 1) {
            myPlantsTabPaneController.removePlantFromDB(plant);
        }
    }

    /**
     * Method to change the nickname of a plant
     *
     * @param plant the selected plant
     */
    private void changeNickname(Plant plant) {
        String newNickname = MessageBox.askForStringInput("Change nickname", "New nickname:");

        if (newNickname == null || newNickname.trim().isEmpty()) {
            return;
        }

        if (plant.getNickname().equalsIgnoreCase(newNickname)) {
            MessageBox.display(BoxTitle.Warning, "The new nickname is the same as the current one.");
            return;
        }

        if (myPlantsTabPaneController.changeNicknameInDB(plant, newNickname)) {
            nickname.setText(newNickname);
        }
    }

    /**
     * Method to change the watering frequency of a plant
     *
     * @param plant the selected plant
     */

    private void updateWateringFrequency(Plant plant) {
        int newWateringFrequency = DialogUtils.getValidWateringFrequency();
        if (newWateringFrequency == -1) {
            return;
        }

        if (myPlantsTabPaneController.changeWateringFrequencyInDB(plant, newWateringFrequency)) {
            System.out.println("Watering frequency changed to: " + newWateringFrequency);
            progressBar.setProgress(plant.getProgress());
            setColorProgressBar(plant.getProgress());
        }
    }

    /**
     * Method to change the date of the last watered date
     *
     * @param plant the selected plant
     */
    private void changeDate(Plant plant) {
        LocalDate date = datePicker.getValue();
        if (plant.setLastWatered(date)) {
            progressBar.setProgress(plant.getProgress());
            setColorProgressBar(plant.getProgress());
            myPlantsTabPaneController.changeLastWateredInDB(plant, date);
        } else {
            MessageBox.display(BoxTitle.Error, "Can't water a plant in the future");
        }
    }

    /**
     * Getter method to get the plant
     *
     * @return
     */
    @Override
    public Plant getPlant() {
        return plant;
    }
}
