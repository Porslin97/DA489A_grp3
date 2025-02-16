package se.myhappyplants.shared;

import java.util.Random;

/**
 * Class for randomizing flower images.
 * This can be used on both client and server side by returning image URLs.
 */
public class PictureRandomizer {

    private static final String[] flowerImages = {
            "Blommor/blomma1.png",
            "Blommor/blomma2.png",
            "Blommor/blomma3.png",
            "Blommor/blomma4.png",
            "Blommor/blomma5.png",
            "Blommor/blomma6.png",
    };

    /**
     * Method that generates a random image URL path.
     * @return String path to a random image.
     */
    public static String getRandomPictureURL() {
        Random random = new Random();
        int index = random.nextInt(flowerImages.length);
        return flowerImages[index];
    }
}
