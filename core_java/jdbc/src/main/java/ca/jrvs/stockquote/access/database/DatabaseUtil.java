package ca.jrvs.stockquote.access.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


import org.slf4j.Logger;

public class DatabaseUtil {
    private static Logger logger;
    public static Connection getConnection(
        String host,
        String dbname,
        String userName,
        String password
    ) {
        String url = "jdbc:postgresql://" + host + "/" + dbname;
        Properties properties = new Properties();
        properties.setProperty("user", userName);
        properties.setProperty("password", password);

        try {
            return DriverManager.getConnection(url, properties);
        } catch (SQLException e) {
            logger.error("Error while connecting to database " + url + " with username " + userName, e);
            throw new RuntimeException(e);
        }
    }
}
