package ca.jrvs.stockquote.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import ca.jrvs.stockquote.access.database.Position;
import ca.jrvs.stockquote.access.database.Quote;
import ca.jrvs.stockquote.service.PositionService;
import ca.jrvs.stockquote.service.QuoteService;
import ca.jrvs.stockquote.service.exceptions.InvalidTickerException;
import ca.jrvs.stockquote.service.exceptions.SellMoreThanOwnedException;
import ca.jrvs.stockquote.service.exceptions.TickerNotOwnedException;
import ca.jrvs.stockquote.service.exceptions.TooManyVolumesException;
import ca.jrvs.stockquote.util.StackTraceUtil;

public class StockQuoteController {

    private QuoteService quoteService;
    private PositionService positionService;
    private static Logger logger;

    public StockQuoteController(QuoteService quoteService, PositionService positionService) {
        logger = Logger.getLogger(StockQuoteController.class);
        logger.info("controller initialized");
        this.quoteService = quoteService;
        this.positionService = positionService;
    }

    public void initClient() {
        System.out.println("Updating all existing stocks...");
        logger.info("Updating all existing stocks in the database");
        this.quoteService.updateAll();
    }

    public void displayStock(String chosenStock) {
        try {
            logger.info("Displaying stock: " + chosenStock);
            Optional<Quote> quote = quoteService.fetch(chosenStock);
            if(quote.isPresent()) {
                System.out.println(quote.get().toUserString());
            } else {
                System.out.println(chosenStock + " does not exist");
            }
        } catch(InvalidTickerException e) {
            logger.error("Invalid ticker exception: " + e.getMessage());
        }
    }

    public void displayPosition(String chosenStock) {
        try {
            logger.info("Displaying position: " + chosenStock);
            Optional<Position> positionWrapper = positionService.find(chosenStock);
            if(positionWrapper.isPresent()) {
                Position position = positionWrapper.get();
                System.out.println(position.toUserString());
            } else {
                System.out.println("You do not own " + chosenStock + " yet.");
            }
        } catch(InvalidTickerException e) {
            logger.error("Invalid ticker exception: " + e.getMessage());
        } 
    }

    public void displayAllPositions() {
        try {
            logger.info("Displaying all owned positions");
            List<Position> positions = positionService.findAll();
            List<String[]> values = new ArrayList<>();
            for(Position position:positions) {
                values.add(position.getAttributeValues());
            }
            System.out.println(StringUtil.toUserString(values, Position.getAttributeTitles()));
        } catch(InvalidTickerException e) {
            logger.error("Invalid ticker exception: " + e.getMessage());
        }
    }

    public void clear() {
        logger.info("Clearing console");
        try {
            String operatingSystem = System.getProperty("os.name").toLowerCase();            
            if (operatingSystem .contains("wind")) {
                logger.info("Detected Windows system: " + operatingSystem);
                // Runtime.getRuntime().exec("cls");
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else {
                // Runtime.getRuntime().exec("clear");
                // new ProcessBuilder("bash", "clear").inheritIO().start().waitFor();
                logger.info("Not Windows: printing \"\\033[H\\033[2J\" and flushing");
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException e) {
            logger.error("IOException while clearing console" + e.getCause() + " " + e.getMessage());
            System.out.println("IOException while clearing console: " + e.getMessage());
        } catch (InterruptedException e) {
            logger.error("Interrupted: " + e.getCause() + " " + e.getMessage());
            System.out.println("Process interrupted: " + e.getMessage());
        }
    }

    public void displayAllStocksInDB() {
        logger.info("Displaying all stocks in database");
        List<Quote> quotes = quoteService.fetchAll();
        List<String[]> values = new ArrayList<>();
        for(Quote quote:quotes) {
            values.add(quote.getAttributeValues());
        }
        System.out.println(
            StringUtil.toUserString(values, Quote.getAttributeTitles())
        );
    }

    public void buy(String chosenStock, int stockNumber) {
        try {
            logger.info("Buying " + stockNumber + " units of " + chosenStock);
            Optional<Quote> quote = this.quoteService.fetchFromDB(chosenStock);
    
            if(!quote.isPresent()){
                logger.info(chosenStock + " is not in database. Fetching from API");
                quote = this.quoteService.fetchQuoteDataFromAPI(chosenStock);
            }
            if(!quote.isPresent()) {
                logger.info(chosenStock + " is invalid.");
                System.out.println(chosenStock + " is not a valid stock. Please choose something else.");
                return;
            }
            Position position = this.positionService.buy(chosenStock, stockNumber, quote.get().getPrice());
            System.out.println("You have successfully bought " + stockNumber + " of " + chosenStock + ". Your position is now ");
            System.out.println(position.toUserString());
        } catch(TooManyVolumesException e) {
            logger.error("User tried to buy too many units of " + chosenStock);
            System.out.println(e.getMessage());
        } catch(InvalidTickerException e) {
            logger.error("Error: invalid ticker " + chosenStock + " : " + e.getMessage());
            System.out.println(e.getMessage());
        } catch(RuntimeException e) {
            logger.error(e.getCause() + " : " + e.getMessage());
            System.out.println("An unexpected error occured: " + e.getMessage());
        }
    }

    public void sell(String chosenStock, int numberToSell) {
        logger.info("Selling " + numberToSell + " units of " + chosenStock);
        try {
            Optional<Quote> quote = this.quoteService.fetchFromDB(chosenStock);
            if(!quote.isPresent()) {
                logger.info("User does not own " + chosenStock);
                System.out.println("You do not own " + chosenStock);
                return;
            }
            Position position = this.positionService.sell(chosenStock, numberToSell, quote.get().getPrice());
            System.out.println("You have sold " + numberToSell + " stock units of " + chosenStock + ". Your position is now");
            System.out.println(position.toUserString());
        } catch(TickerNotOwnedException e) {
            logger.error("User does not own " + chosenStock);
            System.out.println("You do not own " + chosenStock + ". Would you like to buy some?");
        } catch(InvalidTickerException e) {
            logger.error("User tried to sell more than owned (stock unit: " + chosenStock + ")");
            System.out.println("Ticker " + chosenStock + " is invalid");
        } catch(SellMoreThanOwnedException e) {
            logger.error("User tried to sell more than owned (stock unit: " + chosenStock + ")");
            System.out.println("You own less than " + numberToSell + " " + chosenStock + " units. Would you like to buy some?");
        } catch(RuntimeException e) {
            logger.error("An unexpected error occured: " + e.getCause() + " : " + e.getMessage() + "\n" + StackTraceUtil.getStackTrace(e));
            System.out.println("An unexpected error occured: " + e.getMessage());
        }
    }

}
