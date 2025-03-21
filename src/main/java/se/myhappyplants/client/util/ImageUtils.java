package se.myhappyplants.client.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageUtils {

    /**
     * Method to check if the image is valid
     * @param imageFile
     * @return boolean if the image is valid
     */
    public static boolean isValidImage(File imageFile) {
        try {
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            if (bufferedImage == null) {
                return false;
            }

            String imagePath = imageFile.toString();
            String imageExtension = imagePath.substring(imagePath.indexOf("."));
            return imageExtension.equalsIgnoreCase(".jpg") || imageExtension.equalsIgnoreCase(".jpeg") || imageExtension.equalsIgnoreCase(".png");

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}