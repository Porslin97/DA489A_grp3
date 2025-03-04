package se.myhappyplants.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import se.myhappyplants.client.model.BoxTitle;
import se.myhappyplants.client.model.LoggedInUser;
import se.myhappyplants.client.model.RootName;
import se.myhappyplants.client.view.MessageBox;
import se.myhappyplants.client.view.ServerConnection;
import se.myhappyplants.shared.Message;
import se.myhappyplants.shared.MessageType;
import se.myhappyplants.shared.Plant;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controls the inputs from a 'logged in' user and is the mainPane for the GUI
 * Created by: Christopher O'Driscoll, Eric Simonsson
 * Updated by: Linn Borgström, Eric Simonsson, Susanne Vikström, 2021-04-21
 */
public class MainPaneController {

    @FXML
    public TabPane mainPane;
    @FXML
    private MyPlantsTabPaneController myPlantsTabPaneController;
    @FXML
    private SearchTabPaneController searchTabPaneController;
    @FXML
    private SettingsTabPaneController settingsTabPaneController;
    @FXML
    private WishlistTabPaneController wishlistTabPaneController;

    /**
     * Constructor that has access to FXML variables
     */
    @FXML
    public void initialize() {
        myPlantsTabPaneController.setMainController(this);
        searchTabPaneController.setMainController(this);
        settingsTabPaneController.setMainController(this);
        wishlistTabPaneController.setMainController(this);
    }

    /**
     * Getter-method to get the myPlantsTabPaneController
     *
     * @return MyPlantsTabPaneController
     */

    public MyPlantsTabPaneController getMyPlantsTabPaneController() {
        return myPlantsTabPaneController;
    }

    /**
     * Getter-method to get the searchTabPaneController
     *
     * @return searchTabPaneController
     */
    public SearchTabPaneController getSearchTabPaneController() {
        return searchTabPaneController;
    }

    public WishlistTabPaneController getWishlistTabPaneController() {
        return wishlistTabPaneController;
    }

    /**
     * Method to logs out the user and then switches scenes to the loginPane
     *
     * @throws IOException
     */
    @FXML
    public void logoutButtonPressed() throws IOException {
        String email = LoggedInUser.getInstance().getUser().getEmail();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("resources/lastLogin.txt"))) {
            bw.write(email);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LoggedInUser.getInstance().setUser(null);
        StartClient.setRoot(String.valueOf(RootName.loginPane));

    }

    /**
     * Method to update so the user picture is the same on all the tabs
     */
    public void updateAvatarOnAllTabs() {
        myPlantsTabPaneController.updateAvatar();
        searchTabPaneController.updateAvatar();
        settingsTabPaneController.updateAvatar();
        wishlistTabPaneController.updateAvatar();
    }

    /**
     * Method to switch to the tab the user selects
     */
    public void changeToSearchTab() {
        mainPane.getSelectionModel().select(1);
    }

    boolean addPlantToDB(Plant plantToAdd, String database) {
        AtomicBoolean success = new AtomicBoolean(false);
        Thread addPlantThread = new Thread(() ->  {
            Message savePlant = null;
            if(database.equals("wishlist")){
                savePlant = new Message(MessageType.savePlantWishlist, LoggedInUser.getInstance().getUser(), plantToAdd);
            }else if (database.equals("library")){
                savePlant = new Message(MessageType.savePlant, LoggedInUser.getInstance().getUser(), plantToAdd);
            }

            ServerConnection connection = ServerConnection.getClientConnection();
            Message response = connection.makeRequest(savePlant);
            if (!response.isSuccess()) {
                Platform.runLater(() -> MessageBox.display(BoxTitle.Failed, "The connection to the server has failed. Check your connection and try again."));
            }else {
                success.set(true);
            }
        });
        addPlantThread.start();

        try {
            addPlantThread.join(); // Wait for the thread to finish
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return success.get();
    }


    public Iterable<? extends Plant> getCurrentUserLibrary() {
        return myPlantsTabPaneController.getCurrentUserLibrary();
    }

    public void addPlantToUserLibrary(Plant plantToAdd) {
        myPlantsTabPaneController.addPlantToUserLibrary(plantToAdd);
    }
}