package ca.jrvs.exercise.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionManager {
    private final String url;
    private final Properties properties;
    public DatabaseConnectionManager(
        String host,
        String dbname,
        String userName,
        String password
    ) {
        this.url = "jdbc:postgresql://" + host + "/" + dbname;
        System.out.println(this.url);
        this.properties = new Properties();
        this.properties.setProperty("user", userName);
        this.properties.setProperty("password", password);
    }
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.url, this.properties);
    }
}
