package se.myhappyplants.server.services;

import se.myhappyplants.server.PasswordsAndKeys;
import java.sql.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class for handling connection with a specific database
 * Created by: Frida Jacobsson 2021-05-21
 */
public class DatabaseConnection implements IDatabaseConnection {

    private java.sql.Connection conn;
    private String databaseName;

    public DatabaseConnection(String databaseName) {
        this.databaseName = databaseName;
    }

    private java.sql.Connection createConnection() throws SQLException, UnknownHostException {
        String dbServerIp = PasswordsAndKeys.dbServerIp;
        String dbServerPort = PasswordsAndKeys.dbServerPort;
        String dbUser = PasswordsAndKeys.dbUsername;
        String dbPassword = PasswordsAndKeys.dbPassword;

        if (InetAddress.getLocalHost().getHostName().equals(PasswordsAndKeys.dbHostName)) {
            dbServerIp = "localhost";
        }

        String dbURL = String.format("jdbc:postgresql://%s:%s/%s", dbServerIp, dbServerPort, databaseName);
        this.conn = DriverManager.getConnection(dbURL, dbUser, dbPassword);
        return conn;
    }

    @Override
    public Connection getConnection() {
        if (conn == null) {
            try {
                conn = createConnection();
            } catch (UnknownHostException e) {
                System.err.println("Unknown host exception: " + e.getMessage());
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("SQL exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return conn;
    }

    @Override
    public void closeConnection() {
        try {
            conn.close();
        }
        catch (SQLException sqlException) {
           //do nothing when this occurs, we don't care about this exception
        }
        conn = null;
    }
}
