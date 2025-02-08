package se.myhappyplants.server.services;

import java.sql.*;

/**
 * En implementering av IQueryExecutor som använder en H2 in-memory-databas.
 */
public class H2QueryExecutor implements IQueryExecutor {
    private Connection connection;

    public H2QueryExecutor() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
    }

    @Override
    public void executeUpdate(String query, PreparedStatementSetter setter) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            setter.setValues(stmt);
            stmt.executeUpdate();
        }
    }

    @Override
    public ResultSet executeQuery(String query, PreparedStatementSetter setter) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(query);
        setter.setValues(stmt);
        return stmt.executeQuery();
    }

    @Override
    public void beginTransaction() throws SQLException {
        connection.setAutoCommit(false);
    }

    @Override
    public void endTransaction() throws SQLException {
        connection.commit();
        connection.setAutoCommit(true);
    }

    @Override
    public void rollbackTransaction() throws SQLException {
        connection.rollback();
        connection.setAutoCommit(true);
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
