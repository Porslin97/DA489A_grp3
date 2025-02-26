package se.myhappyplants.client.model;

import javafx.scene.image.Image;

import java.io.File;

/**
 * Container class for the images
 */
public class ImageLibrary {

    private static final Image plusSign = new Image("Blommor/plusSign.png");
    private static final Image wishlistSign = new Image("Blommor/star.png");
    private static final Image deleteSign = new Image("Blommor/cancel.png");
    private static final File loadingImageFile = new File("resources/images/img.png");
    private static final File defaultPlantImageFile = new File("resources/images/Grn_vxt.png");


    /**
     * Getter method to the plus sign image
     * @return Image
     */
    public static Image getPlusSign() {
        return plusSign;
    }

    public static Image getDeleteSign() {
        return deleteSign;
    }

    public static Image getWishlistSign() {
        return wishlistSign;
    }

    /**
     * Getter method to the loading image
     * @return Image
     */
    public static File getLoadingImageFile() {
        return loadingImageFile;
    }

    /**
     * Getter method to the default plant image
     * @return File
     */
    public static File getDefaultPlantImage() {
        return defaultPlantImageFile;
    }
}
