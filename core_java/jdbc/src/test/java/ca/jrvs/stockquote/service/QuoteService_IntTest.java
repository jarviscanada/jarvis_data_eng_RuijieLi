package ca.jrvs.stockquote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.text.ParseException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import ca.jrvs.stockquote.access.database.DatabaseUtil;
import ca.jrvs.stockquote.access.database.QuoteDao;
import ca.jrvs.stockquote.access.database.util.TestPositionUtil;
import ca.jrvs.stockquote.access.database.util.TestQuoteUtil;
import ca.jrvs.stockquote.access.httpexternalapi.QuoteHttpHelper;


@TestInstance(Lifecycle.PER_CLASS)
public class QuoteService_IntTest {

    private Connection connection;
    private QuoteDao quoteDao;
    private QuoteService quoteService;

    @BeforeAll
    public void init() {
        QuoteHttpHelper helper = new QuoteHttpHelper();
        String pgUsername = System.getenv("PGUSERNAME");
        String pgPassword = System.getenv("PGPASSWORD");
        Connection connection = DatabaseUtil.getConnection("localhost", "stock_quote", pgUsername, pgPassword);
        this.connection = connection;
        this.quoteDao = new QuoteDao(connection);
        // this.httpHelper = helper;
        this.quoteService = new QuoteService(quoteDao, helper);
    }

    @BeforeEach
    public void initDB() throws SQLException, ParseException {
        TestPositionUtil.resetDB(connection);
        TestPositionUtil.initDB(connection);
    }

    @Test
    public void testGetQuote_Null() {
        assertTrue(!this.quoteService.fetchQuoteDataFromAPI("DOGE").isPresent());
    }
    @Test
    public void testGetQuote_Real() {
    }
}
