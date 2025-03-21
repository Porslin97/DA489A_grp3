package unit_tests.client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import se.myhappyplants.client.model.ButtonText;
import static org.junit.jupiter.api.Assertions.*;

public class ButtonTextTest {
    ButtonText btntext = new ButtonText();
    @FXML
    ToggleButton tglButton = new ToggleButton();

    @BeforeAll
    static void initJfxRuntime() {
        Platform.startup(() -> {});
    }

    @Test
    public void setButtonTextTest_WhenSelected(){
        tglButton.setSelected(true);
        btntext.setButtonText(tglButton);
        assertEquals("On", tglButton.getText());
    }

    @Test
    public void setButtonTextTest_WhenUnSelected(){
        tglButton.setSelected(false);
        btntext.setButtonText(tglButton);
        assertEquals("Off", tglButton.getText());
    }
}
