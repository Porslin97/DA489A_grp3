package se.myhappyplants.server.model.ResponseHandlers;

import se.myhappyplants.server.model.IResponseHandler;
import se.myhappyplants.server.services.PlantApiService;
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
    PlantApiService plantApiService;

    public SaveWishlistPlant(UserPlantRepository userPlantRepository, PlantApiService plantApiService) {
        this.userPlantRepository = userPlantRepository;
        this.plantApiService = plantApiService;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        User user = request.getUser();
        Plant plant = request.getPlant();
        PlantDetails plantDetails = plantApiService.getPlantDetails(plant);
        if (userPlantRepository.saveWishlistPlant(user, plant, plantDetails)) {
            System.out.println(plant.toString());
            response = new Message(true);
        } else {
            response = new Message(false);
        }
        return response;
    }
}
