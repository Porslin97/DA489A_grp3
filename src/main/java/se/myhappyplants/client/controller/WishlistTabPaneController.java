package se.myhappyplants.client.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import se.myhappyplants.client.model.*;
import se.myhappyplants.client.view.MessageBox;
import se.myhappyplants.shared.Plant;

import java.io.IOException;

public class WishlistTabPaneController {

    public ListView lstFunFacts;

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
        if (loggedInUser.getUser() != null) {
            lblUsername.setText(loggedInUser.getUser().getUsername());
            imgUserAvatar.setFill(new ImagePattern(new Image(SetAvatar.setAvatarOnLogin(loggedInUser.getUser().getEmail()))));
            showFunFact(loggedInUser.getUser().areFunFactsActivated());
        } else {
            lblUsername.setText("Guest");
            String defaultAvatarUrl = "file:resources/images/user_default_img.png";
            imgUserAvatar.setFill(new ImagePattern(new Image(defaultAvatarUrl)));
            MessageBox.display(BoxTitle.Guest,"You will be logged in as a guest. You will only be able to search for plants.");

        }
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
        if(loggedInUser.getUser() != null) {
            mainPaneController.logoutButtonPressed();
        } else {
            StartClient.setRoot(String.valueOf(RootName.loginPane));
        }
    }

    public void updateAvatar() {
        imgUserAvatar.setFill(new ImagePattern(new Image(LoggedInUser.getInstance().getUser().getAvatarURL())));
    }

    public void addPlantToCurrentUserWishlist(Plant plant) {
        
    }
}
