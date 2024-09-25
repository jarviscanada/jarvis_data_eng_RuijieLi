package ca.jrvs.stockquote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import ca.jrvs.stockquote.util.TestPositionUtil;
import ca.jrvs.stockquote.util.TestQuoteUtil;

@TestInstance(Lifecycle.PER_CLASS)
public class QuoteDao_Test {
    QuoteDao quoteDao;
    Connection connection;

    @BeforeAll
    public void initConnection() throws SQLException{
        String pgUsername = System.getenv("PGUSERNAME");
        String pgPassword = System.getenv("PGPASSWORD");
        Connection connection = DatabaseUtil.getConnection("localhost", "stock_quote_test", pgUsername, pgPassword);
        this.quoteDao = new QuoteDao(connection);
        this.connection = connection;
        TestPositionUtil.resetDB(connection);
        TestQuoteUtil.resetDB(connection);
    }

    @BeforeEach
    public void initTest() throws SQLException, ParseException {
        TestQuoteUtil.initDB(this.connection);
    }

    @AfterEach
    public void resetDB() throws SQLException {
        TestPositionUtil.resetDB(connection);
        TestQuoteUtil.resetDB(connection);
    }

    @Test
    public void testCreateNew() throws ParseException {
        Quote testQuote = TestQuoteUtil.getQuote(
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
        Quote doge = TestQuoteUtil.getDefaultQuote();
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
        Quote doge = TestQuoteUtil.getDefaultQuote();
        doge.setLow(0);
        doge.setHigh(1.12);
        Quote updated = this.quoteDao.saveExisting(doge);
        assertTrue(doge.equals(updated));
    }

    @Test
    public void testSave_Existing() throws ParseException {
        Quote doge = TestQuoteUtil.getDefaultQuote();
        doge.setHigh(1.10);
        doge.setLow(0);
        Quote updated = this.quoteDao.save(doge);
        assertTrue(doge.equals(updated));
    }

    @Test
    public void testSave_New() throws ParseException {
        Quote quoteToInsert = TestQuoteUtil.getQuote(
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
        assertEquals(3, quotes.size());
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
        assertEquals(3, total);
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
