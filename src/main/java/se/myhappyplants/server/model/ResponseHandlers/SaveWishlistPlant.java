package se.myhappyplants.server.model.ResponseHandlers;

import se.myhappyplants.server.model.IResponseHandler;
import se.myhappyplants.server.services.UserPlantRepository;
import se.myhappyplants.shared.Message;
import se.myhappyplants.shared.Plant;
import se.myhappyplants.shared.PlantDetails;
import se.myhappyplants.shared.User;
/**
 * Class that saves a users plant to the wishlist
 */
public class SaveWishlistPlant implements IResponseHandler {
    private UserPlantRepository userPlantRepository;

    public SaveWishlistPlant(UserPlantRepository userPlantRepository) {
        this.userPlantRepository = userPlantRepository;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        User user = request.getUser();
        Plant plant = request.getPlant();
        PlantDetails plantDetails = request.getPlantDetails();
        if (userPlantRepository.saveWishlistPlant(user, plant, plantDetails)) {
            System.out.println(plant.toString());
            response = new Message(true);

        } else {
            response = new Message(false);
        }
        return response;
    }
}
