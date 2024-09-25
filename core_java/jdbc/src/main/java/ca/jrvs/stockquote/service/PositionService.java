package ca.jrvs.stockquote.service;

import java.util.Optional;

import ca.jrvs.stockquote.access.database.Position;
import ca.jrvs.stockquote.access.database.PositionDao;
import ca.jrvs.stockquote.access.database.Quote;
import ca.jrvs.stockquote.access.database.QuoteDao;

public class PositionService {
    private PositionDao positionDao;
    private QuoteDao quoteDao;
    private QuoteService quoteService;
	
    PositionService(PositionDao positionDao, QuoteDao quoteDao, QuoteService quoteService) {
        this.positionDao = positionDao;
        this.quoteDao = quoteDao;
        this.quoteService = quoteService;
    }

    public Position buy(String ticker, int numberOfShares, double price) {
		Optional<Quote> quoteToBuy = this.quoteDao.findById(ticker);
        Quote quote = null;
        if(!quoteToBuy.isPresent()) {
            Optional<Quote> fetchedQuote = this.quoteService.fetchQuoteDataFromAPI(ticker);
            if(!fetchedQuote.isPresent()) {
                throw new RuntimeException("Ticker " + ticker + " does not exist");
            } else {
                quote = fetchedQuote.get();
            }
        }
        if(numberOfShares > quote.getVolume()) {
            throw new RuntimeException("Cannot buy more than available volume");
        }
        Optional<Position> positionInDB = positionDao.findById(ticker);
        Position position = null;
        quote.setVolume(quote.getVolume() - numberOfShares);
        quoteDao.save(quote);
        if(!positionInDB.isPresent()) {
            position = new Position();
            position.setNumOfShares(numberOfShares);
            position.setValuePaid(price);
            position.setTicker(ticker);
        } else {
            position = positionInDB.get();
        }
        return null;
	}
    public Position sell(String ticker, int numberOfShares, double price) {
        return null;
    }
}
