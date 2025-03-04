package unit_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.myhappyplants.shared.PlantDetails;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlantDetails_Test {

    private ArrayList<String> sunlight = new ArrayList<>();
    private PlantDetails plantDetails;
    @BeforeEach
    public void setSunlight(){
        sunlight.add("shady");
        plantDetails = new PlantDetails("FamilyTest", "Description test", "Recommended average", sunlight, "testus scientificus");
    }

    @Test
    public void testGetFamilyName(){
        plantDetails.getFamilyName();
        assertEquals("FamilyTest", plantDetails.getFamilyName());
    }
    @Test
    public void testGetDescription(){
        assertEquals("Description test", plantDetails.getDescription());
    }
    @Test
    public void testGetWateringFrequency(){
        plantDetails.getRecommended_watering_frequency();
        assertEquals("Recommended average", plantDetails.getRecommended_watering_frequency());
    }
    @Test
    public void testGetSunlight(){
        plantDetails.getSunlight();
        assertEquals("shady", plantDetails.getSunlight().getFirst());
    }

    @Test
    public void testGetScientificNameInPLantDetails(){
        plantDetails.getScientificName();
        assertEquals("testus scientificus", plantDetails.getScientificName());
    }

    @Test
    public void testToString(){
        plantDetails.toString();
        assertEquals("PlantDetails{familyName='FamilyTest', description='Description test', recommended_watering_frequency='Recommended average', sunlight=[shady]}", plantDetails.toString());
    }

}
