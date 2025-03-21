package unit_tests.client;

import org.junit.jupiter.api.Test;
import se.myhappyplants.client.model.LoggedInUser;
import se.myhappyplants.shared.User;

import static org.junit.jupiter.api.Assertions.*;

public class LoggedInUserTest {

    User user = new User("test@mail.com", "test123");
    @Test
    public void getinstanceTest(){
        assertNotNull(LoggedInUser.getInstance());
    }

    @Test
    public void setUserTest(){
        LoggedInUser.getInstance().setUser(user);
        assertEquals(user, LoggedInUser.getInstance().getUser());
    }
}
