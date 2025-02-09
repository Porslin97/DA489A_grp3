package se.myhappyplants.server.services;

import se.myhappyplants.shared.Plant;
import se.myhappyplants.shared.User;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Class responsible for calling the database about a users library.
 * Created by: Linn Borgström
 * Updated by: Frida Jacobsson 2021-05-21
 */
public class UserPlantRepository {

    private IQueryExecutor database;

    /**
     * Constructor that creates a connection to the database.
     *
     * @throws SQLException
     * @throws UnknownHostException
     */
    public UserPlantRepository(IQueryExecutor database) throws UnknownHostException, SQLException {
        this.database = database;

    }

    /**
     * Method to save a new plant in database
     * Author: Frida Jacobsson
     * Updated Frida Jacobsson 2021-04-29
     *
     * @param plant an instance of a newly created plant by user
     * @return a boolean value, true if the plant was stored successfully
     */

    public boolean savePlant(User user, Plant plant) {
        boolean success = false;
        String query = "INSERT INTO user_plants (user_id, nickname, plant_id, last_watered, image_url, watering_frequency) VALUES (?, ?, ?, ?, ?, ?);";
        try {
            database.executeUpdate(query, ps -> {
                ps.setInt(1, user.getUniqueId());
                ps.setString(2, plant.getNickname());
                ps.setString(3, plant.getPlantId());
                ps.setDate(4, plant.getLastWatered());
                ps.setString(5, plant.getImageURL());
                ps.setInt(6, plant.getUsers_watering_frequency());
            });
            success = true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return success;
    }

    /**
     * Method that returns all the plants connected to the logged in user.
     * Author: Linn Borgström,
     * Updated by: Frida Jacobsson
     *
     * @return an arraylist if plants stored in the database
     */
    public ArrayList<Plant> getUserLibrary(User user) {
        ArrayList<Plant> plantList = new ArrayList<>();
        String query = "SELECT nickname, plant_id, last_watered, image_url, watering_frequency FROM user_plants WHERE user_id = ?;";
        System.out.println("User: " + user.getUniqueId());
        try (ResultSet resultSet = database.executeQuery(query, ps -> ps.setInt(1, user.getUniqueId()))) {
            while (resultSet.next()) {
                String nickname = resultSet.getString("nickname");
                String plantId = resultSet.getString("plant_id");
                Date lastWatered = resultSet.getDate("last_watered");
                String imageURL = resultSet.getString("image_url");
                int waterFrequency = resultSet.getInt("watering_frequency");
                System.out.println("Nickname: " + nickname + " PlantId: " + plantId + " Last watered: " + lastWatered + " ImageURL: " + imageURL);
                plantList.add(new Plant(nickname, plantId, lastWatered, waterFrequency, imageURL));
            }
        } catch (SQLException exception) {
            System.out.println(exception.fillInStackTrace());
        }
        return plantList;
    }

    /**
     * Method that returns one specific plant based on nickname.
     *
     * @param nickname
     * @return an instance of a specific plant from the database, null if no plant with the specific nickname exists
     */
    public Plant getPlant(User user, String nickname) {
        Plant plant = null;
        String query = "SELECT nickname, plant_id, last_watered, image_url, watering_frequency FROM user_plants WHERE user_id = ? AND nickname = ?;";
        try (ResultSet resultSet = database.executeQuery(query, ps -> {
            ps.setInt(1, user.getUniqueId());
            ps.setString(2, nickname);
        })) {
            if (resultSet.next()) {
                String plantId = resultSet.getString("plant_id");
                Date lastWatered = resultSet.getDate("last_watered");
                String imageURL = resultSet.getString("image_url");
                int waterFrequency = resultSet.getInt("watering_frequency");
                plant = new Plant(nickname, plantId, lastWatered, waterFrequency, imageURL);
            }
        } catch (SQLException sqlException) {
            System.out.println(sqlException.fillInStackTrace());
        }
        return plant;
    }

    /**
     * Method that makes a query to delete a specific plant from table Plant
     *
     * @param user     the user that owns the plant
     * @param nickname nickname of the plant
     * @return boolean result depending on the result, false if exception
     */
    public boolean deletePlant(User user, String nickname) {
        boolean plantDeleted = false;
        String query = "DELETE FROM user_plants WHERE user_id = ? AND nickname = ?;";
        try {
            database.executeUpdate(query, ps -> {
                ps.setInt(1, user.getUniqueId());
                ps.setString(2, nickname);
            });
            plantDeleted = true;
        } catch (SQLException sqlException) {
            System.out.println(sqlException);
        }
        return plantDeleted;
    }

    /**
     * Method that makes a query to change the last watered date of a specific plant in table Plant
     *
     * @param user     the user that owns the plant
     * @param nickname nickname of the plant
     * @param date     new data to change to
     * @return boolean result depending on the result, false if exception
     */
    public boolean changeLastWatered(User user, String nickname, LocalDate date) {
        boolean dateChanged = false;
        String query = "UPDATE user_plants SET last_watered = ? WHERE user_id = ? AND nickname = ?;";
        try {
            database.executeUpdate(query, ps -> {
                ps.setDate(1, Date.valueOf(date));
                ps.setInt(2, user.getUniqueId());
                ps.setString(3, nickname);
            });
            dateChanged = true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return dateChanged;
    }

    public boolean changeNickname(User user, String nickname, String newNickname) {
        boolean nicknameChanged = false;
        String query = "UPDATE user_plants SET nickname = ? WHERE user_id = ? AND nickname = ?;";
        try {
            database.executeUpdate(query, ps -> {
                ps.setString(1, newNickname);
                ps.setInt(2, user.getUniqueId());
                ps.setString(3, nickname);
            });
            nicknameChanged = true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return nicknameChanged;
    }

    public boolean changeAllToWatered(User user) {
        boolean dateChanged = false;
        LocalDate date = LocalDate.now();
        String query = "UPDATE user_plants SET last_watered = ? WHERE user_id = ?;";
        try {
            database.executeUpdate(query, ps -> {
                ps.setDate(1, Date.valueOf(date));
                ps.setInt(2, user.getUniqueId());
            });
            dateChanged = true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return dateChanged;
    }

    public boolean changePlantPicture(User user, Plant plant) {
        boolean pictureChanged = false;
        String query = "UPDATE user_plants SET image_url = ? WHERE user_id = ? AND nickname = ?;";
        try {
            database.executeUpdate(query, ps -> {
                ps.setString(1, plant.getImageURL());
                ps.setInt(2, user.getUniqueId());
                ps.setString(3, plant.getNickname());
            });
            pictureChanged = true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return pictureChanged;
    }
}