package ca.jrvs.stockquote.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;

import ca.jrvs.stockquote.Position;

public class TestPositionUtil {

    public static void resetDB(Connection connection) throws SQLException {
        PreparedStatement deleteAll = connection.prepareStatement("DELETE FROM position WHERE 1=1");
        deleteAll.execute();
        TestQuoteUtil.resetDB(connection);
    }
    public static void initDB(Connection connection) throws SQLException, ParseException {
        TestQuoteUtil.initDB(connection);
        String INSERT_INTO = "INSERT INTO position " + 
        " (symbol, number_of_shares, value_paid) " +
        "VALUES (?, ?, ?);";

        PreparedStatement insert1 = connection.prepareStatement(INSERT_INTO);
        Position doge = getDefaultPosition();
        insert1.setString(1, doge.getTicker());
        insert1.setInt(2, doge.getNumOfShares());
        insert1.setDouble(3, doge.getValuePaid());
        insert1.execute();

        PreparedStatement insert2 = connection.prepareStatement(INSERT_INTO);
        Position jarvis = getPosition("JRVS", 100, 1000);
        insert2.setString(1, jarvis.getTicker());
        insert2.setInt(2, jarvis.getNumOfShares());
        insert2.setDouble(3, jarvis.getValuePaid());
        insert2.execute();
    }
    public static Position getDefaultPosition() {
        Position position = new Position();
        position.setTicker("DOGE");
        position.setNumOfShares(5000);
        position.setValuePaid(515.75);
        return position;
    }
    public static Position getPosition(String ticker, int numberOfShares, double valuePaid) {
        Position position = new Position();
        position.setTicker(ticker);
        position.setNumOfShares(numberOfShares);
        position.setValuePaid(valuePaid);
        return position;
    }
}
