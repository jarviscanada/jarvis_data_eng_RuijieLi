package ca.jrvs.stockquote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class QuoteDao_Test {
    // Connection connection;
    QuoteDao quoteDao;
    Connection connection;
    @BeforeEach
    public void init() throws SQLException, ParseException {
        String pgUsername = System.getenv("PGUSERNAME");
        String pgPassword = System.getenv("PGPASSWORD");
        Connection connection = DatabaseUtil.getConnection("localhost", "stock_quote_test", pgUsername, pgPassword);
        this.quoteDao = new QuoteDao(connection);
        this.connection = connection;
        // this.quoteDao.deleteAll();
        PreparedStatement deleteAll = connection.prepareStatement("DELETE FROM quote WHERE 1=1");
        deleteAll.execute();
        String INSERT_STMT = "INSERT INTO quote " + 
            "(symbol, open, high, low, price, volume, latest_trading_day, previous_close, change, change_percent, timestamp) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PreparedStatement insert1 = connection.prepareStatement(INSERT_STMT);
        Quote doge = this.getDefaultQuote();
        insert1.setString(1, doge.getTicker());
        insert1.setDouble(2, doge.getOpen());
        insert1.setDouble(3, doge.getHigh());
        insert1.setDouble(4, doge.getLow());
        insert1.setDouble(5, doge.getPrice());
        insert1.setInt(6, doge.getVolume());
        insert1.setDate(7, doge.getLatestTradingDay());
        insert1.setDouble(8, doge.getPreviousClose());
        insert1.setDouble(9, doge.getChange());
        insert1.setString(10, doge.getChangePercent());
        insert1.setTimestamp(11, doge.getTimestamp());
        insert1.execute();
        Quote jarvis = this.getQuote(
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
        insert2.setString(1, jarvis.getTicker());
        insert2.setDouble(2, jarvis.getOpen());
        insert2.setDouble(3, jarvis.getHigh());
        insert2.setDouble(4, jarvis.getLow());
        insert2.setDouble(5, jarvis.getPrice());
        insert2.setInt(6, jarvis.getVolume());
        insert2.setDate(7, jarvis.getLatestTradingDay());
        insert2.setDouble(8, jarvis.getPreviousClose());
        insert2.setDouble(9, jarvis.getChange());
        insert2.setString(10, jarvis.getChangePercent());
        insert2.setTimestamp(11, jarvis.getTimestamp());
        insert2.execute();
    }

    private long getDateInMillis(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        java.util.Date date = sdf.parse(dateString);
        long millis = date.getTime();
        return millis;
    }
    private Quote getDefaultQuote() throws ParseException {
        long lastTradingDayMillis = this.getDateInMillis("2013/12/31 00:00:00");
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
        quote.setTimestamp(new Timestamp(this.getDateInMillis("2014/01/01 00:00:00")));
        return quote;
    }

    private Quote getQuote(
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
        long lastTradingDayMillis = this.getDateInMillis(latestTradingDayStr);
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
        quote.setTimestamp(new Timestamp(this.getDateInMillis(timestampStr)));
        return quote;
    }

    @Test
    public void testCreateNew() throws ParseException {
        Quote testQuote = getQuote(
            "TEST",
            12.34,
            13.45,
            09.87,
            12.00,
            100000,
            "2023/12/31 00:00:00",
            12,
            0.12,
            "12%",
            "2024/01/21 09:00:00"
        );
        Quote insertedQuote = this.quoteDao.createNew(testQuote);
        assertTrue(testQuote.equals(insertedQuote));
    }

    @Test
    public void testFindById() throws ParseException {
        Quote doge = this.getDefaultQuote();
        Quote found = this.quoteDao.findById("DOGE").get();
        assertTrue(doge.equals(found));
    }

    @Test
    public void testFindByIdEmpty() {
        String testID = "HAND ON KEYBOARD HUIFSEFIEBGSDFIBGSDF";
        assertTrue(!this.quoteDao.findById(testID).isPresent());
    }

    @Test
    public void testSaveExisting() throws ParseException {
        Quote doge = getDefaultQuote();
        doge.setLow(0);
        doge.setHigh(1.12);
        Quote updated = this.quoteDao.saveExisting(doge);
        assertTrue(doge.equals(updated));
    }

    @Test
    public void testSave_Existing() throws ParseException {
        Quote doge = this.getDefaultQuote();
        doge.setHigh(1.10);
        doge.setLow(0);
        Quote updated = this.quoteDao.save(doge);
        assertTrue(doge.equals(updated));
    }

    @Test
    public void testSave_New() throws ParseException {
        Quote quoteToInsert = getQuote(
            "TEST",
            12.34,
            13.45,
            09.87,
            12.00,
            100000,
            "2023/12/31 00:00:00",
            12,
            0.12,
            "12%",
            "2024/01/21 09:00:00"
        );
        Quote insertedQuote = this.quoteDao.createNew(quoteToInsert);
        assertTrue(quoteToInsert.equals(insertedQuote));
    }

    @Test
    public void testSave_Null() {
        IllegalArgumentException e = assertThrows(
            IllegalArgumentException.class, () -> {
                this.quoteDao.save(null);
            }
        );
        assertEquals("Quote cannot be null", e.getMessage());
    }

    @Test
    public void testFindAll() {
        List<Quote> quotes = (ArrayList<Quote>)this.quoteDao.findAll();
        assertEquals(2, quotes.size());
    }
    @Test
    public void testDeleteById() throws SQLException {
        this.quoteDao.deleteById("JRVS");
        PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM quote WHERE symbol='JRVS'");
        ResultSet rs = preparedStatement.executeQuery();
        int i = 0;
        while(rs.next()) {
            i++;
        }
        assertEquals(0, i);
    }

    @Test
    public void testDeleteAll() throws SQLException {
        PreparedStatement preparedStatement = this.connection.prepareStatement("SELECT * FROM quote");
        int total = 0;
        ResultSet rs = preparedStatement.executeQuery();
        while(rs.next()) {
            total++;
        }
        assertEquals(2, total);
        this.quoteDao.deleteAll();
        PreparedStatement preparedStatement2 = this.connection.prepareStatement("SELECT * FROM quote");
        int total2 = 0;
        ResultSet rs2 = preparedStatement2.executeQuery();
        while(rs2.next()) {
            total2++;
        }
        assertEquals(0, total2);
    }
}
