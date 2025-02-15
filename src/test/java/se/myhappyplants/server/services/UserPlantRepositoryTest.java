package se.myhappyplants.server.services;

import javafx.application.Platform;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.myhappyplants.shared.Plant;
import se.myhappyplants.shared.User;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import javafx.application.Platform;
import static org.junit.jupiter.api.Assertions.*;
class UserPlantRepositoryTest {

    private UserRepository userRepository;
    private UserPlantRepository userPlantRepository;
    private DBQueryExecutor dbQueryExecutor;


    @BeforeAll
    static void javaFXinitiliazation() {
        try{
            Platform.startup(() ->{

            });
        }catch (IllegalStateException e){

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
        User testUser = new User(1, "testfall2.1@test.com", "testUser", true, true);
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
   void deletePlantFromLibrary() throws SQLException, java.net.UnknownHostException{
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

}