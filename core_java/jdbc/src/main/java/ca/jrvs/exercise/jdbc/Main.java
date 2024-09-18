package ca.jrvs.exercise.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    static Logger logger;
    public static void main(String[] args) {
        DatabaseConnectionManager dcm = new DatabaseConnectionManager(
            "localhost:5432",
            "hplussport",
            "postgres",
            "password"
        );
        Main.logger = LoggerFactory.getLogger(Main.class);
        BasicConfigurator.configure();
        try {
            Connection connection = dcm.getConnection();
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) FROM customer;");
            while (resultSet.next()) {
                System.out.println(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            logger.error("DB exception ", e);
//            ;
        }
    }
}