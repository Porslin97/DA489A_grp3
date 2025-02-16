package se.myhappyplants.client.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import se.myhappyplants.client.controller.WishlistTabPaneController;
import se.myhappyplants.client.model.PictureRandomizerClient;
import se.myhappyplants.shared.Plant;

import java.io.File;

public class WishlistPlantPane extends Pane implements PlantPane {

    private WishlistTabPaneController wishlistTabPaneController;
    private Plant plant;
    private ImageView image;
    private Label plantName;
    private Label dateAddedLabel;
    private ListView listView;



    public WishlistPlantPane(WishlistTabPaneController wishlistTabPaneController) {
        this.wishlistTabPaneController = wishlistTabPaneController;
        initEmptyWishlistLabel();
    }

    public WishlistPlantPane() {
        File fileImg = new File("resources/images/img.png");
        Image img = new Image(fileImg.toURI().toString());
        image = new ImageView(img);
        image.setFitHeight(45.0);
        image.setFitWidth(45.0);
        image.setLayoutX(50.0);
        image.setLayoutY(14.0);

        plantName = new Label("Your plants are being loaded from the database..");
        plantName.setLayoutX(100);
        plantName.setLayoutY(25);
        plantName.setPrefWidth(300);
        plantName.setAlignment(Pos.CENTER);

        this.getChildren().addAll(image, plantName);
    }

    private void initEmptyWishlistLabel() {
        this.image = new ImageView();
        Image img = PictureRandomizerClient.getRandomPicture();
        initImages(img);
        Label lblEmptyInfo = new Label("Your wishlist is currently empty \nClick here to search for plants to add    --------->");
        lblEmptyInfo.setLayoutX(150.0);
        lblEmptyInfo.setLayoutY(28.0);
        Button btnSearchPlants = new Button("Search for plants");
        btnSearchPlants.setOnAction(action -> wishlistTabPaneController.getMainPaneController().changeToSearchTab());
        btnSearchPlants.setLayoutX(500.0);
        btnSearchPlants.setLayoutY(40.0);
        this.getChildren().addAll(image, lblEmptyInfo, btnSearchPlants);
    }


    public WishlistPlantPane(WishlistTabPaneController wishlistTabPaneController, Plant plant) {
        this.wishlistTabPaneController = wishlistTabPaneController;
        this.plant = plant;
        this.image = new ImageView();

        String imageUrl = plant.getImageURL();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            try {
                Image img = new Image(imageUrl);
                initImages(img);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid URL or resource not found: " + imageUrl);
                Image defaultImg = new Image("file:Blommor/blomma1.png");
                initImages(defaultImg);
            }
        } else {
            Image defaultImg = new Image("file:Blommor/blomma1.png");
            initImages(defaultImg);
        }

        initPlantIdLabel(plant);
        initDateAddedLabel(plant);
        initListView();
    }


    private void initListView() {
        listView = new ListView();
        listView.setLayoutX(this.getWidth() + 10.0);
        listView.setLayoutY(this.getHeight() + 100.0);
        listView.setPrefWidth(725.0);
        listView.setPrefHeight(140.0);
        this.getChildren().addAll(image, plantName, dateAddedLabel);
    }




    private void initImages(Image img) {
        image.setFitHeight(70.0);
        image.setFitWidth(70.0);
        image.setLayoutX(40.0);
        image.setLayoutY(20.0);
        image.setPickOnBounds(true);
        image.setPreserveRatio(true);
        image.setImage(img);
    }

    private void initPlantIdLabel(Plant plant) {
        plantName = new Label();
        plantName.setLayoutX(200);
        plantName.setLayoutY(20);
        plantName.setPrefWidth(200);
        plantName.setAlignment(Pos.CENTER_LEFT);
        plantName.setText("Plant ID: " + plant.getPlantId());
    }

    private void initDateAddedLabel(Plant plant) {
        this.dateAddedLabel = new Label();
        dateAddedLabel.setLayoutY(50);
        dateAddedLabel.setLayoutX(200);
        dateAddedLabel.setText("Date added: " + plant.getDateAdded());
    }

    @Override
    public Plant getPlant() {
        return plant;
    }
}