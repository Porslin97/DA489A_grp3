package se.myhappyplants.server.services;

import java.sql.*;

/**
 * En implementering av IQueryExecutor som använder PostgreSQL-databasen.
 */
public class DBQueryExecutor implements IQueryExecutor {
    private Connection connection;

    public DBQueryExecutor() throws SQLException {
        this.connection = DriverManager.getConnection(
                "jdbc:postgresql://pgserver.mau.se:5432/test_grp3myhappyplants",
                "ao6729",
                "i4ok3njx"
        );
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
