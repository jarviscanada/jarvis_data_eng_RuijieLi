package ca.jrvs.stockquote.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.jrvs.stockquote.access.database.Quote;
import ca.jrvs.stockquote.access.database.QuoteDao;
import ca.jrvs.stockquote.access.database.util.TestQuoteUtil;
import ca.jrvs.stockquote.access.httpexternalapi.QuoteHttpHelper;
import ca.jrvs.stockquote.service.exceptions.InvalidTickerException;

public class QuoteService_UnitTest {
    QuoteHttpHelper helper;
    QuoteDao quoteDao;
    QuoteService quoteService;
    @BeforeEach
    public void init() {
        this.helper = mock(QuoteHttpHelper.class);
        this.quoteDao = mock(QuoteDao.class);
        this.quoteService = new QuoteService(quoteDao, helper);
    }

    @Test
    public void testFetchQuoteDataFromAPI_realStock() throws Exception {
        Quote defaultQuote = TestQuoteUtil.getDefaultQuote();
        when(this.helper.fetchQuoteInfo("DOGE")).thenReturn(defaultQuote);
        when(this.quoteDao.save(any(Quote.class))).thenReturn(defaultQuote);
        assertTrue(this.quoteService.fetchQuoteDataFromAPI("DOGE").get().equals(defaultQuote));
    }
    @Test
    public void testFetchQuoteDataFromAPI_empty() throws Exception {
        // when(this.helper.fetchQuoteInfo("SLSDJFLS")).thenReturn(null);
        // assertTrue(!this.quoteService.fetchQuoteDataFromAPI("SLSDJFLS").isPresent());
        assertThrows(InvalidTickerException.class, () -> {
            this.helper.fetchQuoteInfo("SLSDJFLS");
        });
    }
}
