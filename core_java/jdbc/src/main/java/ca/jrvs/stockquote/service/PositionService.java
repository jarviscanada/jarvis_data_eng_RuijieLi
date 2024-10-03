package ca.jrvs.stockquote.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import ca.jrvs.stockquote.access.database.Position;
import ca.jrvs.stockquote.access.database.PositionDao;
import ca.jrvs.stockquote.access.database.Quote;
import ca.jrvs.stockquote.access.database.QuoteDao;
import ca.jrvs.stockquote.service.exceptions.SellMoreThanOwnedException;
import ca.jrvs.stockquote.service.exceptions.TickerNotOwnedException;
import ca.jrvs.stockquote.service.exceptions.TooManyVolumesException;

public class PositionService {
    private PositionDao positionDao;
    private QuoteDao quoteDao;
    private QuoteService quoteService;
    private static Logger logger;
    
    public PositionService(PositionDao positionDao, QuoteDao quoteDao, QuoteService quoteService) {
        this.positionDao = positionDao;
        this.quoteDao = quoteDao;
        this.quoteService = quoteService;
        logger = Logger.getLogger(PositionService.class);
        logger.info("Position Service initialized");
    }

    public Position buy(String ticker, int numberOfShares, double price) {
        logger.info("Buying " + numberOfShares + " units of " + ticker + " at price " + price);
        Quote quoteToBuy = this.quoteService.fetch(ticker).get();

        if(numberOfShares > quoteToBuy.getVolume()) {
            logger.info("User tried to buy too many units of " + quoteToBuy + ". Throwing TooManyVolumesException");
            throw new TooManyVolumesException();
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
        logger.info("Selling " + numberOfShares + " units of " + ticker + " at " + price);
        Optional<Position> positionFromDB = positionDao.findById(ticker);
        if(!positionFromDB.isPresent()) {
            logger.info(ticker + " is not a valid stock. Throwing TickerNotOwnedException");
            throw new TickerNotOwnedException("Cannot sell " + ticker + ": cannot sell a position that is not owned");
        }
        if(positionFromDB.get().getNumOfShares() < numberOfShares) {
            logger.info("Selling too many units of " 
                + ticker 
                + " : owned = "
                + positionFromDB.get().getNumOfShares()
                + "; trying to sell "
                + numberOfShares 
                + ". Throwing SellMoreThanOwnedException");
            throw new SellMoreThanOwnedException();
        }
        Position position = positionFromDB.get();
        position.setNumOfShares(position.getNumOfShares() - numberOfShares);
        position.setValuePaid(position.getValuePaid() - price * numberOfShares);

        Quote quote = this.quoteService.fetch(ticker).get();
        quote.setVolume(quote.getVolume() + numberOfShares);
        quoteDao.save(quote);

        Position positionToReturn = positionDao.save(position);
        positionToReturn.setCurrentValue((double)Math.round(price * positionToReturn.getNumOfShares() * 100.00) / 100);
        return positionToReturn;
    }

    public Optional<Position> find(String id) {
        logger.info("Searching for position with id " + id);
        Optional<Position> positionWrapper = this.positionDao.findById(id);
        if(positionWrapper.isPresent()) {
            Quote quote = this.quoteService.fetch(id).get();
            positionWrapper.get().setCurrentValue((double)Math.round(quote.getPrice() * positionWrapper.get().getNumOfShares() * 100.00) / 100);
        }
        return positionWrapper;
    }

    public List<Position> findAll() {
        logger.info("Fetching all owned positions");
        List<Position> positions = (ArrayList<Position>)positionDao.findAll();
        for(Position position:positions) {
            Quote quote = this.quoteService.fetch(position.getTicker()).get();
            position.setCurrentValue((double)Math.round(position.getNumOfShares() * quote.getPrice() * 100) / 100);
        }
        return positions;
    }
}
