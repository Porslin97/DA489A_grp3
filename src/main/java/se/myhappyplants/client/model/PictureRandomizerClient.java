package se.myhappyplants.client.model;

import javafx.scene.image.Image;
import se.myhappyplants.shared.PictureRandomizer;

public class PictureRandomizerClient {

    /**
     * Get a random flower picture.
     * @return Image object created from the random picture URL.
     */
    public static Image getRandomPicture() {
        String pictureUrl = PictureRandomizer.getRandomPictureURL();
        return new Image(pictureUrl);
    }
}
