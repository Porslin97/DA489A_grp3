package se.myhappyplants.server.services;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.myhappyplants.shared.Plant;
import se.myhappyplants.shared.User;

import java.net.UnknownHostException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UserPlantRepositoryTest {

    private UserRepository userRepository;
    private UserPlantRepository userPlantRepository;
    private DBQueryExecutor dbQueryExecutor;


    @BeforeAll
    static void javaFXinitialization() {
        try {
            Platform.startup(() -> {

            });
        } catch (IllegalStateException e) {

        }
    }

    @BeforeEach
    void setUp() throws SQLException, java.net.UnknownHostException {

        dbQueryExecutor = new DBQueryExecutor();
        userRepository = new UserRepository(dbQueryExecutor);
        userPlantRepository = new UserPlantRepository(dbQueryExecutor);

        Statement stmnt = dbQueryExecutor.getConnection().createStatement();
        stmnt.execute("DELETE FROM user_plants");
        stmnt.execute("DELETE FROM users");
        stmnt.execute("ALTER SEQUENCE users_id_seq RESTART WITH 1");
        stmnt.execute("ALTER SEQUENCE plants_id_seq RESTART WITH 1");
    }

    @Test
    void addPlantWithNickname() throws SQLException, java.net.UnknownHostException {
        User testUser = new User(1, "testfall2.1@test.com", "testUser1", true, true);
        boolean userSaved = userRepository.saveUser(testUser);
        assertTrue(userSaved, "user saved correctly");


        User user = userRepository.getUserDetails("testfall2.1@test.com");
        assertNotNull(user, "The user exists in the database");

        Date today = Date.valueOf(LocalDate.now());
        String plantId = "ivy";
        String nickname = "Jan";
        Plant plant = new Plant(nickname, plantId, today);

        boolean plantSaved = userPlantRepository.savePlant(user, plant);
        assertTrue(plantSaved, "The plant has been saved to the library");

        ArrayList<Plant> library = userPlantRepository.getUserLibrary(user);
        boolean found = false;
        for (Plant p : library) {

            if ("Jan".equals(p.getNickname()) && "ivy".equals(p.getPlantId())) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Plant with the nickname 'Jan' and plantId 'ivy' is in the user's library");
    }

    @Test
    void deletePlantFromLibrary() throws SQLException, java.net.UnknownHostException {
        User testUser = new User(3, "testfall2.3@test.com", "testUser3", true, true);
        boolean userSaved = userRepository.saveUser(testUser);
        assertTrue(userSaved, "testuser 3 has been saved");

        User user = userRepository.getUserDetails("testfall2.3@test.com");
        assertNotNull(user, "the user exists in the database");

        Date today = Date.valueOf(LocalDate.now());
        String plantId = "ocotillo";
        String nickname = "Ocotillo";
        Plant plant = new Plant(nickname, plantId, today);
        boolean plantSaved = userPlantRepository.savePlant(user, plant);
        assertTrue(plantSaved, "plant Ocotillo has been saved to the library");

        boolean deleteplantresult = userPlantRepository.deletePlant(user, nickname);
        assertTrue(deleteplantresult, "the plant 'Ocotillo' has been removed from the library");

    }

    @Test
    void testUpdateWateringFrequency() throws SQLException, java.net.UnknownHostException {
        User user = new User(4, "changefrequnit4@test.com", "testUser4", true, true);
        assertTrue(userRepository.saveUser(user));
        User savedUser = userRepository.getUserDetails("changefrequnit4@test.com");

        Date today = Date.valueOf(LocalDate.now());
        Plant plant = new Plant("FreqPlant", "freq1", today, 7, "http://example.com/img.jpg");
        userPlantRepository.savePlant(savedUser, plant);

        // Update watering frequency to 10
        assertTrue(userPlantRepository.updateWateringFrequency(savedUser, plant, 10), "Watering frequency updated successfully");

        Plant updated = userPlantRepository.getPlant(savedUser, "FreqPlant");
        assertEquals(10, updated.getUsers_watering_frequency(), "The watering frequency should be updated to 10");
    }

    @Test
    void seeLibraryOverview() throws SQLException, UnknownHostException {
        User testUser = new User(5, "testfall2.5@test.com", "testUser4", true, true);
        assertTrue(userRepository.saveUser(testUser), "The user has been saved to the database");

        User savedUser = userRepository.getUserDetails("testfall2.5@test.com");
        assertNotNull(savedUser, "the user exists in the database");

        Date today = Date.valueOf(LocalDate.now());
        Plant plant1 = new Plant("Night scented stock", "plant1", today, 7, "http://example.com/plant1.jpg");
        Plant plant2 = new Plant("Northern marsh", "plant2", today, 7, "http://example.com/plant2.jpg");

        assertTrue(userPlantRepository.savePlant(savedUser, plant1), " 'Night scented stock' has been saved");
        assertTrue(userPlantRepository.savePlant(savedUser, plant2), " 'Northern marsh' has been saved");

        ArrayList<Plant> library = userPlantRepository.getUserLibrary(savedUser);
        assertNotNull(library, "the library is not null");
        assertFalse(library.isEmpty(), "the library has plants");

        boolean foundPlant1 = library.stream().anyMatch(p -> "Night scented stock".equals(p.getNickname()));
        boolean foundPlant2 = library.stream().anyMatch(p -> "Northern marsh".equals(p.getNickname()));
        assertTrue(foundPlant1, "The library has 'Night scented stock'");
        assertTrue(foundPlant2, "The library has 'Northern marsh'");

    }

    @Test
    void testChangeLastWatered() throws SQLException, UnknownHostException {
        User testUser = new User(6, "testfall2.6@test.com", "testUser6", true, true);

        assertTrue(userRepository.saveUser(testUser));
        User savedTestUser = userRepository.getUserDetails("testfall2.6@test.com");

        Date oldDate = Date.valueOf("2025-01-01");
        Plant testPlant = new Plant("testPlant", "1", oldDate);

        assertTrue(userPlantRepository.savePlant(savedTestUser, testPlant));

        LocalDate newDate = LocalDate.now();
        assertTrue(userPlantRepository.changeLastWatered(savedTestUser, testPlant.getNickname(), newDate));
        testPlant = userPlantRepository.getPlant(savedTestUser, testPlant.getNickname());
        assertEquals(Date.valueOf(newDate), testPlant.getLastWatered());

    }



    @Test
    void testSaveWishListPlant() throws SQLException, UnknownHostException {
        User testUser = new User(7, "testfall2.7@test.com", "testUser7", true, true);

        assertTrue(userRepository.saveUser(testUser));
        User savedTestUser = userRepository.getUserDetails("testfall2.7@test.com");
        Plant testPlant = new Plant("7", "commonName7", "http://example.com/img.jpg", Date.valueOf(LocalDate.now()));

        assertTrue(userPlantRepository.saveWishlistPlant(savedTestUser, testPlant));

        ArrayList<Plant> wishlist = userPlantRepository.getUserWishlist(savedTestUser);
        assertNotNull(wishlist);
        System.out.println(wishlist);

        boolean found = false;
        for (Plant p : wishlist) {
            if ("7".equals(p.getPlantId())) {
                found = true;
                break;
            }
        }

        assertTrue(found);
    }


    @Test
    void testChangeNickname() throws SQLException, UnknownHostException {
        User testUser = new User(8, "testfall2.8@test.com", "testUser8", true, true);

        assertTrue(userRepository.saveUser(testUser));
        User savedTestUser = userRepository.getUserDetails("testfall2.8@test.com");
        String oldNickname = "oldNickname";

        Plant testPlant = new Plant(oldNickname, "8", Date.valueOf(LocalDate.now()));
        assertTrue(userPlantRepository.savePlant(savedTestUser, testPlant));

        String newNickname = "newNickname";
        assertTrue(userPlantRepository.changeNickname(savedTestUser, oldNickname, newNickname));
        Plant updatedPlant = userPlantRepository.getPlant(savedTestUser, newNickname);
        assertEquals(newNickname, updatedPlant.getNickname());

    }

    @Test
    void testChangeAllToWatered() throws SQLException, UnknownHostException {
        User testUser1 = new User(9, "testfall2.9@test.com", "testUser9", true, true);

        assertTrue(userRepository.saveUser(testUser1));

        User savedTestUser = userRepository.getUserDetails("testfall2.9@test.com");

        String oldLastWatered = "2025-01-01";
        Plant testPlant1 = new Plant("testPlant", "9", Date.valueOf(oldLastWatered));
        Plant testPlant2 = new Plant("testPlant", "10", Date.valueOf(oldLastWatered));
        Plant testPlant3 = new Plant("testPlant", "11", Date.valueOf(oldLastWatered));

        assertTrue(userPlantRepository.savePlant(savedTestUser, testPlant1));
        assertTrue(userPlantRepository.savePlant(savedTestUser, testPlant2));
        assertTrue(userPlantRepository.savePlant(savedTestUser, testPlant3));

        assertTrue(userPlantRepository.changeAllToWatered(savedTestUser));

        ArrayList<Plant> library = userPlantRepository.getUserLibrary(savedTestUser);

        for (Plant p : library) {
            assertEquals(Date.valueOf(LocalDate.now()), p.getLastWatered());
        }


    }

    @Test
    void testChangePlantPicture() throws SQLException, UnknownHostException {
        User testUser = new User(10, "testfall2.10@test.com", "testUser10", true, true);

        assertTrue(userRepository.saveUser(testUser));
        User savedTestUser = userRepository.getUserDetails("testfall2.10@test.com");

        String picture = "http://picture.com/img.jpg";

        Plant testPlant = new Plant("nickName", "10", Date.valueOf(LocalDate.now()), picture);
        assertTrue(userPlantRepository.savePlant(savedTestUser, testPlant));

        assertTrue(userPlantRepository.changePlantPicture(savedTestUser, testPlant));

    }
}