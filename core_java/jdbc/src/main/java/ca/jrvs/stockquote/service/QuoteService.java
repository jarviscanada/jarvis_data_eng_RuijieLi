package ca.jrvs.stockquote.service;

import java.util.List;
import java.util.Optional;
import org.apache.log4j.Logger;

import ca.jrvs.stockquote.access.database.Quote;
import ca.jrvs.stockquote.access.database.QuoteDao;
import ca.jrvs.stockquote.access.httpexternalapi.QuoteHttpHelper;
import ca.jrvs.stockquote.service.exceptions.InvalidTickerException;
import ca.jrvs.stockquote.util.StackTraceUtil;

public class QuoteService {
    private QuoteDao dao;
    private QuoteHttpHelper httpHelper;
    private static Logger logger;
    public QuoteService(QuoteDao dao, QuoteHttpHelper helper) {
        logger = Logger.getLogger(QuoteService.class);
        this.dao = dao;
        this.httpHelper = helper;
        logger.info("Quote service initialized");
    }

    /**
     * Fetches latest quote data from endpoint
     * @param ticker
     * @return Latest quote information or empty optional if ticker symbol not found
     */
    public Optional<Quote> fetchQuoteDataFromAPI(String ticker) throws InvalidTickerException {
        logger.info("Fetching " + ticker + " from API");
        Quote quote = this.httpHelper.fetchQuoteInfo(ticker);

        if(quote == null) {
            logger.info("Ticker " + ticker + " does not exist. Throwing InvalidTickerException");
            throw new InvalidTickerException(ticker + " does not exist");
        }
        return Optional.of(this.dao.save(quote));
    }

    public Optional<Quote> fetchFromDB(String ticker) throws InvalidTickerException {
        logger.info("Fetching " + ticker + " from DB");
        
        Optional<Quote> quote = this.dao.findById(ticker);
        if(!quote.isPresent()) {
            throw new InvalidTickerException(ticker + "is not valid");
        }
        return quote;
    }

    public Optional<Quote> fetch(String ticker) throws InvalidTickerException{
        logger.info("Fetching " + ticker + "(check API if not in DB)");
        Optional<Quote> quote = this.fetchFromDB(ticker);
        return quote.isPresent() ? quote : this.fetchQuoteDataFromAPI(ticker);
    }

    public List<Quote> fetchAll() {
        logger.info("Fetching all stocks inside DB");
        return (List<Quote>)dao.findAll();
    }

    public void updateAll() {
        logger.info("Updating all stocks");
        List<Quote> quotes = this.fetchAll();
        String currentQuote = "";
        for(Quote quote: quotes) {
            try{
                System.out.println("Updating: " + quote.getTicker() + " (last updated: " + quote.getTimestamp() + " )");
                currentQuote = quote.getTicker();
                Optional<Quote> updated = this.fetchQuoteDataFromAPI(quote.getTicker());
                if(updated.isPresent()) {
                    System.out.println("Updated : " + updated.get().getTicker() + " (last updated: " + updated.get().getTimestamp() + " )");
                } else {
                    System.out.println("Updating  " + quote.getTicker() + " failed");
                }
            } catch(InvalidTickerException e) {
                logger.error("Caught InvalidTickerException : " + currentQuote + " is not a valid stock");
                System.out.println(currentQuote + " is not a valid stock.");
                
            } catch(Exception e) {
                logger.error("An unexpected problem occured while updating " + currentQuote + ": " + e.getCause() + "\n" + StackTraceUtil.getStackTrace(e));
                System.out.println("Error while updating " + currentQuote + " : " + e.getMessage());
            }    
        }
    }
}
