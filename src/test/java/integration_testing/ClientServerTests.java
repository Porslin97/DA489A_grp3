package integration_testing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.myhappyplants.client.view.ServerConnection;
import se.myhappyplants.server.controller.ResponseController;
import se.myhappyplants.server.services.*;
import se.myhappyplants.shared.*;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Integration tests for the client-server communication.
 */

public class ClientServerTests {
    private UserRepository userRepository;
    private UserPlantRepository userPlantRepository;
    private PlantApiService plantApiServiceSpy;
    private DBQueryExecutor dbQueryExecutor;
    Server server;
    private static final int TEST_PORT = 2556;
    private ServerConnection clientConnection;


    /**
     * Sets up the test environment by creating a new server and client connection, and clearing the database.
     * Uses a spy to mock the PlantApiService so that the actual API is not called during testing.
     */
    @BeforeEach
    void setUp() throws SQLException, UnknownHostException {
        dbQueryExecutor = new DBQueryExecutor();
        clearDatabase();

        userRepository = new UserRepository(dbQueryExecutor);
        userPlantRepository = new UserPlantRepository(dbQueryExecutor);
        plantApiServiceSpy = spy(new PlantApiService());

        ResponseController responseController = new ResponseController(userRepository, userPlantRepository, plantApiServiceSpy);
        server = new Server(TEST_PORT, responseController);
        clientConnection = ServerConnection.getClientConnection();
        clientConnection.setPort(TEST_PORT);
    }

    /**
     * Closes the connection to the database, shuts down the server, and clears the database after each test.
     */
    @AfterEach
    void tearDown() throws SQLException {
        try {
            clearDatabase();
        } finally {
            if (server != null) {
                server.shutdown();
            }
            if (dbQueryExecutor != null) {
                dbQueryExecutor.closeConnection();
            }
        }
    }

    private void clearDatabase() throws SQLException {
        try (Statement stmt = dbQueryExecutor.getConnection().createStatement()) {
            stmt.execute("DELETE FROM user_plants;");
            stmt.execute("DELETE FROM users;");
            stmt.execute("ALTER SEQUENCE users_id_seq RESTART WITH 1;");
            stmt.execute("ALTER SEQUENCE plants_id_seq RESTART WITH 1;");
        }
    }


    @Test
    void shouldSuccessfullyRegisterNewUser() {
        String email = "test@mail.com";
        String username = "TestRegister";
        String rawPassword = "password123";

        Message registerRequest = new Message(MessageType.register, new User(email, username, rawPassword, true)); //notifcationActivated true by default in saveUser method
        Message registerResponse = clientConnection.makeRequest(registerRequest);

        assertNotNull(registerResponse);
        assertTrue(registerResponse.isSuccess(), "Register should succeed when email is unique");

        boolean loginResult = userRepository.checkLogin(email, rawPassword);
        assertTrue(loginResult, "The new user should be able to log in with the registered password");
    }

    @Test
    void shouldAuthenticateRegisteredUser() {
        String email = "test@mail.com";
        String username = "TestLogin";
        String rawPassword = "password123";
        userRepository.saveUser(new User(email, username, rawPassword, true));

        Message loginRequest = new Message(MessageType.login, new User(email, rawPassword));
        Message loginResponse = clientConnection.makeRequest(loginRequest);

        assertNotNull(loginResponse);
        assertTrue(loginResponse.isSuccess(), "Login should succeed when password is correct");
    }

    @Test
    void shouldSuccessfullyDeleteUserAccount() {
        String email = "test@mail.com";
        String username = "TestDelete";
        String rawPassword = "password123";
        userRepository.saveUser(new User(email, username, rawPassword, true));

        Message deleteAccountRequest = new Message(MessageType.deleteAccount, new User(email, rawPassword));
        Message deleteAccountResponse = clientConnection.makeRequest(deleteAccountRequest);

        assertNotNull(deleteAccountResponse);
        assertTrue(deleteAccountResponse.isSuccess(), "Delete account should succeed when password is correct");
    }

    @Test
    void shouldSuccessfullySavePlantToUserLibrary() {
        String email = "test@mail.com";
        String username = "TestSavePlant";
        String rawPassword = "password123";
        userRepository.saveUser(new User(email, username, rawPassword, true));

        Plant plant = new Plant("1", "TestPlant", "TestPlant", "TestPlant.jpg");
        plant.setUsers_watering_frequency(5);
        plant.setLastWatered(LocalDate.now());

        User user = userRepository.getUserDetails(email);

        Message savePlantRequest = new Message(MessageType.savePlant, user, plant);
        Message savePlantResponse = clientConnection.makeRequest(savePlantRequest);

        assertNotNull(savePlantResponse);
        assertTrue(savePlantResponse.isSuccess(), "Save plant should succeed when valid user");
    }

    @Test
    void shouldSuccessfullyDeletePlantFromUserLibrary() {
        String email = "test@mail.com";
        String username = "TestDeletePlant";
        String rawPassword = "password123";
        userRepository.saveUser(new User(email, username, rawPassword, true));

        Plant plant = new Plant("1", "TestPlant", "TestPlant", "TestPlant.jpg");
        plant.setNickname("TestPlantNickname");
        plant.setUsers_watering_frequency(5);
        plant.setLastWatered(LocalDate.now());

        User user = userRepository.getUserDetails(email);

        userPlantRepository.savePlant(user, plant);

        Message deletePlantRequest = new Message(MessageType.deletePlant, user, plant);
        Message deletePlantResponse = clientConnection.makeRequest(deletePlantRequest);

        assertNotNull(deletePlantResponse);
        assertTrue(deletePlantResponse.isSuccess(), "Delete plant should succeed when plant exists in user's library");
    }

    @Test
    void shouldSuccessfullyRetrieveUserPlantLibrary() { // TODO: issue with is_favorite it seems. Not present in testing database
        String email = "test@email.com";
        String username = "TestGetLibrary";
        String rawPassword = "password123";
        userRepository.saveUser(new User(email, username, rawPassword, true));

        Plant plant = new Plant("1", "TestPlant", "TestPlant", "TestPlant.jpg");
        plant.setNickname("TestPlantNickname");
        plant.setUsers_watering_frequency(5);
        plant.setLastWatered(LocalDate.now());

        Plant plant2 = new Plant("2", "TestPlant2", "TestPlant2", "TestPlant2.jpg");
        plant2.setNickname("TestPlantNickname2");
        plant2.setUsers_watering_frequency(10);
        plant2.setLastWatered(LocalDate.now());

        User user = userRepository.getUserDetails(email);

        userPlantRepository.savePlant(user, plant);
        userPlantRepository.savePlant(user, plant2);

        Message getUsersLibraryRequest = new Message(MessageType.getLibrary, user);
        Message getUsersLibraryResponse = clientConnection.makeRequest(getUsersLibraryRequest);

        assertNotNull(getUsersLibraryResponse);
        assertTrue(getUsersLibraryResponse.isSuccess(), "Get users library should succeed when user is logged in");

        List<Plant> userLibrary = getUsersLibraryResponse.getPlantArray();
        assertNotNull(userLibrary);
        assertEquals(2, userLibrary.size(), "User's library should contain 2 plants");
    }

    @Test
    void shouldSuccessfullyChangeUserPlantNickname() {
        String email = "test@mail.com";
        String username = "TestChangeNickname";
        String rawPassword = "password123";
        userRepository.saveUser(new User(email, username, rawPassword, true));

        Plant plant = new Plant("1", "TestPlant", "TestPlant", "TestPlant.jpg");
        plant.setNickname("OldNickname");
        plant.setUsers_watering_frequency(5);
        plant.setLastWatered(LocalDate.now());

        User user = userRepository.getUserDetails(email);
        userPlantRepository.savePlant(user, plant);

        String newNickname = "NewNickname";
        Message changeNicknameRequest = new Message(MessageType.changeNickname, user, plant, newNickname);
        Message changeNicknameResponse = clientConnection.makeRequest(changeNicknameRequest);

        assertNotNull(changeNicknameResponse);
        assertTrue(changeNicknameResponse.isSuccess(), "Change nickname should succeed when plant exists in user's library");

        Plant updatedPlant = userPlantRepository.getPlant(user, newNickname);
        assertNotNull(updatedPlant, "Plant should exist in the database after update");
        assertEquals(newNickname, updatedPlant.getNickname(), "Plant nickname should be updated in the database");
    }

    @Test
    void shouldSuccessfullyUpdatePlantLastWateredDate() {
        String email = "test@mail.com";
        String username = "TestChangeLastWatered";
        String rawPassword = "password123";
        userRepository.saveUser(new User(email, username, rawPassword, true));

        Plant plant = new Plant("1", "TestPlant", "TestPlant", "TestPlant.jpg");
        plant.setLastWatered(LocalDate.now().minusDays(1));
        plant.setUsers_watering_frequency(5);
        plant.setNickname("TestPlantNickname");

        User user = userRepository.getUserDetails(email);
        userPlantRepository.savePlant(user, plant);

        LocalDate newLastWateredDate = LocalDate.now();
        Message changeLastWateredRequest = new Message(MessageType.changeLastWatered, user, plant, newLastWateredDate);
        Message changeLastWateredResponse = clientConnection.makeRequest(changeLastWateredRequest);

        assertNotNull(changeLastWateredResponse);
        assertTrue(changeLastWateredResponse.isSuccess(), "Change last watered date should succeed when plant exists in user's library");

        Plant updatedPlant = userPlantRepository.getPlant(user, plant.getNickname());
        assertNotNull(updatedPlant, "Plant should exist in database after update");

        assertEquals(newLastWateredDate, updatedPlant.getLastWatered().toLocalDate(), "The last watered date should be updated");
    }

    @Test
    void shouldSuccessfullyUpdatePlantPicture() {
        String email = "test@mail.com";
        String username = "TestUpdatePicture";
        String rawPassword = "password123";
        userRepository.saveUser(new User(email, username, rawPassword, true));

        Plant plant = new Plant("1", "TestPlant", "TestPlant", "TestPlant.jpg");
        plant.setNickname("TestPlantNickname");
        plant.setUsers_watering_frequency(5);
        plant.setLastWatered(LocalDate.now());

        User user = userRepository.getUserDetails(email);
        userPlantRepository.savePlant(user, plant);

        String newPictureUrl = "http://example.com/newTestPlant.jpg";
        plant.setImageURL(newPictureUrl);
        Message updatePictureRequest = new Message(MessageType.changePlantPicture, user, plant);
        Message updatePictureResponse = clientConnection.makeRequest(updatePictureRequest);

        assertNotNull(updatePictureResponse);
        assertTrue(updatePictureResponse.isSuccess(), "Update picture should succeed when plant exists in user's library");

        Plant updatedPlant = userPlantRepository.getPlant(user, plant.getNickname());
        assertNotNull(updatedPlant, "Plant should exist in the database after update");
        assertEquals(newPictureUrl, updatedPlant.getImageURL(), "Plant picture URL should be updated in the database");
    }

    @Test
    void shouldSuccessfullyUpdatePlantWateringFrequency() {
        String email = "test@mail.com";
        String username = "TestUpdateWateringFreq";
        String rawPassword = "password123";
        userRepository.saveUser(new User(email, username, rawPassword, true));

        Plant plant = new Plant("1", "TestPlant", "TestPlant", "TestPlant.jpg");
        plant.setNickname("TestPlantNickname");
        plant.setUsers_watering_frequency(5);
        plant.setLastWatered(LocalDate.now());

        User user = userRepository.getUserDetails(email);
        userPlantRepository.savePlant(user, plant);

        int newWateringFrequency = 10;
        Message updateWateringFrequencyRequest = new Message(MessageType.changeWateringFrequency, user, plant, newWateringFrequency);
        Message updateWateringFrequencyResponse = clientConnection.makeRequest(updateWateringFrequencyRequest);

        assertNotNull(updateWateringFrequencyResponse);
        assertTrue(updateWateringFrequencyResponse.isSuccess(), "Update watering frequency should succeed when plant exists in user's library");

        Plant updatedPlant = userPlantRepository.getPlant(user, plant.getNickname());
        assertNotNull(updatedPlant, "Plant should exist in the database after update");
        assertEquals(newWateringFrequency, updatedPlant.getUsers_watering_frequency(), "Plant watering frequency should be updated in the database");
    }

    @Test
    void shouldSuccessfullyMarkAllPlantsAsWatered() {
        String email = "test@mail.com";
        String username = "TestMarkAllWatered";
        String rawPassword = "password123";
        userRepository.saveUser(new User(email, username, rawPassword, true));

        Plant plant1 = new Plant("1", "TestPlant1", "TestPlant1", "TestPlant1.jpg");
        plant1.setLastWatered(LocalDate.now().minusDays(1));
        plant1.setUsers_watering_frequency(5);
        plant1.setNickname("TestPlant1Nickname");

        Plant plant2 = new Plant("2", "TestPlant2", "TestPlant2", "TestPlant2.jpg");
        plant2.setLastWatered(LocalDate.now().minusDays(2));
        plant2.setUsers_watering_frequency(7);
        plant2.setNickname("TestPlant2Nickname");

        User user = userRepository.getUserDetails(email);
        userPlantRepository.savePlant(user, plant1);
        userPlantRepository.savePlant(user, plant2);

        Message markAllWateredRequest = new Message(MessageType.changeAllToWatered, user);
        Message markAllWateredResponse = clientConnection.makeRequest(markAllWateredRequest);

        assertNotNull(markAllWateredResponse);
        assertTrue(markAllWateredResponse.isSuccess(), "Mark all plants as watered should succeed when plants exists in library");

        Plant updatedPlant1 = userPlantRepository.getPlant(user, plant1.getNickname());
        Plant updatedPlant2 = userPlantRepository.getPlant(user, plant2.getNickname());

        assertNotNull(updatedPlant1, "The updated TestPlant1 should exist in the database");
        assertNotNull(updatedPlant2, "The updated TestPlant2 should exist in the database");
        assertEquals(LocalDate.now(), updatedPlant1.getLastWatered().toLocalDate(), "Plant1 last watered date should be updated");
        assertEquals(LocalDate.now(), updatedPlant2.getLastWatered().toLocalDate(), "Plant2 last watered date should be updated");
    }

    @Test
    void shouldSuccessfullyToggleUserNotifications() {
        String email = "test@mail.com";
        String username = "TestToggleNotifications";
        String rawPassword = "password123";
        userRepository.saveUser(new User(email, username, rawPassword, true)); // notifications are activated by default

        User user = userRepository.getUserDetails(email);
        boolean initialPreference = user.areNotificationsActivated();

        Message toggleNotificationsRequest = new Message(MessageType.changeNotifications, !initialPreference, user);
        Message toggleNotificationsResponse = clientConnection.makeRequest(toggleNotificationsRequest);

        assertNotNull(toggleNotificationsResponse);
        assertTrue(toggleNotificationsResponse.isSuccess(), "Toggle notifications preference should succeed when user is valid");

        User updatedUser = userRepository.getUserDetails(email);
        assertNotNull(updatedUser, "User should exist after notifications preference has been updated");
        assertNotEquals(initialPreference, updatedUser.areNotificationsActivated(), "Notifications preference should be updated");
    }

    @Test
    void shouldSuccessfullyToggleFunFactsPreference() {
        String email = "test@mail.com";
        String username = "TestToggleFunFacts";
        String rawPassword = "password123";
        userRepository.saveUser(new User(email, username, rawPassword, true)); // fun facts are activated by default

        User user = userRepository.getUserDetails(email);
        boolean initialPreference = user.getFunFactsActivated();

        Message toggleFunFactsRequest = new Message(MessageType.changeFunFacts, !initialPreference, user);
        Message toggleFunFactsResponse = clientConnection.makeRequest(toggleFunFactsRequest);

        assertNotNull(toggleFunFactsResponse);
        assertTrue(toggleFunFactsResponse.isSuccess(), "Toggle fun facts preference should succeed when user is valid");

        User updatedUser = userRepository.getUserDetails(email);
        assertNotNull(updatedUser, "User should exist after fun facts preference have been updated");
        assertNotEquals(initialPreference, updatedUser.getFunFactsActivated(), "Fun facts preference should be updated");
    }


    /* TODO: fix this test when wishlist functionality is complete and correctly implemented
    @Test
    void shouldSuccessfullyRetrieveUserWishlist() {
        String email = "test@mail.com";
        String username = "TestGetWishlist";
        String rawPassword = "password123";
        userRepository.saveUser(new User(email, username, rawPassword, true));

        Plant plant = new Plant("1", "TestPlant", "TestPlant", "TestPlant.jpg");
        plant.setNickname("TestPlantNickname");
        plant.setLastWatered(LocalDate.now().minusDays(2));
        plant.setUsers_watering_frequency(7);

        User user = userRepository.getUserDetails(email);

        userPlantRepository.saveWishlistPlant(user, plant);

        Message getWishlistRequest = new Message(MessageType.getWishlist, user);
        Message getWishlistResponse = clientConnection.makeRequest(getWishlistRequest);

        assertNotNull(getWishlistResponse, "Get wishlist response should not be null");
        assertTrue(getWishlistResponse.isSuccess(), "Get wishlist should succeed when user is valid");

        List<Plant> wishlist = getWishlistResponse.getPlantArray();
        assertNotNull(wishlist, "Wishlist should not be null");
        assertEquals(1, wishlist.size(), "Wishlist should contain 1 plant");
    }
    */


    @Test
    void shouldSuccessfullyGetMorePlantInfo() {
        Plant plant = new Plant("123", "TestPlant", "TestPlant", "TestPlant.jpg");

        PlantDetails mockPlantDetails = new PlantDetails(
                "MockedFamily",
                "MockedDescription",
                "Average",
                List.of("Full Sun", "Partial Shade"),
                "MockedPlantus Scientificus"
        );
        doReturn(mockPlantDetails).when(plantApiServiceSpy).getPlantDetails(any(Plant.class));

        Message getMorePlantInfoRequest = new Message(MessageType.getMorePlantInfo, plant);
        Message getMorePlantInfoResponse = clientConnection.makeRequest(getMorePlantInfoRequest);

        assertNotNull(getMorePlantInfoResponse, "Get more plant info response should not be null");
        assertTrue(getMorePlantInfoResponse.isSuccess(), "Get more plant info should succeed when plant is valid");

        PlantDetails plantDetails = getMorePlantInfoResponse.getPlantDetails();
        System.out.println(plantDetails);
        assertNotNull(plantDetails, "Plant details should not be null");
        assertEquals("MockedFamily", plantDetails.getFamilyName(), "Family name should be correct");
        assertEquals("MockedDescription", plantDetails.getDescription(), "Description should be correct");
        assertEquals("Average", plantDetails.getRecommended_watering_frequency(), "Recommended watering frequency should be correct");
        assertEquals(List.of("Full Sun", "Partial Shade"), plantDetails.getSunlight(), "Sunlight should be correct");
        assertEquals("MockedPlantus Scientificus", plantDetails.getScientificName(), "Scientific name should be correct");
    }

    @Test
    void shouldSuccessfullySearchForPlants() {
        String query = "Monstera";
        Optional<List<Plant>> mockPlants = Optional.of(List.of(
                new Plant("1", "Monstera", "Monstera deliciosa", "Monstera.jpg"),
                new Plant("2", "Monstera", "Monstera adansonii", "Monstera2.jpg")
        ));
        doReturn(mockPlants).when(plantApiServiceSpy).getPlants(query, SortingOption.COMMON_NAME);

        Message searchRequest = new Message(MessageType.search, query, SortingOption.COMMON_NAME);
        Message searchResponse = clientConnection.makeRequest(searchRequest);

        assertNotNull(searchResponse, "Search response should not be null");
        assertTrue(searchResponse.isSuccess(), "Search should succeed when query is valid");

        List<Plant> searchResults = searchResponse.getPlantArray();
        assertNotNull(searchResults, "Search results should not be null");
        assertEquals(2, searchResults.size(), "Search results should contain 2 plants");
    }

    @Test
    void shouldSuccessfullySavePlantToWishlist() {
        String email = "test@mail.com";
        String username = "TestGetWishlist";
        String rawPassword = "password123";
        userRepository.saveUser(new User(email, username, rawPassword, true));

        Plant plant = new Plant("1", "TestPlant", "TestPlant", "TestPlant.jpg");

        User user = userRepository.getUserDetails(email);

        PlantDetails mockPlantDetails = new PlantDetails(
                "MockedFamily",
                "MockedDescription",
                "Average",
                List.of("Full Sun", "Partial Shade"),
                "MockedPlantus Scientificus"
        );
        doReturn(mockPlantDetails).when(plantApiServiceSpy).getPlantDetails(any(Plant.class));

        Message saveWishlistPlantRequest = new Message(MessageType.savePlantWishlist, user, plant);
        Message saveWishlistPlantResponse = clientConnection.makeRequest(saveWishlistPlantRequest);

        assertNotNull(saveWishlistPlantResponse, "Save wishlist plant response should not be null");
        assertTrue(saveWishlistPlantResponse.isSuccess(), "Save wishlist plant should succeed when user is valid");

        List<Plant> wishlist = userPlantRepository.getUserWishlist(user);
        assertNotNull(wishlist, "Wishlist should not be null");
        assertEquals(1, wishlist.size(), "Wishlist should contain 1 plant");
    }
}

