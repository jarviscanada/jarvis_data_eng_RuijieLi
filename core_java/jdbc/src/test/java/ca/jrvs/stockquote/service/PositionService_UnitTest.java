package ca.jrvs.stockquote.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.jrvs.stockquote.access.database.Position;
import ca.jrvs.stockquote.access.database.PositionDao;
import ca.jrvs.stockquote.access.database.Quote;
import ca.jrvs.stockquote.access.database.QuoteDao;
import ca.jrvs.stockquote.access.database.util.TestPositionUtil;
import ca.jrvs.stockquote.access.database.util.TestQuoteUtil;
import ca.jrvs.stockquote.service.exceptions.InvalidTickerException;
import ca.jrvs.stockquote.service.exceptions.SellMoreThanOwnedException;
import ca.jrvs.stockquote.service.exceptions.TickerNotOwnedException;
import ca.jrvs.stockquote.service.exceptions.TooManyVolumesException;

// @TestInstance(Lifecycle.PER_CLASS)
public class PositionService_UnitTest {
    private PositionDao positionDao;
    private QuoteDao quoteDao;
    private QuoteService quoteService;
    private PositionService positionService;

    @BeforeEach
    public void init() {
        this.positionDao        = mock(PositionDao.class);
        this.quoteDao           = mock(QuoteDao.class);
        this.quoteService       = mock(QuoteService.class);
        this.positionService    = new PositionService(positionDao, quoteDao, quoteService);
    }
    @Test
    public void testBuy_NonExistantStock() throws Exception {
        String fakeID = "SEEILQHELFSNALFNLANFAKNSLFSJJFHDSDSD";
        // String expectedMsg = "Ticker " + fakeID + " does not exist";
        when(this.quoteDao.findById(fakeID)).thenReturn(Optional.empty());
        when(this.quoteService.fetchQuoteDataFromAPI(fakeID)).thenReturn(Optional.empty());
        assertThrows(InvalidTickerException.class, () -> {
            this.positionService.buy(fakeID, 100, 100);
        });
        // } catch(Exception e){
        //     System.out.println("0000000000000000000000000000000000000");
        //     e.printStackTrace();
        // }
    }
    @Test
    public void testBuy_ValidStockNotInDB_MoreThanVolume() throws Exception {
        String symbol = "DOGE";
        Quote defaultQuote = TestQuoteUtil.getDefaultQuote();
    
        when(this.quoteDao.findById(symbol)).thenReturn(Optional.empty());
        when(this.quoteService.fetchQuoteDataFromAPI(symbol)).thenReturn(Optional.of(defaultQuote));

        assertThrows(InvalidTickerException.class, () -> {
            this.positionService.buy(symbol, defaultQuote.getVolume() + 1, defaultQuote.getPrice());
        });
    }

    @Test
    public void testBuy_ValidStockInDB_MoreThanVolume() throws Exception {
        String symbol = "DOGE";
        Quote defaultQuote = TestQuoteUtil.getDefaultQuote();
        when(this.quoteService.fetch(symbol)).thenReturn(Optional.of(defaultQuote));
        assertThrows(TooManyVolumesException.class, () -> {
            this.positionService.buy(symbol, defaultQuote.getVolume() + 1, defaultQuote.getPrice());
        });
        verify(this.quoteService, times(0)).fetchQuoteDataFromAPI(symbol);
    }

    @Test
    public void testBuy_ValidStockInDB_newPosition() throws Exception {
        String testID = "TEST";
        Position positionToReturn = new Position();
        positionToReturn.setNumOfShares(10);
        positionToReturn.setValuePaid(1000);
        positionToReturn.setTicker(testID);

        Quote quoteToReturn = TestQuoteUtil.getQuote(
            testID,
            0,
            0,
            0,
            0,
            10000,
            "2020/12/31 00:00:00",
            0,
            0,
            "10%",
            "2024/09/25 00:00:00"
        );

        when(this.quoteService.fetch(testID)).thenReturn(Optional.of(quoteToReturn));
        when(this.positionDao.findById(testID)).thenReturn(Optional.empty());
        when(this.positionDao.save(any())).thenReturn(positionToReturn);
    
        Position returnedPosition = this.positionService.buy(testID, 10, 100);

        assertEquals(testID, returnedPosition.getTicker());
        assertEquals(10, returnedPosition.getNumOfShares());
        assertEquals(1000, returnedPosition.getValuePaid());

        Quote quoteToSave = TestQuoteUtil.getQuote(
            testID,
            0,
            0,
            0,
            0,
            10000 - positionToReturn.getNumOfShares(),
            "2020/12/31 00:00:00",
            0,
            0,
            "10%",
            "2024/09/25 00:00:00"
        );

        verify(this.quoteDao, times(1)).save(argThat((calledQuote) -> {
            return calledQuote.equals(quoteToSave);
        }));
    }

    @Test
    public void testBuy_validStock_ExistingPosition() throws Exception {
        String testID = "DOGE";
        int nShares = 10;
        double nPrice = 0.11;

        Quote quoteToReturn = TestQuoteUtil.getDefaultQuote();
        Position positionToReturn = TestPositionUtil.getDefaultPosition();
    
        Position positionToSave = TestPositionUtil.getDefaultPosition();
        positionToSave.setNumOfShares(positionToReturn.getNumOfShares() + nShares);
        positionToSave.setValuePaid(positionToReturn.getValuePaid() + nPrice * (double)nShares);
        
        when(this.quoteService.fetch(testID)).thenReturn(Optional.of(quoteToReturn));
        when(this.positionDao.findById(testID)).thenReturn(Optional.of(positionToReturn));
        when(this.positionDao.save(any())).thenReturn(positionToReturn);

        Position savedPosition = this.positionService.buy(testID, nShares, nPrice);

        assertEquals(testID, savedPosition.getTicker());
        assertEquals(positionToSave.getNumOfShares(), savedPosition.getNumOfShares());
        assertEquals(positionToSave.getValuePaid(), savedPosition.getValuePaid());

        Quote quoteToSave = TestQuoteUtil.getDefaultQuote();
        quoteToSave.setVolume(quoteToSave.getVolume() - nShares);

        verify(this.quoteDao, times(1)).save(argThat((calledQuote) -> {
            return calledQuote.equals(quoteToSave);
        }));
    }

    @Test
    public void testSell_NonOwnedStock() {
        String testID = "DKFSLFSKLDFJDSKLFJLSDFJ";
        when(this.positionDao.findById(testID)).thenReturn(Optional.empty());
        TickerNotOwnedException e = assertThrows(TickerNotOwnedException.class, () -> {
            this.positionService.sell(testID, 10, 10);
        });
        assertEquals("Cannot sell " + testID + ": cannot sell a position that is not owned", e.getMessage());
    }

    @Test
    public void testSell_TooManySold() {
        Position position = TestPositionUtil.getDefaultPosition();
        when(this.positionDao.findById(position.getTicker())).thenReturn(Optional.of(position));
        SellMoreThanOwnedException e = assertThrows(SellMoreThanOwnedException.class, () -> {
            this.positionService.sell(position.getTicker(), position.getNumOfShares() + 1, 10);
        });
        assertEquals("Cannot sell more than owned", e.getMessage());
    }

    @Test
    public void testSell_GoodStock() throws Exception {
        // return default quote and default position (DOGE)
        Position position = TestPositionUtil.getDefaultPosition();
        Quote quote = TestQuoteUtil.getDefaultQuote();
        when(this.quoteDao.findById(quote.getTicker())).thenReturn(Optional.of(quote));
        when(this.positionDao.findById(position.getTicker())).thenReturn(Optional.of(position));

        int numberOfShares = 1;
        double price = 0.11;

        // position that will be saved to the DB
        Position positionToSave = TestPositionUtil.getDefaultPosition();
        positionToSave.setNumOfShares(positionToSave.getNumOfShares() - numberOfShares);
        positionToSave.setValuePaid(positionToSave.getValuePaid() - price * (double)numberOfShares);

        // quote that will be saved to the DB
        Quote quoteToSave = TestQuoteUtil.getDefaultQuote();
        quoteToSave.setVolume(quoteToSave.getVolume() + numberOfShares);

        when(this.quoteService.fetch(position.getTicker())).thenReturn(Optional.of(quoteToSave));
        when(this.positionDao.save(any())).thenReturn(positionToSave);
        this.positionService.sell(positionToSave.getTicker(), numberOfShares, price);

        verify(this.positionDao, times(1)).save(argThat((savedPosition)-> {
            return savedPosition.equals(positionToSave);
        }));
        verify(this.quoteDao, times(1)).save(argThat((savedQuote) -> {
            return savedQuote.equals(quoteToSave);
        }));

    }
}
