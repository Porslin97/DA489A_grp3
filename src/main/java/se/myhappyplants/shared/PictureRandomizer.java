package se.myhappyplants.shared;

import java.util.Random;

/**
 * Class for randomizing flower images.
 * This can be used on both client and server side by returning image URLs.
 */
public class PictureRandomizer {

    private static final String[] flowerImages = {
            "resources/images/blomma2.jpg",
            "resources/images/blomma5.jpg",
            "resources/images/blomma6.jpg",
            "resources/images/blomma9.jpg",
            "resources/images/blomma10.jpg",
            "resources/images/blomma17.jpg",
            "resources/images/blomma18.jpg",
            "resources/images/blomma19.jpg",
            "resources/images/blomma21.jpg"
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
