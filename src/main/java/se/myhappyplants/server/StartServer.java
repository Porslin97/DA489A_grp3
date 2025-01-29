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
        PlantRepository plantRepository = new PlantRepository(queryExecutor);
        UserPlantRepository userPlantRepository = new UserPlantRepository(plantRepository, queryExecutor);

        ResponseController responseController = new ResponseController(userRepository,userPlantRepository,plantRepository);

        new Server(2555, responseController);
    }
}
