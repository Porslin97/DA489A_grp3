package se.myhappyplants.client.controller;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import se.myhappyplants.client.model.*;
import se.myhappyplants.client.view.ServerConnection;
import se.myhappyplants.client.util.DialogUtils;
import se.myhappyplants.client.view.MessageBox;
import se.myhappyplants.client.view.PopupBox;
import se.myhappyplants.client.view.WishlistPlantPane;
import se.myhappyplants.shared.*;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

public class WishlistTabPaneController {
    @FXML
    public ListView lstFunFacts;
    @FXML
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

    private final String database = "wishlist";


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
        String imageURL = selectedPlant.getImageURL();

        Plant plantToAdd = new Plant(selectedPlant.getPlantId(), selectedPlant.getCommonName(), imageURL, dateAdded);
        PopupBox.display(MessageText.successfullyAddWishlistPlant.toString());
        addPlantToDB(plantToAdd);
    }

    private void addPlantToDB(Plant plantToAdd) {
        boolean success = mainPaneController.addPlantToDB(plantToAdd, database);

        if (success) {
            currentUserWishlist.add(plantToAdd);
            addCurrentUserWishlistToHomeScreen();
        }
    }

    @FXML
    public void removePlantFromCurrentUserWishlist(Plant selectedPlant, ActionEvent action) {
        Plant plantToRemove = new Plant(selectedPlant.getCommonName(), selectedPlant.getPlantId(), null);
        removePlantFromDB(plantToRemove);
        currentUserWishlist.remove(selectedPlant);
        Parent listItemToRemove = ((Button) action.getSource()).getParent();
        lstViewUserPlantWishlist.getItems().remove(listItemToRemove);
        System.out.println(lstViewUserPlantWishlist.getItems());
        lstViewUserPlantWishlist.refresh();
    }

    @FXML
    public void removePlantFromDB(Plant plant) {
        Platform.runLater(() -> PopupBox.display(MessageText.removeWishlistPlant.toString()));
        Thread removePlantThread = new Thread(() -> {
            currentUserWishlist.remove(plant);
            Message deletePlant = new Message(MessageType.removePlantWishlist, LoggedInUser.getInstance().getUser(), plant);
            ServerConnection connection = ServerConnection.getClientConnection();
            Message response = connection.makeRequest(deletePlant);

            if (!response.isSuccess()) {
                Platform.runLater(() -> MessageBox.display(BoxTitle.Failed, "The connection to the server has failed. Check your connection and try again."));
            }
        });
        removePlantThread.start();
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
            if (currentUserWishlist.size() < 1) {
                obsListWishlistPlantPane.add(new WishlistPlantPane(this));
            } else {
                for (Plant plant : currentUserWishlist) {
                    obsListWishlistPlantPane.add(new WishlistPlantPane(this, ImageLibrary.getLoadingImage(), plant));
                }
            }
        }
        Platform.runLater(() -> {
            lstViewUserPlantWishlist.setItems(obsListWishlistPlantPane);
        });

        Task getImagesTask =
                new Task() {
                    @Override
                    protected Object call() {
                        long i = 1;
                        for (WishlistPlantPane wishlistPlantPane : obsListWishlistPlantPane) {
                            Plant Plant = wishlistPlantPane.getPlant();
                            if (Plant.getImageURL().equals("")) {
                                wishlistPlantPane.setDefaultImage(ImageLibrary.getDefaultPlantImage().getUrl());
                            } else {
                                try {
                                    wishlistPlantPane.updateImage();
                                } catch (IllegalArgumentException e) {
                                    wishlistPlantPane.setDefaultImage(ImageLibrary.getDefaultPlantImage().getUrl());
                                }
                            }
                            updateProgress(i++, obsListWishlistPlantPane.size());
                        }
                        return true;
                    }
                };
        Thread imageThread = new Thread(getImagesTask);
        imageThread.start();
    }

    public MainPaneController getMainPaneController() {
        return mainPaneController;
    }

    @FXML
    public void addPlantToCurrentUserLibrary(Plant plantAdd, ActionEvent action) {
        if (!isUserLoggedIn()) {
            return;
        }

        String plantNickname = getPlantNickname(plantAdd);
        if (plantNickname == null) {
            return;
        }

        int newWateringFrequency = DialogUtils.getValidWateringFrequency();
        if (newWateringFrequency == -1) {
            return;
        }

        int plantsWithThisNickname = 1;
        String uniqueNickName = plantNickname;
        for (Plant plant : mainPaneController.getCurrentUserLibrary()) {
            if (plant.getNickname().equals(uniqueNickName)) {
                plantsWithThisNickname++;
                uniqueNickName = plantNickname + plantsWithThisNickname;
            }
        }
        long currentDateMilli = System.currentTimeMillis();
        Date date = new Date(currentDateMilli);
        String imageURL = plantAdd.getImageURL();
        Plant plantToAdd = new Plant(uniqueNickName, plantAdd.getPlantId(), date, newWateringFrequency, imageURL);
        PopupBox.display(MessageText.successfullyAddPlant.toString());
        boolean success = mainPaneController.addPlantToDB(plantToAdd, database);
        if (success){
            mainPaneController.addPlantToUserLibrary(plantToAdd);
            removePlantFromCurrentUserWishlist(plantToAdd, action);
        }
    }

    private boolean isUserLoggedIn() {
        LoggedInUser loggedInUser = LoggedInUser.getInstance();
        if (loggedInUser.getUser() == null) {
            MessageBox.display(BoxTitle.Guest, "You need to be logged in to add a plant to your library.");
            return false;
        }
        return true;
    }

    private String getPlantNickname(Plant plantAdd) {
        String plantNickname = plantAdd.getCommonName();
        int answer = MessageBox.askYesNo(BoxTitle.Add, "Do you want to add a nickname for your plant?");

        if (answer == 1) {
            while (true) {
                String nicknameInput = MessageBox.askForStringInput("Add a nickname", "Nickname:");
                if (nicknameInput == null) {
                    return null;
                }
                nicknameInput = nicknameInput.trim();
                if (nicknameInput.isEmpty()) {
                    MessageBox.display(BoxTitle.Error, "Nickname cannot be empty. Please enter a valid nickname.");
                    continue;
                }
                return nicknameInput;
            }
        } else if (answer == -1) {
            return null;
        }
        return plantNickname;
    }

    public PlantDetails getPlantDetails(Plant plant) {
        PopupBox.display(MessageText.holdOnGettingInfo.toString());
        PlantDetails plantDetails = null;
        Message getInfoSearchedPlant = new Message(MessageType.getMorePlantInfo, plant);
        ServerConnection connection = ServerConnection.getClientConnection();
        Message response = connection.makeRequest(getInfoSearchedPlant);
        if (response != null) {
            plantDetails = response.getPlantDetails();
        }
        return plantDetails;
    }
}
