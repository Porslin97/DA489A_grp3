package se.myhappyplants.server;

import se.myhappyplants.server.controller.ResponseController;
import se.myhappyplants.server.services.*;

import java.net.UnknownHostException;
import java.sql.SQLException;

/**
 * Class that starts the server
 * Created by: Frida Jacobson, Eric Simonson, Anton Holm, Linn Borgström, Christopher O'Driscoll
 * Updated by: Frida Jacobsson 2021-05-21
 */
public class StartServer {
    public static void main(String[] args) throws UnknownHostException, SQLException {
        IDatabaseConnection databaseConnection = new DatabaseConnection("grp3myhappyplants");

        IQueryExecutor queryExecutor = new QueryExecutor(databaseConnection);

        UserRepository userRepository = new UserRepository(queryExecutor);
        PlantApiService plantApiService = new PlantApiService();
        UserPlantRepository userPlantRepository = new UserPlantRepository(plantApiService, queryExecutor);

        ResponseController responseController = new ResponseController(userRepository,userPlantRepository, plantApiService);

        new Server(2555, responseController);
    }
}
