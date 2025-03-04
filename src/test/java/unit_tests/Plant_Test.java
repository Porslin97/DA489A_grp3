package unit_tests;

import org.junit.jupiter.api.Test;
import se.myhappyplants.shared.Plant;

import java.sql.Date;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


public class Plant_Test {
    Date defaultDate = new Date(0, 0, 1);
    Plant testPlant = new Plant("1", defaultDate, "TestName", "Scientific name", "light", "Family", "Water", "This describes the plant", "Blommor/blomma2.png");


    @Test
    public void testSetNickname(){
        testPlant.setNickname("testNickname");
        assertEquals("testNickname", testPlant.getNickname());
    }

    @Test
    public void testSetWateringFrequency(){
        testPlant.setUsers_watering_frequency(3);
        assertEquals(3, testPlant.getUsers_watering_frequency());
    }

    @Test
    public void testSetImageURL(){
        testPlant.setImageURL("Blommor/blomma3.png");
        assertEquals("Blommor/blomma3.png", testPlant.getImageURL());
    }

    @Test
    public void testSetIsFavorite(){
        testPlant.setIsFavorite(true);
        assertTrue(testPlant.getIsFavorite());
    }

    @Test
    public void testSetLastWatered(){
        LocalDate localDate = LocalDate.now();
        testPlant.setLastWatered(localDate);
        assertEquals(localDate, testPlant.getLastWatered().toLocalDate());
    }

    @Test
    public void testGetPlantId(){
        assertEquals("1", testPlant.getPlantId());
    }

    @Test
    public void testGetImageURL(){
        assertEquals("Blommor/blomma2.png", testPlant.getImageURL());
    }

    @Test
    public void testGetCommonName(){
        assertEquals("TestName", testPlant.getCommonName());
    }

    @Test
    public void testGetScientificName(){
        assertEquals("Scientific name", testPlant.getScientificName());
    }

    @Test
    public void testGetLastWatered(){
        assertNull(testPlant.getLastWatered());
    }

    @Test
    public void testGetIsFavorite(){
        assertFalse(testPlant.getIsFavorite());
    }

    @Test
    public void testGetUserWateringFrequency(){
        assertEquals(0, testPlant.getUsers_watering_frequency());
    }

    @Test
    public void testGetProgress(){
        testPlant.setLastWatered(defaultDate.toLocalDate());
        assertEquals(0.02, testPlant.getProgress());
    }

    @Test
    public void testGetDaysUntilWater(){
        assertNull(testPlant.getLastWatered());
    }

    @Test
    public void testGetDatabaseId(){
        assertEquals(0, testPlant.getDatabaseId());
    }

    @Test
    public void testGetDateAdded(){
        assertEquals(defaultDate, testPlant.getDateAdded());
    }

    @Test
    public void testToString(){
        assertEquals("Common name: TestName \tScientific name: Scientific name ", testPlant.toString());
    }
}
