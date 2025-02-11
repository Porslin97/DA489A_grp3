package se.myhappyplants.server.services;
import static se.myhappyplants.server.PasswordsAndKeys.*;

import java.sql.*;

    /**
    * En implementering av IQueryExecutor som använder en H2 in-memory-databas.
    * En implementering av IQueryExecutor som använder PostgreSQL-databasen.
    */
    public class DBQueryExecutor implements IQueryExecutor {
        private Connection connection;

    public DBQueryExecutor() throws SQLException {

        this.connection = DriverManager.getConnection(
                "jdbc:postgresql://" + dbServerIp2 + ":" + dbServerPort2 + "/test_grp3myhappyplants",
                dbUsername2,
                dbPassword2
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
