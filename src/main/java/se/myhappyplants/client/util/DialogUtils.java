package se.myhappyplants.client.util;

import se.myhappyplants.client.model.BoxTitle;
import se.myhappyplants.client.view.MessageBox;

/**
 * Class with utility methods for dialogs
 */

public class DialogUtils {

    /**
     * Method to get the watering frequency from the user
     * @return the watering frequency in days
     */

    public static int getValidWateringFrequency() {
        while (true) {
            String input = MessageBox.askForStringInput("Watering frequency", "How often should this plant be watered (in days)?");

            if (input == null) {
                return -1;
            }

            input = input.trim();
            if (input.isEmpty()) {
                MessageBox.display(BoxTitle.Error, "Watering frequency cannot be empty. Please enter a valid number.");
                continue;
            }

            try {
                int newWateringFrequency = Integer.parseInt(input);

                if (newWateringFrequency > 0) {
                    return newWateringFrequency;
                } else {
                    MessageBox.display(BoxTitle.Error, "Please enter a number greater than 0.");
                }
            } catch (NumberFormatException e) {
                MessageBox.display(BoxTitle.Error, "Please enter a valid number for watering frequency.");
            }
        }
    }
}

