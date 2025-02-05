package se.myhappyplants.server.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for handling querys to database
 * Created by: Frida Jacobsson 2021-05-21
 */
public class QueryExecutor implements IQueryExecutor {

    private IDatabaseConnection connection;

    public QueryExecutor(IDatabaseConnection connection) {
        this.connection = connection;
    }

    @Override
    public void executeUpdate(String query, PreparedStatementSetter setter) throws SQLException {
        int retries = 0;
        while (retries < 3) {
            try (PreparedStatement ps = this.connection.getConnection().prepareStatement(query)) {
                if (setter != null) {
                    setter.setValues(ps);
                }
                ps.executeUpdate();
                return;
            } catch (SQLException sqlException) {
                System.err.println("SQLException on attempt " + (retries + 1) + ": " + sqlException.getMessage());
                connection.closeConnection();
                retries++;
            }
        }
        throw new SQLException("Failed to execute update after 3 attempts");
    }


    @Override
    public ResultSet executeQuery(String query, PreparedStatementSetter setter) throws SQLException {
        int retries = 0;
        while (retries < 3) {
            try {
                PreparedStatement ps = connection.getConnection().prepareStatement(query);

                if (setter != null) {
                    setter.setValues(ps);
                }

                return ps.executeQuery();
            } catch (SQLException sqlException) {
                System.err.println("SQLException on attempt " + (retries + 1) + ": " + sqlException.getMessage());
                connection.closeConnection();
                retries++;
            }
        }
        throw new SQLException("Failed to execute query after 3 attempts");
    }


    @Override
    public void beginTransaction() throws SQLException {
        int retries = 0;
        while (retries < 3) {
            try {
                connection.getConnection().setAutoCommit(false);
                return;
            } catch (SQLException sqlException) {
                connection.closeConnection();
                retries++;
            }
        }
        throw new SQLException("Failed to begin transaction after 3 attempts");
    }

    @Override
    public void endTransaction() throws SQLException {
        try {
            connection.getConnection().commit();
        } finally {
            connection.getConnection().setAutoCommit(true);
        }
    }

    @Override
    public void rollbackTransaction() throws SQLException {
        connection.getConnection().rollback();
    }
}
