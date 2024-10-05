package ca.jrvs.stockquote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Date;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.time.Instant;

import ca.jrvs.stockquote.access.database.DatabaseUtil;
import ca.jrvs.stockquote.access.database.Position;
import ca.jrvs.stockquote.access.database.PositionDao;
import ca.jrvs.stockquote.access.database.Quote;
import ca.jrvs.stockquote.access.database.QuoteDao;
import ca.jrvs.stockquote.access.database.util.TestPositionUtil;
import ca.jrvs.stockquote.access.database.util.TestQuoteUtil;
import ca.jrvs.stockquote.access.httpexternalapi.QuoteHttpHelper;
import ca.jrvs.stockquote.service.exceptions.InvalidTickerException;
import ca.jrvs.stockquote.service.exceptions.SellMoreThanOwnedException;
import ca.jrvs.stockquote.service.exceptions.TickerNotOwnedException;
import ca.jrvs.stockquote.service.exceptions.TooManyVolumesException;

@TestInstance(Lifecycle.PER_CLASS)
public class PositionService_IntTest {
    private Connection connection;
    private PositionService positionService;
    private QuoteHttpHelper helper;

    @BeforeEach
    public void init() throws SQLException, ParseException {
        String pgUsername = System.getenv("PGUSERNAME");
        String pgPassword = System.getenv("PGPASSWORD");
        Connection connection = DatabaseUtil.getConnection("localhost", "stock_quote_test", pgUsername, pgPassword);
        QuoteDao quoteDao = new QuoteDao(connection);
        PositionDao positionDao = new PositionDao(connection);
        QuoteHttpHelper helper = mock(QuoteHttpHelper.class);
        QuoteService quoteService = new QuoteService(quoteDao, helper);
        PositionService positionService = new PositionService(positionDao, quoteDao, quoteService);
        this.positionService = positionService;
        this.connection = connection;
        this.helper = helper;
        // this.quoteService = quoteService;
        TestPositionUtil.resetDB(connection);
        TestQuoteUtil.resetDB(connection);
        TestPositionUtil.initDB(connection);
    }

    // @BeforeEach
    // public void initDB() throws SQLException, ParseException {
    //     TestPositionUtil.resetDB(connection);
    //     TestPositionUtil.initDB(connection);
    // }

    @Test
    public void buy_NonExistantStock_NotInDB() throws InvalidTickerException {
        String ticker = "QWERTY";
        // when(this.helper.fetchQuoteInfo(ticker)).thenReturn(null);
        assertThrows(InvalidTickerException.class, () -> {
            this.positionService.buy(ticker, 10, 10);
        });
    }

    @Test
    public void buy_ExistantStock_NotInDB() throws Exception {
        String ticker = "MSFT";
        // this is to avoid issues with the API limit
        Quote quote = new Quote();
        quote.setOpen(100.00);
        quote.setHigh(100.10);
        quote.setLow(99.90);
        quote.setPrice(100.00);
        quote.setVolume(100000);
        quote.setLatestTradingDay(new Date(Instant.now().getEpochSecond()));
        quote.setPreviousClose(100);
        quote.setChange(0.1);
        quote.setChangePercent("10%");
        quote.setTimestamp(new Timestamp(Instant.now().getEpochSecond()));
        quote.setTicker(ticker);
        when(this.helper.fetchQuoteInfo(ticker)).thenReturn(
            quote
        );
        // doAnswer(Optional.of(quote)).with(quoteService).fetch(any());

        Position position = this.positionService.buy(ticker, 10, 10);
        assertEquals(ticker, position.getTicker());
        assertEquals(10, position.getNumOfShares());
        assertEquals(100, position.getValuePaid());

        PreparedStatement selectQuote = connection.prepareStatement("SELECT * FROM quote WHERE symbol='MSFT'");
        PreparedStatement selectPosition = connection.prepareStatement("SELECT * FROM position WHERE symbol='MSFT'");
    
        ResultSet quoteQueryRS = selectQuote.executeQuery();
        ResultSet positionQueryRS = selectPosition.executeQuery();

        while(quoteQueryRS.next()) {
            assertEquals("MSFT", quoteQueryRS.getString("symbol"));
        }
        while(positionQueryRS.next()) {
            assertEquals("MSFT", positionQueryRS.getString("symbol"));
        }
    }

    @Test
    public void buy_moreThanQuoteVolume() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM quote WHERE symbol='DOGE';");
        ResultSet rs = preparedStatement.executeQuery();
        int i = 0;
        while(rs.next()) {
            i = rs.getInt("volume");
        }
        int numberToBuy = i + 1;
        assertThrows(TooManyVolumesException.class, () -> {
            this.positionService.buy("DOGE", numberToBuy, 10);
        });
    }

    @Test
    public void buy_StockAlreadyInDB_LessThanVolume() throws Exception {
        int numberToBuy = 10;
        String ticker = "DOGE";

        PreparedStatement selectQuote = connection.prepareStatement("SELECT * FROM quote WHERE symbol='DOGE';");
        ResultSet selectQuoteRS = selectQuote.executeQuery();
        int availableVolume = 0;

        while(selectQuoteRS.next()) {
            availableVolume = selectQuoteRS.getInt("volume");
        }
        if(availableVolume == 0) {
            fail("Available volume is zero, something went wrong with test DB");
        }

        String positionTicker = "";
        int numberOfShares = 0;
        double valuePaid = 0;

        PreparedStatement selectPosition = connection.prepareStatement("SELECT * FROM position WHERE symbol='DOGE';");
        ResultSet selectPositionRS = selectPosition.executeQuery();
        while (selectPositionRS.next()) {
            positionTicker = selectPositionRS.getString("symbol");
            numberOfShares = selectPositionRS.getInt("number_of_shares");
            valuePaid = selectPositionRS.getDouble("value_paid");
        }
        if(positionTicker.equals("") || numberOfShares == 0 || valuePaid == 0) {
            fail("Something went wrong with the DB");
        }

        // Did it return the right position?
        Position updatedPos = this.positionService.buy(ticker, numberToBuy, 10);
        assertEquals(positionTicker, updatedPos.getTicker());
        assertEquals(numberOfShares + numberToBuy, updatedPos.getNumOfShares());
        assertEquals(valuePaid + numberToBuy * 10, updatedPos.getValuePaid());

        // Is the database okay?
        PreparedStatement selectPositionUpdated = connection.prepareStatement("SELECT * FROM position WHERE symbol='DOGE';");
        ResultSet selectUpdatedPositionRS = selectPositionUpdated.executeQuery();
        while (selectUpdatedPositionRS.next()) {
            assertEquals(positionTicker, selectUpdatedPositionRS.getString("symbol"));
            assertEquals(numberOfShares + numberToBuy, selectUpdatedPositionRS.getInt("number_of_shares"));
            assertEquals(valuePaid + numberToBuy * 10, selectUpdatedPositionRS.getDouble("value_paid"));
        }
        // Did the quote get updated correctly?
        PreparedStatement selectUpdatedQuote = connection.prepareStatement("SELECT * FROM quote WHERE symbol='DOGE';");
        ResultSet selectUpdatedQuoteRS = selectUpdatedQuote.executeQuery();
        int availableVolumeUpdated = 0;
        while(selectUpdatedQuoteRS.next()) {
            availableVolumeUpdated = selectUpdatedQuoteRS.getInt("volume");
        }
        assertEquals(availableVolume - numberToBuy, availableVolumeUpdated);
    }

    @Test
    public void buy_StockNotInDB_LessThanVolume() throws Exception {
        int numberToBuy = 10;
        boolean isInDB = false;
        String ticker = "MSFT";
        double price = 10;
        // this is to avoid issues with the API limit
        Quote quote = new Quote();
        quote.setOpen(100.00);
        quote.setHigh(100.10);
        quote.setLow(99.90);
        quote.setPrice(100.00);
        quote.setVolume(100000);
        quote.setLatestTradingDay(new Date(Instant.now().getEpochSecond()));
        quote.setPreviousClose(100);
        quote.setChange(0.1);
        quote.setChangePercent("10%");
        quote.setTimestamp(new Timestamp(Instant.now().getEpochSecond()));
        quote.setTicker(ticker);
        when(this.helper.fetchQuoteInfo(ticker)).thenReturn(
            quote
        );
        // make sure this is actually something not in DB
        PreparedStatement checkIfTickerExistsBeforeBuying = connection.prepareStatement("SELECT * FROM quote WHERE symbol='" + ticker + "';");
        ResultSet checkIfTickerExistsBeforeBuyingRS = checkIfTickerExistsBeforeBuying.executeQuery();
        while(checkIfTickerExistsBeforeBuyingRS.next()) {
            isInDB = true;
        }
        if(isInDB) {
            fail("Test database was modified, change the ticker");
        }
        
        // buy -- is the position correct?
        Position position = this.positionService.buy(ticker, numberToBuy, price);
        assertEquals(ticker, position.getTicker());
        assertEquals(numberToBuy, position.getNumOfShares());
        assertEquals(numberToBuy * price, position.getValuePaid());

        // Is the position okay in the database
        PreparedStatement checkIfPositionIsInDB = connection.prepareStatement("SELECT * FROM position WHERE symbol='" + ticker + "';");
        ResultSet checkIfPositionIsInDBRS = checkIfPositionIsInDB.executeQuery();
        while (checkIfPositionIsInDBRS.next()) {
            assertEquals(ticker, checkIfPositionIsInDBRS.getString("symbol"));
            assertEquals(numberToBuy, checkIfPositionIsInDBRS.getInt("number_of_shares"));
            assertEquals(numberToBuy * price, checkIfPositionIsInDBRS.getDouble("value_paid"));
        }

        // Is the quote saved in the database
        PreparedStatement checkIfQuoteIsOkayInDB = connection.prepareStatement("SELECT * FROM quote WHERE symbol='" + ticker + "';");
        ResultSet checkIfQuoteIsOkayInDBRS = checkIfQuoteIsOkayInDB.executeQuery();
        String generatedTicker = "";
        while (checkIfQuoteIsOkayInDBRS.next()) {
            generatedTicker =  checkIfQuoteIsOkayInDBRS.getString("symbol");
        }
        assertEquals(ticker, generatedTicker);
    }

    @Test
    public void testSell_NonExistantStock() throws SQLException, InvalidTickerException {
        String ticker = "QWERTY";
        int numberOfShares = 10;
        int price = 10;
        // when(this.helper.fetchQuoteInfo(ticker)).thenThrow(InvalidTickerException.class);
        // check not in database
        PreparedStatement isTickerInDB = connection.prepareStatement("SELECT * FROM position WHERE symbol='" + ticker + "';");
        ResultSet isTickerInDBRS = isTickerInDB.executeQuery();
        while(isTickerInDBRS.next()) {
            fail("FAIL: ticker " + ticker + " is not supposed to be in test database");
        }
        // assert throws
        assertThrows(TickerNotOwnedException.class, () -> {
            this.positionService.sell(ticker, numberOfShares, price);
        });
        // assertEquals("Cannot sell " + ticker + ": cannot sell a position that is not owned", e.getMessage());
    }

    @Test
    public void testSell_MoreThanOwned() throws SQLException {
        String ticker = "DOGE";
        int numberOfSharesOwned = 0;
        PreparedStatement selectTickerPos = connection.prepareStatement("SELECT * FROM position WHERE symbol='" + ticker + "';");
        ResultSet selectTickerPosRS = selectTickerPos.executeQuery();
        while (selectTickerPosRS.next()) {
            numberOfSharesOwned = selectTickerPosRS.getInt("number_of_shares");
        }
        if(numberOfSharesOwned == 0) {
            fail("Test database not setup correctly : " + ticker + " should NOT have 0 shares");
        }
        int numberToBuy = numberOfSharesOwned + 1;
        assertThrows(SellMoreThanOwnedException.class, () -> {
            this.positionService.sell(ticker, numberToBuy, 10);
        });
        // assertEquals("Cannot sell more than owned", e.getMessage());
    }

    @Test
    public void testSell_SellExistingStockLessThanAmountOwned() throws Exception {
        String ticker = "DOGE";
        int numberToSell = 10;
        double price = 0.1;
        int volume = 0;
        PreparedStatement getQuoteVolume = connection.prepareStatement("SELECT volume FROM quote WHERE symbol='" + ticker + "';");
        ResultSet getQuoteVolumeRS = getQuoteVolume.executeQuery();
        while(getQuoteVolumeRS.next()) {
            volume = getQuoteVolumeRS.getInt("volume");
        }
        
        Position defaultPosition = TestPositionUtil.getDefaultPosition();
        Position returnedPosition = this.positionService.sell(ticker, numberToSell, price);

        assertEquals(defaultPosition.getTicker(), returnedPosition.getTicker());
        assertEquals(defaultPosition.getNumOfShares() - numberToSell, returnedPosition.getNumOfShares());
        assertEquals(defaultPosition.getValuePaid() - numberToSell * price, returnedPosition.getValuePaid());

        // is the quote okay?
        PreparedStatement getQuoteVolumeAfterSell = connection.prepareStatement("SELECT volume FROM quote WHERE symbol='" + ticker + "';");
        ResultSet getQuoteVolumeAfterSellRS = getQuoteVolumeAfterSell.executeQuery();
        int volumeAfterSell = -1;
        while(getQuoteVolumeAfterSellRS.next()) {
            volumeAfterSell = getQuoteVolumeAfterSellRS.getInt("volume");
        }
        assertEquals(volume + numberToSell, volumeAfterSell);

        // is the position in the DB okay?
        PreparedStatement checkPosition = connection.prepareStatement("SELECT * FROM position WHERE symbol='" + ticker + "';");
        ResultSet checkPositionRS = checkPosition.executeQuery();
        boolean whileRan = false;
        while (checkPositionRS.next()) {
            whileRan = true;
            assertEquals(defaultPosition.getTicker(), checkPositionRS.getString("symbol"));
            assertEquals(defaultPosition.getNumOfShares() - numberToSell, checkPositionRS.getInt("number_of_shares"));
            assertEquals(defaultPosition.getValuePaid() - numberToSell * price, checkPositionRS.getDouble("value_paid"));
        }
        if(!whileRan) {
            fail("Position is not in DB");
        }
    }

}
