package se.myhappyplants.server.services;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Interface for defining query executor methods
 * Created by: Frida Jacobsson 2021-05-19
 */
public interface IQueryExecutor {

    void executeUpdate(String query, PreparedStatementSetter setter) throws SQLException;

    ResultSet executeQuery(String query, PreparedStatementSetter setter) throws SQLException;

    void beginTransaction() throws SQLException;

    void endTransaction() throws SQLException;

    void rollbackTransaction() throws SQLException;

    // https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/jdbc/core/PreparedStatementSetter.html
    @FunctionalInterface
    interface PreparedStatementSetter {
        void setValues(PreparedStatement ps) throws SQLException;
    }
}
