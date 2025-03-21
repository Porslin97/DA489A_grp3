package unit_tests.client;

import org.junit.jupiter.api.Test;
import se.myhappyplants.client.controller.MainPaneController;
import se.myhappyplants.shared.Plant;

import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;


public class MainPaneControllerTest {

    MainPaneController mpc = new MainPaneController();
    Date defaultDate = new Date(0, 0, 1);
    Plant plant =new Plant("1", defaultDate, "TestName", "Scientific name", "light", "Family", "Water", "This describes the plant", "Blommor/blomma2.png");
    @Test
    public void addPlantToDBTest(){
        assertFalse(mpc.addPlantToDB(plant, "wishlist"));
        assertFalse(mpc.addPlantToDB(plant, "library"));
    }


}
