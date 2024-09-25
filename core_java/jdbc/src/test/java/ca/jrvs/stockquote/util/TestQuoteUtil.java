package ca.jrvs.stockquote.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import ca.jrvs.stockquote.Quote;


public class TestQuoteUtil {
    public static void resetDB(Connection connection) throws SQLException {
        PreparedStatement deleteAll = connection.prepareStatement("DELETE FROM quote WHERE 1=1");
        deleteAll.execute();
    }

    private static void setValuesInStatement(PreparedStatement statement, Quote quote) throws SQLException {
        statement.setString(1, quote.getTicker());
        statement.setDouble(2, quote.getOpen());
        statement.setDouble(3, quote.getHigh());
        statement.setDouble(4, quote.getLow());
        statement.setDouble(5, quote.getPrice());
        statement.setInt(6, quote.getVolume());
        statement.setDate(7, quote.getLatestTradingDay());
        statement.setDouble(8, quote.getPreviousClose());
        statement.setDouble(9, quote.getChange());
        statement.setString(10, quote.getChangePercent());
        statement.setTimestamp(11, quote.getTimestamp());
    }

    public static void initDB(Connection connection) throws ParseException, SQLException{
        String INSERT_STMT = "INSERT INTO quote " + 
            "(symbol, open, high, low, price, volume, latest_trading_day, previous_close, change, change_percent, timestamp) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement insert1 = connection.prepareStatement(INSERT_STMT);
        Quote doge = getDefaultQuote();
        setValuesInStatement(insert1, doge);
        insert1.execute();
        Quote jarvis = getQuote(
            "JRVS",
            10.10,
            11.11,
            9.09,
            10.00,
            100000,
            "2020/12/20 00:00:00",
            0,
            0,
            "12%",
            "2024/09/20 09:00:00"
        );
        PreparedStatement insert2 = connection.prepareStatement(INSERT_STMT);
        setValuesInStatement(insert2, jarvis);
        insert2.execute();

        Quote testQuote = getQuote(
            "SHIBAINU",
            0.10,
            0.12,
            0.09,
            0.11,
            200000,
            "2022/12/20 00:00:00",
            0,
            0,
            "11%",
            "2023/09/20 09:00:00"
        );
        PreparedStatement insert3 = connection.prepareStatement(INSERT_STMT);
        setValuesInStatement(insert3, testQuote);
        insert3.execute();
    }
   

    private static long getDateInMillis(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        java.util.Date date = sdf.parse(dateString);
        long millis = date.getTime();
        return millis;
    }
    public static Quote getDefaultQuote() throws ParseException {
        long lastTradingDayMillis = getDateInMillis("2013/12/31 00:00:00");
        Quote quote = new Quote();
        quote.setTicker("DOGE");
        quote.setOpen(0.11);
        quote.setHigh(0.11);
        quote.setLow(0.09);
        quote.setPrice(0.10);
        quote.setVolume(10000000);
        quote.setLatestTradingDay(new Date(lastTradingDayMillis));
        quote.setPreviousClose(0.08);
        quote.setChange(0.01);
        quote.setChangePercent("10%");
        quote.setTimestamp(new Timestamp(getDateInMillis("2014/01/01 00:00:00")));
        return quote;
    }

    public static Quote getQuote(
        String ticker,
        double open,
        double high,
        double low,
        double price,
        int volume,
        String latestTradingDayStr,
        double previousClose,
        double change,
        String changePercent,
        String timestampStr
    ) throws ParseException {
        long lastTradingDayMillis = getDateInMillis(latestTradingDayStr);
        Quote quote = new Quote();
        quote.setTicker(ticker);
        quote.setOpen(open);
        quote.setHigh(high);
        quote.setLow(low);
        quote.setPrice(price);
        quote.setVolume(volume);
        quote.setLatestTradingDay(new Date(lastTradingDayMillis));
        quote.setPreviousClose(previousClose);
        quote.setChange(change);
        quote.setChangePercent(changePercent);
        quote.setTimestamp(new Timestamp(getDateInMillis(timestampStr)));
        return quote;
    }
}
