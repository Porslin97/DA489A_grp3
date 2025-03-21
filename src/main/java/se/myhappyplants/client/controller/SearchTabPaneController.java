package se.myhappyplants.client.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import se.myhappyplants.client.model.*;
import se.myhappyplants.client.view.ServerConnection;
import se.myhappyplants.client.util.DialogUtils;
import se.myhappyplants.client.model.AutocompleteSearchField;
import se.myhappyplants.client.view.MessageBox;
import se.myhappyplants.client.view.PopupBox;
import se.myhappyplants.client.view.SearchPlantPane;
import se.myhappyplants.shared.*;
import se.myhappyplants.client.model.SetAvatar;

import java.io.IOException;
import java.sql.Date;
import java.util.List;

/**
 * Class that controls the logic of the "search"-tab
 * Created by: Christopher O'Driscoll
 * Updated by: Christopher O'Driscoll, 2021-05-14
 */

public class SearchTabPaneController {
    @FXML
    public ListView lstFunFacts;
    @FXML
    private MainPaneController mainPaneController;
    @FXML
    private Circle imgUserAvatar;
    @FXML
    private Label lblUsername;
    @FXML
    private Button btnSearch;
    @FXML
    private AutocompleteSearchField txtFldSearchText;
    @FXML
    private ComboBox<SortingOption> cmbSortOption;
    @FXML
    private ListView listViewResult;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    public ImageView imgFunFactTitle;
    @FXML
    public TextField txtNbrOfResults;

    private List<Plant> searchResults;

    private final String database = "library";

    /**
     * Method to initialize the GUI
     *
     * @throws IOException
     */
    @FXML
    public void initialize() {
        LoggedInUser loggedInUser = LoggedInUser.getInstance();
        if (loggedInUser.getUser() != null) {
            lblUsername.setText(loggedInUser.getUser().getUsername());
            imgUserAvatar.setFill(new ImagePattern(new Image(SetAvatar.setAvatarOnLogin(loggedInUser.getUser().getEmail()))));
            showFunFact(loggedInUser.getUser().getFunFactsActivated());
        } else {
            lblUsername.setText("Guest");
            String defaultAvatarUrl = "file:resources/images/user_default_img.png";
            imgUserAvatar.setFill(new ImagePattern(new Image(defaultAvatarUrl)));
            MessageBox.display(BoxTitle.Guest, "You will be logged in as a guest. You will only be able to search for plants.");
        }
        cmbSortOption.setValue(SortingOption.COMMON_NAME); // set default sorting value
        cmbSortOption.setItems(ListSorter.sortOptionsSearch());
    }

    /**
     * Method to message the right controller-class that the log out-button has been pressed
     *
     * @throws IOException
     */
    public void setMainController(MainPaneController mainPaneController) {
        this.mainPaneController = mainPaneController;
    }

    /**
     * Method to set and display the fun facts
     *
     * @param factsActivated boolean, if the user has activated the option to true
     */
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

    /**
     * Method to add a plant to the logged in users library. Asks the user if it wants to add a nickname and watering frequency
     *
     * @param plantAdd the selected plant to add
     */
    @FXML
    public void addPlantToCurrentUserLibrary(Plant plantAdd) {
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
        new PopupBox(MessageText.successfullyAddPlant.toString());
        boolean success = mainPaneController.addPlantToDB(plantToAdd, database);
        if (success){
            mainPaneController.addPlantToUserLibrary(plantToAdd);
        }
    }

    /**
     * Method to add a plant to the logged in users wishlist. Asks the user if it wants to add a nickname
     *
     * @param plantAdd the selected plant to add
     */
    @FXML
    public void addPlantToCurrentUserWishlist(Plant plantAdd) {
        if (!isUserLoggedIn()) {
            return;
        }
        mainPaneController.getWishlistTabPaneController().addPlantToCurrentUserWishlist(plantAdd);
    }

    /**
     * Method to check if the user is logged in
     *
     * @return true if the user is logged in
     */
    private boolean isUserLoggedIn() {
        LoggedInUser loggedInUser = LoggedInUser.getInstance();
        if (loggedInUser.getUser() == null) {
            MessageBox.display(BoxTitle.Guest, "You need to be logged in to add a plant to your library.");
            return false;
        }
        return true;
    }

    /**
    * Method to get the plant nickname from the user
    *
    * @param plantAdd the plant to add
    * @return the nickname of the plant
    */
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

    /**
    * Method to show the search result on the pane
    */
    private void showResultsOnPane() {
        ObservableList<SearchPlantPane> searchPlantPanes = FXCollections.observableArrayList();
        for (Plant plant : searchResults) {
            searchPlantPanes.add(new SearchPlantPane(this, ImageLibrary.getLoadingImage(), plant));
        }
        listViewResult.getItems().clear();
        listViewResult.setItems(searchPlantPanes);

        Task getImagesTask =
            new Task() {
                @Override
                protected Object call() {
                    long i = 1;
                    for (SearchPlantPane spp : searchPlantPanes) {
                        Plant Plant = spp.getPlant();
                        if (Plant.getImageURL().equals("")) {
                            spp.setDefaultImage(ImageLibrary.getDefaultPlantImage().getUrl());
                        } else {
                            try {
                                spp.updateImage();
                            } catch (IllegalArgumentException e) {
                                spp.setDefaultImage(ImageLibrary.getDefaultPlantImage().getUrl());
                            }
                        }
                        updateProgress(i++, searchPlantPanes.size());
                    }
                    Text text = (Text) progressIndicator.lookup(".percentage");
                    if (text.getText().equals("90%") || text.getText().equals("Done")) {
                        text.setText("Done");
                        progressIndicator.setPrefWidth(text.getLayoutBounds().getWidth());
                    }
                    return true;
                }
            };
        Thread imageThread = new Thread(getImagesTask);
        progressIndicator.progressProperty().bind(getImagesTask.progressProperty());
        imageThread.start();
    }

    /**
    * Method to sent a message to the server to get the results from the database. Displays a message to the user that more info is on its way
    */
    @FXML
    private void searchButtonPressed() {
        btnSearch.setDisable(true);
        txtFldSearchText.addToHistory();
        new PopupBox(MessageText.holdOnGettingInfo.toString());

        Thread searchThread = new Thread(() -> {
            SortingOption selectedSortingOption = cmbSortOption.getValue();
            Message apiRequest = new Message(MessageType.search, txtFldSearchText.getText(), selectedSortingOption);
            ServerConnection connection = ServerConnection.getClientConnection();
            Message apiResponse = connection.makeRequest(apiRequest);

            if (apiResponse != null && apiResponse.isSuccess()) {
                searchResults = apiResponse.getPlantArray();
                Platform.runLater(() -> txtNbrOfResults.setText(searchResults.size() + " results"));
                if (searchResults.isEmpty()) {
                    progressIndicator.progressProperty().unbind();
                    progressIndicator.setProgress(100);
                    btnSearch.setDisable(false);
                    Platform.runLater(() -> listViewResult.getItems().clear());
                    return;
                }
                Platform.runLater(this::showResultsOnPane);
            } else {
                Platform.runLater(() -> MessageBox.display(BoxTitle.Error, "The connection to the server has failed. Check your connection and try again."));
            }
            btnSearch.setDisable(false);
        });
        searchThread.start();
    }

    /**
    * Method to message the right controller-class that the log out-button has been pressed
    *
    * @throws IOException
    */
    @FXML
    private void logoutButtonPressed() throws IOException {
        LoggedInUser loggedInUser = LoggedInUser.getInstance();
        if (loggedInUser.getUser() != null) {
            mainPaneController.logoutButtonPressed();
        } else {
            StartClient.setRoot(String.valueOf(RootName.loginPane));
        }
    }

    /**
     * Method to get more info on the plant
     * @param plant
     * @return
     */
    public PlantDetails getPlantDetails(Plant plant) {
        new PopupBox(MessageText.holdOnGettingInfo.toString());
        PlantDetails plantDetails = null;
        Message getInfoSearchedPlant = new Message(MessageType.getMorePlantInfo, plant);
        ServerConnection connection = ServerConnection.getClientConnection();
        Message response = connection.makeRequest(getInfoSearchedPlant);
        if (response != null) {
            plantDetails = response.getPlantDetails();
        }
        return plantDetails;
    }

    /**
    * Method to rearranges the results based on selected sorting option
    */
    @FXML
    public void sortResults() {
        System.out.println("Calling sortResults in SearchTabPaneController");
        SortingOption selectedOption;
        selectedOption = cmbSortOption.getValue();
        if (selectedOption == null) {
            selectedOption = SortingOption.COMMON_NAME;
        }
        listViewResult.setItems(ListSorter.sort(selectedOption, listViewResult.getItems()));
    }

    /**
    * Method to update the users avatar picture on the tab
    */
    public void updateAvatar() {
        imgUserAvatar.setFill(new ImagePattern(new Image(LoggedInUser.getInstance().getUser().getAvatarURL())));
    }
}
