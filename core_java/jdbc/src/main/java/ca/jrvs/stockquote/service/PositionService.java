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
	
    public PositionService(PositionDao positionDao, QuoteDao quoteDao, QuoteService quoteService) {
        this.positionDao = positionDao;
        this.quoteDao = quoteDao;
        this.quoteService = quoteService;
    }

    private Quote getQuote(String ticker) {
        // get quote from database
        Optional<Quote> quoteToBuy = this.quoteDao.findById(ticker);
        // System.out.println(quoteToBuy.get());

        Quote quote = null;
        // if not in DB, go get it from internet
        if(!quoteToBuy.isPresent()) {
            Optional<Quote> fetchedQuote = this.quoteService.fetchQuoteDataFromAPI(ticker);
            if(!fetchedQuote.isPresent()) {
                throw new RuntimeException("Ticker " + ticker + " does not exist");
            } else {
                quote = fetchedQuote.get();
            }
        } else {
            quote = quoteToBuy.get();
        }
        return quote;
    }

    public Position buy(String ticker, int numberOfShares, double price) {
        Quote quoteToBuy = this.getQuote(ticker);

        if(numberOfShares > quoteToBuy.getVolume()) {
            throw new RuntimeException("Cannot buy more than available volume");
        }
        // save quote in DB
        quoteToBuy.setVolume(quoteToBuy.getVolume() - numberOfShares);
        quoteDao.save(quoteToBuy);

        // Save the position
        Optional<Position> positionInDB = positionDao.findById(ticker);
        Position position = null;
        if(!positionInDB.isPresent()) {
            position = new Position();
            position.setNumOfShares(numberOfShares);
            position.setValuePaid(price * numberOfShares);
            position.setTicker(ticker);
        } else {
            position = positionInDB.get();
            position.setNumOfShares(position.getNumOfShares() + numberOfShares);
            position.setValuePaid(position.getValuePaid() + (price * numberOfShares));
        }
        return positionDao.save(position);
	}

    public Position sell(String ticker, int numberOfShares, double price) {

        Optional<Position> positionFromDB = positionDao.findById(ticker);
        if(!positionFromDB.isPresent()) {
            throw new RuntimeException("Cannot sell " + ticker + ": cannot sell a position that is not owned");
        }
        if(positionFromDB.get().getNumOfShares() < numberOfShares) {
            throw new RuntimeException("Cannot sell more than owned");
        }
        Position position = positionFromDB.get();
        position.setNumOfShares(position.getNumOfShares() - numberOfShares);
        position.setValuePaid(position.getValuePaid() - price * numberOfShares);

        Quote quote = getQuote(ticker);
        quote.setVolume(quote.getVolume() + numberOfShares);
        quoteDao.save(quote);

        return positionDao.save(position);
    }
}
