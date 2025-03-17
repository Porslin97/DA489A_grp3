package GUI_testing;

import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.util.WaitForAsyncUtils;
import se.myhappyplants.client.controller.StartClient;
import se.myhappyplants.server.services.UserRepository;
import se.myhappyplants.client.view.ServerConnection;
import se.myhappyplants.server.controller.ResponseController;
import se.myhappyplants.server.services.*;
import se.myhappyplants.shared.User;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.spy;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

@ExtendWith(ApplicationExtension.class)
public class LoginTest extends FxRobot {

    private UserRepository userRepository;
    private UserPlantRepository userPlantRepository;
    private PlantApiService plantApiServiceSpy;
    private DBQueryExecutor dbQueryExecutor;
    Server server;
    private static final int TEST_PORT = 2556;
    private ServerConnection clientConnection;

    @Start
    @SuppressWarnings("unused")
    public void start(Stage stage) throws Exception {
        new StartClient().start(stage);
    }

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
    void testLoginWithValidCredentials() {
        userRepository.saveUser(new User("validuser@example.com", "ValidUser", "password123", true));

        clickOn("#txtFldEmail").eraseText(25);
        clickOn("#txtFldEmail").write("validuser@example.com");
        clickOn("#passFldPassword").write("password123");
        clickOn("#loginButton");

        WaitForAsyncUtils.sleep(1, TimeUnit.SECONDS);

        verifyThat("#popupBoxLabel", isVisible());
        verifyThat("#popupBoxLabel", LabeledMatchers.hasText("Now logged in as\nValidUser"));

        verifyThat("#myPlantsTab", isVisible());
    }

    @Test
    void testLoginWithInvalidCredentials() {
        clickOn("#txtFldEmail").eraseText(25);
        clickOn("#txtFldEmail").write("invaliduser@example.com");
        clickOn("#passFldPassword").write("wrongpassword");
        clickOn("#loginButton");

        WaitForAsyncUtils.sleep(1, TimeUnit.SECONDS);

        verifyThat("#messageBoxLabel", isVisible());
        verifyThat("#messageBoxLabel", LabeledMatchers.hasText("Sorry, we couldn't find an account with that email or you typed the password wrong. Try again or create a new account."));
    }

    @Test
    void testLoginEmptyEmailField() {
        clickOn("#txtFldEmail").eraseText(25);
        clickOn("#txtFldEmail").write("");
        clickOn("#passFldPassword").write("password123");
        clickOn("#loginButton");

        WaitForAsyncUtils.sleep(1, TimeUnit.SECONDS);

        verifyThat("#messageBoxLabel", isVisible());
        verifyThat("#messageBoxLabel", LabeledMatchers.hasText("Please enter your email address in format: yourname@example.com"));
    }

    @Test
    void testLoginEmptyPasswordField() {
        clickOn("#txtFldEmail").eraseText(25);
        clickOn("#txtFldEmail").write("validuser@example.com");
        clickOn("#passFldPassword").write("");
        clickOn("#loginButton");

        WaitForAsyncUtils.sleep(1, TimeUnit.SECONDS);

        verifyThat("#messageBoxLabel", isVisible());
        verifyThat("#messageBoxLabel", LabeledMatchers.hasText("Please enter your password"));
    }

    @Test
    void testLoginInvalidEmailFormat() {
        clickOn("#txtFldEmail").eraseText(25);
        clickOn("#txtFldEmail").write("invalid-email");
        clickOn("#passFldPassword").write("password123");
        clickOn("#loginButton");

        WaitForAsyncUtils.sleep(1, TimeUnit.SECONDS);

        verifyThat("#messageBoxLabel", isVisible());
        verifyThat("#messageBoxLabel", LabeledMatchers.hasText("Please enter your email address in format: yourname@example.com"));
    }
}
