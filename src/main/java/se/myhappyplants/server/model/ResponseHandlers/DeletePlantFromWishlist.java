package se.myhappyplants.server.model.ResponseHandlers;

import se.myhappyplants.server.model.IResponseHandler;
import se.myhappyplants.server.services.UserPlantRepository;
import se.myhappyplants.shared.Message;
import se.myhappyplants.shared.Plant;
import se.myhappyplants.shared.User;

public class DeletePlantFromWishlist implements IResponseHandler {
    private UserPlantRepository userPlantRepository;

    public DeletePlantFromWishlist(UserPlantRepository userPlantRepository) {
        this.userPlantRepository = userPlantRepository;
    }

    @Override
    public Message getResponse(Message request) {
        System.out.println("Vi är i delete plant from wishlist");
        Message response;
        User user = request.getUser();
        int userId = user.getUniqueId();
        Plant plant = request.getPlant();
        int plantID = Integer.parseInt(plant.getPlantId());
        System.out.println("Efter parse");
        boolean test = userPlantRepository.deletePlantFromWishlist(userId, plantID);
        if (test) {
            System.out.println("Message är true");
            response = new Message(true);
        } else {
            System.out.println("Message är false");
            response = new Message(false);
        }
        return response;
    }
}
