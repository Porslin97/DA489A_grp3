package se.myhappyplants.server.model.ResponseHandlers;

import se.myhappyplants.server.model.IResponseHandler;
import se.myhappyplants.server.services.PlantApiService;
import se.myhappyplants.shared.Message;
import se.myhappyplants.shared.Plant;

import java.util.ArrayList;
/**
 * Class that handles the request of a search
 */
public class Search implements IResponseHandler {
    private PlantApiService plantApiService;

    public Search(PlantApiService plantApiService) {
        this.plantApiService = plantApiService;
    }

    @Override
    public Message getResponse(Message request) {
        Message response;
        String searchText = request.getMessageText();
        try {
            ArrayList<Plant> plantList = plantApiService.getResult(searchText);
            response = new Message(plantList, true);
        } catch (Exception e) {
            response = new Message(false);
            e.printStackTrace();
        }
        return response;
    }
}
