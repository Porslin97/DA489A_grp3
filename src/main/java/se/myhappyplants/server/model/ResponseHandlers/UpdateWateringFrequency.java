package se.myhappyplants.server.model.ResponseHandlers;

import se.myhappyplants.server.model.IResponseHandler;
import se.myhappyplants.server.services.UserPlantRepository;
import se.myhappyplants.shared.Message;
import se.myhappyplants.shared.Plant;
import se.myhappyplants.shared.User;

public class UpdateWateringFrequency implements IResponseHandler {
    private UserPlantRepository userPlantRepository;

    public UpdateWateringFrequency(UserPlantRepository userPlantRepository) {
        this.userPlantRepository = userPlantRepository;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        User user = request.getUser();
        Plant plant = request.getPlant();
        int newWateringFrequency = request.getNewWateringFrequency();
        if (userPlantRepository.updateWateringFrequency(user, plant, newWateringFrequency)) {
            response = new Message(true);
        } else {
            response = new Message(false);
        }
        return response;
    }
}
