package se.myhappyplants.client.controller;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import se.myhappyplants.client.model.*;
import se.myhappyplants.client.service.ServerConnection;
import se.myhappyplants.client.view.MessageBox;
import se.myhappyplants.client.view.PopupBox;
import se.myhappyplants.client.view.WishlistPlantPane;
import se.myhappyplants.shared.Message;
import se.myhappyplants.shared.MessageType;
import se.myhappyplants.shared.PictureRandomizer;
import se.myhappyplants.shared.Plant;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

public class WishlistTabPaneController {

    public ListView lstFunFacts;

    private List<Plant> currentUserWishlist;

    @FXML
    private ListView lstViewUserPlantWishlist;

    @FXML
    private MainPaneController mainPaneController;

    @FXML
    public ImageView imgFunFactTitle;

    @FXML
    private Label lblUsername;

    @FXML
    private Circle imgUserAvatar;


    @FXML
    public void initialize() {
        LoggedInUser loggedInUser = LoggedInUser.getInstance();
        lblUsername.setText(loggedInUser.getUser().getUsername());
        imgUserAvatar.setFill(new ImagePattern(new Image(SetAvatar.setAvatarOnLogin(loggedInUser.getUser().getEmail()))));
        showFunFact(loggedInUser.getUser().getFunFactsActivated());
        createCurrentUserWishlistFromDB();
        addCurrentUserWishlistToHomeScreen();
    }

    public void setMainController(MainPaneController mainPaneController) {
        this.mainPaneController = mainPaneController;
    }


    public void showFunFact(boolean factsActivated) {

        FunFacts funFacts = new FunFacts();
        if (factsActivated) {
            imgFunFactTitle.setVisible(true);
            lstFunFacts.setItems(funFacts.getRandomFact());
        } else {
            imgFunFactTitle.setVisible(false);
            lstFunFacts.setItems(null);
        }
    }


    @FXML
    private void logoutButtonPressed() throws IOException {
        LoggedInUser loggedInUser = LoggedInUser.getInstance();
        if (loggedInUser.getUser() != null) {
            mainPaneController.logoutButtonPressed();
        } else {
            StartClient.setRoot(String.valueOf(RootName.loginPane));
        }
    }

    public void updateAvatar() {
        imgUserAvatar.setFill(new ImagePattern(new Image(LoggedInUser.getInstance().getUser().getAvatarURL())));
    }

    @FXML
    public void addPlantToCurrentUserWishlist(Plant selectedPlant) {

        long currentDateMilli = System.currentTimeMillis();
        Date dateAdded = new Date(currentDateMilli);
        String imageURL = PictureRandomizer.getRandomPictureURL();

        Plant plantToAdd = new Plant(selectedPlant.getPlantId(), selectedPlant.getCommonName(), imageURL, dateAdded);
        PopupBox.display(MessageText.sucessfullyAddPlant.toString());
        addPlantToDB(plantToAdd);

    }

    private void addPlantToDB(Plant plantToAdd) {
        Thread addPlantThread = new Thread(() -> {
            currentUserWishlist.add(plantToAdd);
            Message savePlant = new Message(MessageType.savePlantWishlist, LoggedInUser.getInstance().getUser(), plantToAdd);
            ServerConnection connection = ServerConnection.getClientConnection();
            Message response = connection.makeRequest(savePlant);
            if (!response.isSuccess()) {
                Platform.runLater(() -> MessageBox.display(BoxTitle.Failed, "The connection to the server has failed. Check your connection and try again."));
            }
            createCurrentUserWishlistFromDB();
        });
        addPlantThread.start();
    }

    @FXML
    public void createCurrentUserWishlistFromDB() {
        Thread getWishlistThread = new Thread(() -> {
            Message getWishlist = new Message(MessageType.getWishlist, LoggedInUser.getInstance().getUser());
            ServerConnection connection = ServerConnection.getClientConnection();
            Message response = connection.makeRequest(getWishlist);

            if (response.isSuccess()) {
                currentUserWishlist = response.getPlantArray();
                System.out.println("currentUserWishlist: " + currentUserWishlist);
                addCurrentUserWishlistToHomeScreen();
            } else {
                Platform.runLater(() -> MessageBox.display(BoxTitle.Failed, "The connection to the server has failed. Check your connection and try again."));
            }
        });
        getWishlistThread.start();
    }


    @FXML
    private void addCurrentUserWishlistToHomeScreen() {
        ObservableList<WishlistPlantPane> obsListWishlistPlantPane = FXCollections.observableArrayList();
        if (currentUserWishlist == null) {
            obsListWishlistPlantPane.add(new WishlistPlantPane());
        } else {
            if (currentUserWishlist.size()<1) {
                obsListWishlistPlantPane.add(new WishlistPlantPane(this));
            } else {
                for (Plant plant : currentUserWishlist) {
                    obsListWishlistPlantPane.add(new WishlistPlantPane(this, plant));
                }
            }
        }
        Platform.runLater(() -> {
            lstViewUserPlantWishlist.setItems(obsListWishlistPlantPane);
        });
    }

    public MainPaneController getMainPaneController() {
        return mainPaneController;
    }
}
