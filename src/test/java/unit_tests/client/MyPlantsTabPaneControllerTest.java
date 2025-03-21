package unit_tests.client;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import se.myhappyplants.client.controller.MyPlantsTabPaneController;
import se.myhappyplants.shared.Plant;

import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class MyPlantsTabPaneControllerTest {

    Date defaultDate = new Date(0, 0, 1);
    Plant testPlant = new Plant("1", defaultDate, "TestName", "Scientific name", "light", "Family", "Water", "This describes the plant", "Blommor/blomma2.png");

    MyPlantsTabPaneController myPlantsTabPaneController = new MyPlantsTabPaneController();

    @BeforeAll
    static void initJfxRuntime() {
        Platform.startup(() -> {});
    }

    @Test
    public void addPlantToUserLibraryTest(){
        testPlant.setLastWatered(defaultDate.toLocalDate());
        assertTrue(myPlantsTabPaneController.addPlantToUserLibrary(testPlant));
    }

    @Test
    public void getCurrentUserLibraryTest(){
        testPlant.setLastWatered(defaultDate.toLocalDate());
        myPlantsTabPaneController.addPlantToUserLibrary(testPlant);
        ArrayList<Plant> currentUserLibrary = (ArrayList<Plant>) myPlantsTabPaneController.getCurrentUserLibrary();
        assertEquals(testPlant, currentUserLibrary.getFirst());
    }



}
