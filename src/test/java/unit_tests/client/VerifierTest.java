package unit_tests.client;

import org.junit.jupiter.api.Test;
import se.myhappyplants.client.controller.RegisterPaneController;
import se.myhappyplants.client.model.Verifier;

import static org.junit.jupiter.api.Assertions.*;

public class VerifierTest {
    Verifier verifier = new Verifier();
    RegisterPaneController rpc = new RegisterPaneController();

    @Test
    public void validateEmailTest(){
      assertFalse(verifier.validateEmail("test"));
      assertTrue(verifier.validateEmail("test@test.com"));
    }

    @Test
    public void validateRegistrationTest(){
        assertFalse(verifier.validateRegistration(rpc));
    }

}
