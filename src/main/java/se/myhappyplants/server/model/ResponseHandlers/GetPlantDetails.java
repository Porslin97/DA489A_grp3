package se.myhappyplants.server.model.ResponseHandlers;

import se.myhappyplants.server.model.IResponseHandler;
import se.myhappyplants.server.services.PlantApiService;
import se.myhappyplants.shared.Message;
import se.myhappyplants.shared.Plant;
import se.myhappyplants.shared.PlantDetails;
/**
 * Class that gets the plant details
 */
public class GetPlantDetails implements IResponseHandler {
    private PlantApiService plantApiService;

    public GetPlantDetails(PlantApiService plantApiService) {
        this.plantApiService = plantApiService;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        Plant plant = request.getPlant();
        try {
            PlantDetails plantDetails = plantApiService.getPlantDetails(plant);
            response = new Message(plantDetails, true);
        } catch (Exception e) {
            response = new Message(false);
            e.printStackTrace();
        }
        return response;
    }
}
