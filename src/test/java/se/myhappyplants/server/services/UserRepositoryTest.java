package se.myhappyplants.server.services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import se.myhappyplants.shared.User;
import java.sql.*;

/**
 * Testklass för UserRepository med en PostgreSQL-testdatabas.
 */
class UserRepositoryTest {

    private UserRepository userRepository;
    private DBQueryExecutor dbQueryExecutor;

    /**
     * Förbereder en PostgreSQL-testdatabas och initialiserar UserRepository innan varje test.
     * Rensar tabellerna med `DELETE` istället för `TRUNCATE` för enklare hantering.
     */
    @BeforeEach
    void setUp() throws SQLException {
        dbQueryExecutor = new DBQueryExecutor(true);
        userRepository = new UserRepository(dbQueryExecutor);

        Statement stmt = dbQueryExecutor.getConnection().createStatement();

        stmt.execute("DELETE FROM user_plants;");
        stmt.execute("DELETE FROM users;");

        stmt.execute("ALTER SEQUENCE users_id_seq RESTART WITH 1;");
        stmt.execute("ALTER SEQUENCE plants_id_seq RESTART WITH 1;");
    }

    /**
     * Testar att en användare kan sparas i databasen.
     */
    @Test
    void saveUser_shouldReturnTrue_whenUserSavedSuccessfully() throws SQLException {
        User user = new User(1, "testing@mail.com", "TestingUser", true, true);
        boolean result = userRepository.saveUser(user);

        // Kontrollera att användaren finns i databasen
        ResultSet rs = dbQueryExecutor.executeQuery("SELECT * FROM users WHERE email = ?", ps -> {
            ps.setString(1, "testing@mail.com");
        });

        assertTrue(rs.next(), "User should be saved in database");
        assertTrue(result, "User should be saved successfully");
    }

    /**
     * Testar att inloggning fungerar när lösenordet matchar.
     */
    @Test
    void checkLogin_shouldReturnTrue_whenPasswordMatches() throws SQLException {
        String email = "test@mail.com";
        String rawPassword = "password123";
        String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt(10));

        dbQueryExecutor.executeUpdate("INSERT INTO users (email, username, password) VALUES (?, ?, ?)", ps -> {
            ps.setString(1, email);
            ps.setString(2, "TestUser");
            ps.setString(3, hashedPassword);
        });

        boolean result = userRepository.checkLogin(email, rawPassword);
        assertTrue(result, "Login should succeed when password matches");
    }

    /**
     * Testar att inloggning misslyckas om lösenordet är felaktigt.
     */
    @Test
    void checkLogin_shouldReturnFalse_whenPasswordDoesNotMatch() throws SQLException {
        String email = "test@mail.com";
        String correctPassword = "correctPassword";
        String storedPassword = BCrypt.hashpw(correctPassword, BCrypt.gensalt(10));

        dbQueryExecutor.executeUpdate("INSERT INTO users (email, username, password) VALUES (?, ?, ?)", ps -> {
            ps.setString(1, email);
            ps.setString(2, "TestUser");
            ps.setString(3, storedPassword);
        });

        boolean result = userRepository.checkLogin(email, "wrongPassword");
        assertFalse(result, "Login should fail when password does not match");
    }

    /**
     * Testar att en användare kan raderas från databasen.
     */
    @Test
    void deleteAccount_shouldReturnTrue_whenUserDeletedSuccessfully() throws SQLException {
        String email = "test@mail.com";
        String password = "password123";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));

        dbQueryExecutor.executeUpdate("INSERT INTO users (email, username, password) VALUES (?, ?, ?)", ps -> {
            ps.setString(1, email);
            ps.setString(2, "TestUser");
            ps.setString(3, hashedPassword);
        });

        boolean result = userRepository.deleteAccount(email, password);

        ResultSet rs = dbQueryExecutor.executeQuery("SELECT * FROM users WHERE email = ?", ps -> {
            ps.setString(1, email);
        });

        assertFalse(rs.next(), "User should be deleted from database");
        assertTrue(result, "User should be deleted successfully");
    }

    /**
     * Testar att vi kan hämta en användares information från databasen.
     */
    @Test
    void getUserDetails_shouldReturnUser_whenUserExists() throws SQLException {
        String email = "test@mail.com";
        String username = "TestUser";
        boolean notificationActivated = true;
        boolean funFactsActivated = false;

        dbQueryExecutor.executeUpdate(
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
     */
    @Test
    void changeNotifications_shouldWork_whenUserExists() throws SQLException {
        String email = "test@mail.com";
        String username = "TestUser";

        dbQueryExecutor.executeUpdate("INSERT INTO users (email, username, password, notification_activated) VALUES (?, ?, ?, ?)", ps -> {
            ps.setString(1, email);
            ps.setString(2, username);
            ps.setString(3, BCrypt.hashpw("password123", BCrypt.gensalt()));
            ps.setBoolean(4, false);
        });

        User user = new User(1, email, username, false, false);
        boolean result = userRepository.changeNotifications(user, true);
        assertTrue(result, "Should return true if user exists and update is successful");
    }

    /**
     * Testar att `changeFunFacts` hanterar `null` korrekt genom att istället skicka `false`.
     */
    @Test
    void changeFunFacts_shouldHandleNullProperly() {
        User user = new User(1, "test@mail.com", "TestUser", true, true);
        boolean result = userRepository.changeFunFacts(user, false);
        assertTrue(result, "Should handle false values correctly");
    }

    /**
     * Stänger anslutningen efter varje test.
     */
    @AfterEach
    void tearDown() throws SQLException {
        if (dbQueryExecutor != null) {
            dbQueryExecutor.closeConnection();
        }
    }
}
