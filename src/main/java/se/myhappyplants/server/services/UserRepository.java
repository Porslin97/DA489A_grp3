package se.myhappyplants.server.services;

import org.mindrot.jbcrypt.BCrypt;
import se.myhappyplants.shared.User;

import java.sql.*;

/**
 * Class responsible for calling the database about users.
 * Created by: Frida Jacobsson 2021-03-30
 * Updated by: Frida Jacobsson 2021-05-21
 */
public class UserRepository {

    private IQueryExecutor queryExecutor;

    public UserRepository(IQueryExecutor queryExecutor) {
        this.queryExecutor = queryExecutor;
    }

    /**
     * Method to save a new user using BCrypt.
     *
     * @param user An instance of a newly created User that should be stored in the database.
     * @return A boolean value, true if the user was stored successfully
     */
    public boolean saveUser(User user) {
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        String query = "INSERT INTO users (email, username, password, notification_activated, fun_facts_activated) VALUES (?, ?, ?, ?, ?);";

        try {
            queryExecutor.executeUpdate(query, ps -> {
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getUsername());
                ps.setString(3, hashedPassword);
                ps.setBoolean(4, true);
                ps.setBoolean(5, true);
            });
            return true;
        } catch (SQLException sqlException) {
            System.err.println("Failed to save user: " + sqlException.getMessage());
            sqlException.printStackTrace();
            return false;
        }
    }

    /**
     * Method to check if a user exists in database.
     * Purpose of method is to make it possible for user to log in
     *
     * @param email    typed email from client and the application
     * @param password typed password from client and the application
     * @return A boolean value, true if the user exist in database and the password is correct
     */
    public boolean checkLogin(String email, String password) {
        boolean isVerified = false;
        String query = "SELECT password FROM users WHERE email = ? OR username = ?;";
      
        try (ResultSet resultSet = queryExecutor.executeQuery(query, ps -> {
            ps.setString(1, email);
            ps.setString(2, email);
                }))
        {
            if (resultSet.next()) {
                String hashedPassword = resultSet.getString(1);
                isVerified = BCrypt.checkpw(password, hashedPassword);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return isVerified;
    }

    /**
     * Method to get information (id, username and notification status) about a specific user
     *
     * @param emailOrUsername ??
     * @return a new instance of USer
     */
    public User getUserDetails(String emailOrUsername) {
        User user = null;
        int uniqueID = 0;
        String username = null;
        String email = null;
        boolean notificationActivated = false;
        boolean funFactsActivated = false;
        String query = "SELECT id, username, email, notification_activated, fun_facts_activated FROM users WHERE email = ? OR username = ?;";

        try (ResultSet resultSet = queryExecutor.executeQuery(query, ps -> {
            ps.setString(1, emailOrUsername);
            ps.setString(2, emailOrUsername);
                }))
        {
            if (resultSet.next()) {
                uniqueID = resultSet.getInt(1);
                username = resultSet.getString(2);
                email = resultSet.getString(3);
                notificationActivated = resultSet.getBoolean(4);
                funFactsActivated = resultSet.getBoolean(5);
            }
            user = new User(uniqueID, email, username, notificationActivated, funFactsActivated);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return user;
    }

    /**
     * Method to delete a user and all plants in user library at once
     * author: Frida Jacobsson
     *
     * @param email
     * @param password
     * @return boolean value, false if transaction is rolled back
     * @throws SQLException
     */
    public boolean deleteAccount(String email, String password) {
        boolean accountDeleted = false;
        if (checkLogin(email, password)) {
            String querySelect = "SELECT id FROM users WHERE email = ?;";
            try {
                queryExecutor.beginTransaction();
                try (ResultSet resultSet = queryExecutor.executeQuery(querySelect, ps -> {
                    ps.setString(1, email);
                })) {
                    if (resultSet.next()) {
                        int id = resultSet.getInt(1);
                        String queryDeleteUser = "DELETE FROM users WHERE id = ?;";
                        queryExecutor.executeUpdate(queryDeleteUser, ps -> {
                            ps.setInt(1, id);
                        });

                    } else {
                        throw new SQLException("No user found with email: " + email);
                    }
                    queryExecutor.endTransaction();
                    accountDeleted = true;
                }
            } catch (SQLException sqlException) {
                try {
                    queryExecutor.rollbackTransaction();
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
            }
        }
        return accountDeleted;
    }

    /**
     * Method to change the notification status for a user
     *
     * @param user          the user that should have the notification status changed
     * @param notifications the new notification status
     * @return boolean value, false if the transaction is rolled back
     */
    public boolean changeNotifications(User user, boolean notifications) {
        String query = "UPDATE users SET notification_activated = ? WHERE email = ?";
        try {
            queryExecutor.executeUpdate(query, ps -> {
                ps.setBoolean(1, notifications);
                ps.setString(2, user.getEmail());
            });
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Method to change the fun facts status for a user
     *
     * @param user              the user that should have the fun facts status changed
     * @param funFactsActivated the new fun facts status
     * @return boolean value, false if the transaction is rolled back
     */
    public boolean changeFunFacts(User user, Boolean funFactsActivated) {
        String query = "UPDATE users SET fun_facts_activated = ? WHERE email = ?";
        try {
            queryExecutor.executeUpdate(query, ps -> {
                ps.setBoolean(1, funFactsActivated);
                ps.setString(2, user.getEmail());
            });
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return false;
        }
        return true;
    }
}

