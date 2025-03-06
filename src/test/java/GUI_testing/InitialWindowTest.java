package GUI_testing;

import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;
import se.myhappyplants.client.controller.StartClient;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;


/**
 * Test class for initial window.
 * TestFX is used for GUI testing
 */

@ExtendWith(ApplicationExtension.class) // https://github.com/TestFX/TestFX
public class InitialWindowTest extends FxRobot {

    @Start
    @SuppressWarnings("unused") // Used by TestFX
    public void start(Stage stage) throws Exception {
        new StartClient().start(stage);
    }

    @Test
    void testInitialWindowComponents() {
        verifyThat("#txtFldEmail", isVisible());
        verifyThat("#passFldPassword", isVisible());
        verifyThat("#loginButton", isEnabled());
        verifyThat("#registerLink", isVisible());
        verifyThat("#guestButton", isVisible());
        verifyThat("#guestButton", isEnabled());
    }

    @Test
    void testGuestAccess() {
        clickOn("#guestButton");

        verifyThat("#messageBoxLabel", isVisible());
        verifyThat("#messageBoxLabel", LabeledMatchers.hasText("You will be logged in as a guest. You will only be able to search for plants."));

        clickOn("#okButton");
        verifyThat("#searchTabPane", isVisible());
    }

    @Test
    void testRegisterLinkNavigation() {
        clickOn("#registerLink");

        verifyThat("#registerPane", isVisible());
    }
}