package se.myhappyplants.server.model;

import se.myhappyplants.server.model.ResponseHandlers.*;
import se.myhappyplants.server.services.PlantApiService;
import se.myhappyplants.server.services.UserPlantRepository;
import se.myhappyplants.server.services.UserRepository;
import se.myhappyplants.shared.MessageType;

import java.util.HashMap;

/**
 * Class that stores all the different handlers for database requests
 * Created by: Christopher O'Driscoll
 * Updated by:
 */
public class ResponseContext {

    private HashMap<MessageType, IResponseHandler> responders = new HashMap<>();
    private UserRepository userRepository;
    private UserPlantRepository userPlantRepository;
    private PlantApiService plantApiService;

    public ResponseContext(UserRepository userRepository, UserPlantRepository userPlantRepository, PlantApiService plantApiService) {

        this.userRepository = userRepository;
        this.userPlantRepository = userPlantRepository;
        this.plantApiService = plantApiService;
        createResponders();
    }

    /**
     * Links the relevant ResponseHandlers to each MessageType
     */
    private void createResponders() {
        responders.put(MessageType.changeAllToWatered, new ChangeAllToWatered(userPlantRepository));
        responders.put(MessageType.changeFunFacts, new ChangeFunFacts(userRepository));
        responders.put(MessageType.changeLastWatered, new ChangeLastWatered(userPlantRepository));
        responders.put(MessageType.changeNickname, new ChangeNickname(userPlantRepository));
        responders.put(MessageType.changeWateringFrequency, new UpdateWateringFrequency(userPlantRepository));
        responders.put(MessageType.changeNotifications, new ChangeNotifications(userRepository));
        responders.put(MessageType.changePlantPicture, new ChangePlantPicture(userPlantRepository));
        responders.put(MessageType.deleteAccount, new DeleteAccount(userRepository));
        responders.put(MessageType.deletePlant, new DeletePlant(userPlantRepository));
        responders.put(MessageType.getLibrary, new GetLibrary(userPlantRepository));
        responders.put(MessageType.getMorePlantInfo, new GetPlantDetails(plantApiService));
        responders.put(MessageType.login, new Login(userRepository));
        responders.put(MessageType.register, new Register(userRepository));
        responders.put(MessageType.savePlant, new SavePlant(userPlantRepository));
        responders.put(MessageType.savePlantWishlist, new SaveWishlistPlant(userPlantRepository, plantApiService));
        responders.put(MessageType.getWishlist, new GetWishlist(userPlantRepository));
        responders.put(MessageType.removePlantWishlist, new DeletePlantFromWishlist(userPlantRepository));
        responders.put(MessageType.search, new Search(plantApiService));
        responders.put(MessageType.updateIsFavorite, new UpdateFavorite(userPlantRepository));
    }

    public IResponseHandler getResponseHandler(MessageType messageType) {
        return responders.get(messageType);
    }
}
