package se.myhappyplants.server.model.ResponseHandlers;

import se.myhappyplants.server.model.IResponseHandler;
import se.myhappyplants.server.services.UserPlantRepository;
import se.myhappyplants.shared.Message;
import se.myhappyplants.shared.Plant;
import se.myhappyplants.shared.User;

import java.util.ArrayList;
/**
 * Class that gets the users wishlist
 */
public class GetWishlist implements IResponseHandler {
    private UserPlantRepository userPlantRepository;

    public GetWishlist(UserPlantRepository userPlantRepository) {
        this.userPlantRepository = userPlantRepository;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        User user = request.getUser();
        try {
            ArrayList<Plant> userWishlist = userPlantRepository.getUserWishlist(user);
            response = new Message(userWishlist, true);
        } catch (Exception e) {
            response = new Message(false);
            e.printStackTrace();
        }
        return response;
    }
}
