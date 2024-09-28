package ca.jrvs.stockquote.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

    public void updateAll() {
        List<Position> positions = (ArrayList<Position>)this.positionDao.findAll();
        for(Position position:positions) {
            System.out.println("Updating " + position.getTicker());
            Optional<Quote> quote = this.quoteService.fetchQuoteDataFromAPI(position.getTicker());
            if(quote.isPresent()) {
                System.out.println("Updated stock " + quote.get().getTicker());
            } else {
                System.out.println("update " + position.getTicker() + " failed");
            }
        }
    }

    public static String getTickerNotPresentMsg(String ticker) {
        return "Ticker " + ticker + " does not exist";
    }
    public static String getTooManyVolumeMsg() {
        return "Cannot buy more than available volume";
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
                throw new RuntimeException(getTickerNotPresentMsg(ticker));
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
            throw new RuntimeException(getTooManyVolumeMsg());
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
        Position positionToReturn = positionDao.save(position);
        positionToReturn.setCurrentValue((double)Math.round(price * positionToReturn.getNumOfShares() * 100.00) / 100);
        return positionToReturn;
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

        Position positionToReturn = positionDao.save(position);
        positionToReturn.setCurrentValue((double)Math.round(price * positionToReturn.getNumOfShares() * 100.00) / 100);
        return positionToReturn;
    }

    public Optional<Position> find(String id) {
        Optional<Position> positionWrapper = this.positionDao.findById(id);
        if(positionWrapper.isPresent()) {
            Quote quote = this.getQuote(id);
            positionWrapper.get().setCurrentValue((double)Math.round(quote.getPrice() * positionWrapper.get().getNumOfShares() * 100.00) / 100);
        }
        return positionWrapper;
    }

    public List<Position> findAll() {
        List<Position> positions = (ArrayList<Position>)positionDao.findAll();
        for(Position position:positions) {
            Quote quote = getQuote(position.getTicker());
            position.setCurrentValue((double)Math.round(position.getNumOfShares() * quote.getPrice() * 100) / 100);
        }
        return positions;
    }
}
