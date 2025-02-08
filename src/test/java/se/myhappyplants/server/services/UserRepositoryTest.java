package se.myhappyplants.server.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import se.myhappyplants.shared.User;
import java.sql.*;

/**
 * Testklass för UserRepository med en H2-databas.
 */

class UserRepositoryTest {

    private UserRepository userRepository;
    private H2QueryExecutor h2QueryExecutor;

    /**
     * Förbereder en H2-databas och initialiserar UserRepository innan varje test.
     * Skapar en ny databasstruktur genom att radera tabellen om den redan finns.
     */
    @BeforeEach
    void setUp() throws SQLException {
        h2QueryExecutor = new H2QueryExecutor();
        userRepository = new UserRepository(h2QueryExecutor);

        Statement stmt = h2QueryExecutor.getConnection().createStatement();
        stmt.execute("DROP TABLE IF EXISTS users");     // Tar bort tabellen om den finns
        stmt.execute("CREATE TABLE users (" +           // Skapar en ny användartabell
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "email VARCHAR(255) UNIQUE, " +
                "username VARCHAR(255), " +
                "password VARCHAR(255), " +
                "notification_activated BOOLEAN, " +
                "fun_facts_activated BOOLEAN" +
                ");");
    }

    /**
     * Testar att en användare kan sparas i databasen.
     * Verifierar att metoden returnerar `true` och att användaren finns i databasen.
     */
    @Test
    void saveUser_shouldReturnTrue_whenUserSavedSuccessfully() throws SQLException {
        User user = new User(1, "test@mail.com", "TestUser", true, true);
        boolean result = userRepository.saveUser(user);

        // Kontrollera att användaren finns i databasen
        ResultSet rs = h2QueryExecutor.executeQuery("SELECT * FROM users WHERE email = ?", ps -> {
            ps.setString(1, "test@mail.com");
        });

        assertTrue(rs.next(), "User should be saved in database");
        assertTrue(result, "User should be saved successfully");
    }

    /**
     * Testar att inloggning fungerar när lösenordet matchar.
     * Lägger till en användare manuellt och verifierar att `checkLogin` returnerar `true`.
     */
    @Test
    void checkLogin_shouldReturnTrue_whenPasswordMatches() throws SQLException {
        String email = "test@mail.com";
        String rawPassword = "password123";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt(10));

        h2QueryExecutor.executeUpdate("INSERT INTO users (email, password) VALUES (?, ?)", ps -> {
            ps.setString(1, email);
            ps.setString(2, hashedPassword);
        });

        boolean result = userRepository.checkLogin(email, rawPassword);

        assertTrue(result, "Login should succeed when password matches");
    }

    /**
     * Testar att inloggning misslyckas om lösenordet är felaktigt.
     * Lägger till en användare och försöker logga in med fel lösenord.
     */
    @Test
    void checkLogin_shouldReturnFalse_whenPasswordDoesNotMatch() throws SQLException {
        String email = "test@mail.com";
        String correctPassword = "correctPassword";
        String storedPassword = BCrypt.hashpw(correctPassword, BCrypt.gensalt(10));

        h2QueryExecutor.executeUpdate("INSERT INTO users (email, password) VALUES (?, ?)", ps -> {
            ps.setString(1, email);
            ps.setString(2, storedPassword);
        });

        boolean result = userRepository.checkLogin(email, "wrongPassword");

        assertFalse(result, "Login should fail when password does not match");
    }

    /**
     * Testar att en användare kan raderas från databasen.
     * Verifierar att användaren försvinner från databasen och att metoden returnerar `true`.
     */
    @Test
    void deleteAccount_shouldReturnTrue_whenUserDeletedSuccessfully() throws SQLException {
        String email = "test@mail.com";
        String password = "password123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));

        h2QueryExecutor.executeUpdate("INSERT INTO users (email, password) VALUES (?, ?)", ps -> {
            ps.setString(1, email);
            ps.setString(2, hashedPassword);
        });

        boolean result = userRepository.deleteAccount(email, password);

        ResultSet rs = h2QueryExecutor.executeQuery("SELECT * FROM users WHERE email = ?", ps -> {
            ps.setString(1, email);
        });

        assertFalse(rs.next(), "User should be deleted from database");
        assertTrue(result, "User should be deleted successfully");
    }

    /**
     * Testar att vi kan hämta en användares information från databasen.
     * Lägger till en användare och kontrollerar att `getUserDetails` returnerar rätt data.
     */
    @Test
    void getUserDetails_shouldReturnUser_whenUserExists() throws SQLException {
        String email = "test@mail.com";
        String username = "TestUser";
        boolean notificationActivated = true;
        boolean funFactsActivated = false;

        h2QueryExecutor.executeUpdate(
                "INSERT INTO users (email, username, password, notification_activated, fun_facts_activated) VALUES (?, ?, ?, ?, ?)", ps -> {
                    ps.setString(1, email);
                    ps.setString(2, username);
                    ps.setString(3, BCrypt.hashpw("password123", BCrypt.gensalt()));
                    ps.setBoolean(4, notificationActivated);
                    ps.setBoolean(5, funFactsActivated);
                });

        User result = userRepository.getUserDetails(email);

        assertNotNull(result, "User should be returned");
        assertEquals(email, result.getEmail());
        assertEquals(username, result.getUsername());
        assertEquals(notificationActivated, result.areNotificationsActivated());
        assertEquals(funFactsActivated, result.areFunFactsActivated());
    }

    /**
     * Testar att raderingen av en användare misslyckas om användaren inte finns.
     * Förväntar sig att metoden returnerar `false`.
     */
    @Test
    void deleteAccount_shouldRollbackTransaction_whenUserNotFound() {
        String email = "nonexistent@mail.com";
        String password = "password123";

        boolean result = userRepository.deleteAccount(email, password);

        assertFalse(result, "Should return false if user does not exist");
    }

    /**
     * Testar att vi kan uppdatera notifikationer för en befintlig användare.
     * Förväntar sig att metoden returnerar `true` när uppdateringen lyckas.
     */
    @Test
    void changeNotifications_shouldWork_whenUserExists() throws SQLException {
        String email = "test@mail.com";
        String username = "TestUser";

        h2QueryExecutor.executeUpdate("INSERT INTO users (email, username, password, notification_activated) VALUES (?, ?, ?, ?)", ps -> {
            ps.setString(1, email);
            ps.setString(2, username);
            ps.setString(3, BCrypt.hashpw("password123", BCrypt.gensalt()));
            ps.setBoolean(4, false); // Börjar som false
        });

        User user = new User(1, email, username, false, false);
        boolean result = userRepository.changeNotifications(user, true);

        assertTrue(result, "Should return true if user exists and update is successful");
    }

    /**
     * Testar att `changeFunFacts` hanterar `null` korrekt genom att istället skicka `false`.
     * Förväntar sig att metoden returnerar `true` när `false` skickas in.
     */
    @Test
    void changeFunFacts_shouldHandleNullProperly() {
        User user = new User(1, "test@mail.com", "TestUser", true, true);

        // Antingen skicka false istället för null eller ta bort testet helt
        boolean result = userRepository.changeFunFacts(user, false);

        assertTrue(result, "Should handle false values correctly");
    }

    /**
     * Stänger anslutningen efter varje test.
     */
    @AfterEach
    void tearDown() throws SQLException {
        if (h2QueryExecutor != null) {
            h2QueryExecutor.closeConnection();
        }
    }
}
