package se.myhappyplants.client.model;

import javafx.scene.image.Image;

/**
 * Container class for the images
 */
public class ImageLibrary {

    private static final Image plusSign = new Image("Blommor/plusSign.png");
    private static final Image wishlistSign = new Image("Blommor/star.png");
    private static final Image deleteSign = new Image("Blommor/cancel.png");

    private static final Image emptyHeart = new Image("Blommor/heart_empty.png");
    private static final Image fullHeart = new Image("Blommor/heart_full.png");
    private static final Image loadingImage = new Image("Blommor/img.png");
    private static final Image defaultPlantImage = new Image("Blommor/Grn_vxt.png");


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

    public static Image getEmptyHeart() {
        return emptyHeart;
    }

    public static Image getFullHeart() {
        return fullHeart;
    }

    /**
     * Getter method to the loading image
     * @return Image
     */
    public static Image getLoadingImage() {
        return loadingImage;
    }

    /**
     * Getter method to the default plant image
     * @return File
     */
    public static Image getDefaultPlantImage() {
        return defaultPlantImage;
    }
}
