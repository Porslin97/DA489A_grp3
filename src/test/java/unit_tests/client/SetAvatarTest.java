package unit_tests.client;

import org.junit.jupiter.api.Test;
import se.myhappyplants.client.model.SetAvatar;

import static org.junit.jupiter.api.Assertions.*;

public class SetAvatarTest {

    @Test
    public void setAvatarOnLoginTest(){
        //Due to the method returning an absolute path that will be different from pc to pc just the end is tested
        String test = SetAvatar.setAvatarOnLogin("test@mail.com");
        assertTrue(test.endsWith("resources/images/user_default_img.png"));
    }

}
