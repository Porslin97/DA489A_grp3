package se.myhappyplants.client.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import se.myhappyplants.client.model.BoxTitle;
import se.myhappyplants.client.model.LoggedInUser;
import se.myhappyplants.client.model.RootName;
import se.myhappyplants.client.model.Verifier;
import se.myhappyplants.client.view.ServerConnection;
import se.myhappyplants.client.view.MessageBox;
import se.myhappyplants.client.view.PopupBox;
import se.myhappyplants.shared.Message;
import se.myhappyplants.shared.MessageType;
import se.myhappyplants.shared.User;

import javax.xml.validation.Validator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Controls the inputs from a user that hasn't logged in
 * Created by: Eric Simonsson, Christopher O'Driscoll
 * Updated by: Linn Borgström, 2021-05-13
 */
public class LoginPaneController {

    @FXML
    public Hyperlink registerLink;
    @FXML
    private TextField txtFldEmail;
    @FXML
    private PasswordField passFldPassword;


    /**
     * Switches to 'logged in' scene
     *
     * @throws IOException
     */
    @FXML
    public void initialize() throws IOException {
        String lastLoggedInUser;

        File file = new File("resources/lastLogin.txt");
        if (!file.exists()) {
            file.createNewFile();

        }
        else if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader("resources/lastLogin.txt"));) {
                lastLoggedInUser = br.readLine();
                txtFldEmail.setText(lastLoggedInUser);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        txtFldEmail.toFront();
    }

    /**
     * Method which tries to log in user. If it's successful, it changes scene
     *
     * @throws IOException
     */
    @FXML
    private void loginButtonPressed() {
        String email = txtFldEmail.getText();
        String password = passFldPassword.getText();

        if (email.isEmpty()) {
            Platform.runLater(() -> MessageBox.display(BoxTitle.Error, "Please enter your email address or username"));
            return;
        }

        /* disable email validation for now since users should be able to log in with username
        if (!Verifier.validateEmail(email)) {
            Platform.runLater(() -> MessageBox.display(BoxTitle.Error, "Please enter your email address in format: yourname@example.com"));
            return;
        } */

        if (password.isEmpty()) {
            Platform.runLater(() -> MessageBox.display(BoxTitle.Error, "Please enter your password"));
            return;
        }

        Thread loginThread = new Thread(() -> {
            Message loginMessage = new Message(MessageType.login, new User(txtFldEmail.getText(), passFldPassword.getText()));
            ServerConnection connection = ServerConnection.getClientConnection();
            Message loginResponse = connection.makeRequest(loginMessage);

            if (loginResponse != null) {
                if (loginResponse.isSuccess()) {
                    LoggedInUser.getInstance().setUser(loginResponse.getUser());
                    Platform.runLater(() -> new PopupBox("Now logged in as\n" + LoggedInUser.getInstance().getUser().getUsername()));
                    try {
                        switchToMainPane();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Platform.runLater(() -> MessageBox.display(BoxTitle.Failed, "Sorry, we couldn't find an account with that email or you typed the password wrong. Try again or create a new account."));

                }
            }
            else {
                Platform.runLater(() -> MessageBox.display(BoxTitle.Failed, "The connection to the server has failed. Check your connection and try again."));
            }
        });
        loginThread.start();
    }

    /**
     * Method to switch to the mainPane FXML
     *
     * @throws IOException
     */
    @FXML
    private void switchToMainPane() throws IOException {
        StartClient.setRoot(String.valueOf(RootName.mainPane));
    }

    /**
     * Method to switch to the registerPane
     *
     * @param actionEvent
     */
    public void swapToRegister(ActionEvent actionEvent) {
        try {
            StartClient.setRoot(RootName.registerPane.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to switch to the searchTabPane
     *
     * @param actionEvent
     * @throws IOException
     */
    public void guestButtonPressed(ActionEvent actionEvent) throws IOException {
        StartClient.setRoot(RootName.searchTabPane.toString());
    }
}
