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
public class RegistrationTest extends FxRobot {

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
    void testRegisterNewUser() { // works?
        clickOn("#registerLink");

        clickOn("#txtFldNewEmail").write("newuser@example.com");
        clickOn("#txtFldNewEmail1").write("newuser@example.com"); // TODO: rename in FXML to txtFldNewEmailRepeat or similar
        clickOn("#txtFldNewUsername").write("NewUser");
        clickOn("#passFldNewPassword").write("password123");
        clickOn("#passFldNewPassword1").write("password123");
        clickOn("#registerButton");

        verifyThat("#yesNoMessageBoxLabel", isVisible());
        verifyThat("#yesNoMessageBoxLabel", LabeledMatchers.hasText("Your account details will be saved in accordance with GDPR requirements\nDo you still want to create the account?"));

        clickOn("#yesButton");

        WaitForAsyncUtils.sleep(2, TimeUnit.SECONDS);

        verifyThat("#messageBoxLabel", isVisible());
        verifyThat("#messageBoxLabel", LabeledMatchers.hasText("Account created successfully! Now logged in as NewUser"));
    }

    @Test
    void testRegisterExistingUser() {
        String email = "test@mail.com";
        String username = "TestDelete";
        String rawPassword = "password123";
        userRepository.saveUser(new User(email, username, rawPassword, true));

        clickOn("#registerLink");
        clickOn("#txtFldNewEmail").write(email);
        clickOn("#txtFldNewEmail1").write(email);
        clickOn("#txtFldNewUsername").write(username);
        clickOn("#passFldNewPassword").write(rawPassword);
        clickOn("#passFldNewPassword1").write(rawPassword);
        clickOn("#registerButton");
        clickOn("#yesButton");

        WaitForAsyncUtils.sleep(1, TimeUnit.SECONDS);

        verifyThat("#messageBoxLabel", isVisible());
        verifyThat("#messageBoxLabel", LabeledMatchers.hasText("An account with this email address or username already exists here at My Happy Plants."));
    }

    @Test
    void testInvalidEmailFormat() {
        clickOn("#registerLink");
        clickOn("#txtFldNewEmail").write("invalid-email");
        clickOn("#txtFldNewEmail1").write("invalid-email");
        clickOn("#txtFldNewUsername").write("NewUser");
        clickOn("#passFldNewPassword").write("password123");
        clickOn("#passFldNewPassword1").write("password123");
        clickOn("#registerButton");
        clickOn("#yesButton");

        WaitForAsyncUtils.sleep(1, TimeUnit.SECONDS);

        verifyThat("#messageBoxLabel", isVisible());
        verifyThat("#messageBoxLabel", LabeledMatchers.hasText("Please enter your email address in format: yourname@example.com"));
    }

    @Test
    void testEmptyUsername() {
        clickOn("#registerLink");
        clickOn("#txtFldNewEmail").write("newuser@example.com");
        clickOn("#txtFldNewEmail1").write("newuser@example.com");
        clickOn("#passFldNewPassword").write("password123");
        clickOn("#passFldNewPassword1").write("password123");
        clickOn("#registerButton");
        clickOn("#yesButton");

        WaitForAsyncUtils.sleep(1, TimeUnit.SECONDS);

        verifyThat("#messageBoxLabel", isVisible());
        verifyThat("#messageBoxLabel", LabeledMatchers.hasText("Please enter a username"));
    }

    @Test
    void testEmptyPassword() {
        clickOn("#registerLink");
        clickOn("#txtFldNewEmail").write("newuser@example.com");
        clickOn("#txtFldNewEmail1").write("newuser@example.com");
        clickOn("#txtFldNewUsername").write("NewUser");
        clickOn("#registerButton");
        clickOn("#yesButton");

        WaitForAsyncUtils.sleep(1, TimeUnit.SECONDS);

        verifyThat("#messageBoxLabel", isVisible());
        verifyThat("#messageBoxLabel", LabeledMatchers.hasText("Please enter a password"));
    }

    @Test
    void testMismatchedEmails() {
        clickOn("#registerLink");
        clickOn("#txtFldNewEmail").write("newuser@example.com");
        clickOn("#txtFldNewEmail1").write("different@example.com");
        clickOn("#txtFldNewUsername").write("NewUser");
        clickOn("#passFldNewPassword").write("password123");
        clickOn("#passFldNewPassword1").write("password123");
        clickOn("#registerButton");
        clickOn("#yesButton");

        WaitForAsyncUtils.sleep(1, TimeUnit.SECONDS);

        verifyThat("#messageBoxLabel", isVisible());
        verifyThat("#messageBoxLabel", LabeledMatchers.hasText("Please enter the same email twice"));
    }

    @Test
    void testMismatchedPasswords() {
        clickOn("#registerLink");
        clickOn("#txtFldNewEmail").write("newuser@example.com");
        clickOn("#txtFldNewEmail1").write("newuser@example.com");
        clickOn("#txtFldNewUsername").write("NewUser");
        clickOn("#passFldNewPassword").write("password123");
        clickOn("#passFldNewPassword1").write("differentpassword");
        clickOn("#registerButton");
        clickOn("#yesButton");

        WaitForAsyncUtils.sleep(1, TimeUnit.SECONDS);

        verifyThat("#messageBoxLabel", isVisible());
        verifyThat("#messageBoxLabel", LabeledMatchers.hasText("Please enter the same password twice"));
    }
}

