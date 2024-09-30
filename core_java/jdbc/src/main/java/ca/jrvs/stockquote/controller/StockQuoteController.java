package ca.jrvs.stockquote.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ca.jrvs.stockquote.access.database.Position;
import ca.jrvs.stockquote.access.database.Quote;
import ca.jrvs.stockquote.service.PositionService;
import ca.jrvs.stockquote.service.QuoteService;
import ca.jrvs.stockquote.service.exceptions.InvalidTickerException;
import ca.jrvs.stockquote.service.exceptions.SellMoreThanOwnedException;
import ca.jrvs.stockquote.service.exceptions.TickerNotOwnedException;
import ca.jrvs.stockquote.service.exceptions.TooManyVolumesException;

public class StockQuoteController {

    private QuoteService quoteService;
    private PositionService positionService;

    public StockQuoteController(QuoteService quoteService, PositionService positionService) {
        this.quoteService = quoteService;
        this.positionService = positionService;
    }

    public void initClient() {
        System.out.println("Updating all existing stocks...");
        this.quoteService.updateAll();
    }

    public void displayStock(String chosenStock) {
        Optional<Quote> quote = quoteService.fetch(chosenStock);
        if(quote.isPresent()) {
            System.out.println(quote.get().toUserString());
        } else {
            System.out.println(chosenStock + " does not exist");
        }
    }

    public void displayPosition(String chosenStock) {
        Optional<Position> positionWrapper = positionService.find(chosenStock);
        Optional<Quote> quoteWrapper = quoteService.fetch(chosenStock);
        if(positionWrapper.isPresent()) {
            Position position = positionWrapper.get();
            Quote quote = quoteWrapper.get();
            position.setCurrentValue((double)Math.round(quote.getPrice() * position.getNumOfShares()* 100) / 100 );
            System.out.println(position.toUserString());
        } else {
            System.out.println("You do not own " + chosenStock + " yet.");
        }
    }

    public void displayAllPositions() {
        List<Position> positions = positionService.findAll();
        List<String[]> values = new ArrayList<>();
        for(Position position:positions) {
            values.add(position.getAttributeValues());
        }
        System.out.println(StringUtil.toUserString(values, Position.getAttributeTitles()));
    }

    public void clear() {

        try {
            String operatingSystem = System.getProperty("os.name").toLowerCase();            
            if (operatingSystem .contains("wind")) {
                // Runtime.getRuntime().exec("cls");
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else {
                // Runtime.getRuntime().exec("clear");
                // new ProcessBuilder("bash", "clear").inheritIO().start().waitFor();
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException e) {
            System.out.println("IOException while clearing console: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Process interrupted: " + e.getMessage());
        }
    }

    public void displayAllStocksInDB() {
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
        Optional<Quote> quote = this.quoteService.fetchFromDB(chosenStock);

        if(!quote.isPresent()){
            quote = this.quoteService.fetchQuoteDataFromAPI(chosenStock);
        }

        if(!quote.isPresent()) {
            System.out.println(chosenStock + " is not a valid stock. Please choose something else.");
            return;
        }
        try {
            Position position = this.positionService.buy(chosenStock, stockNumber, quote.get().getPrice());
            System.out.println("You have successfully bought " + stockNumber + " of " + chosenStock + ". Your position is now ");
            System.out.println(position.toUserString());
        } catch(TooManyVolumesException e) {
            System.out.println(e.getMessage());
        } catch(InvalidTickerException e) {
            System.out.println(e.getMessage());
        } catch(RuntimeException e) {
            System.out.println("An unexpected error occured: " + e.getMessage());
        }
    }

    public void sell(String chosenStock, int numberToSell) {
        Optional<Quote> quote = this.quoteService.fetchFromDB(chosenStock);
        // if(!quote.isPresent()) {
        //     System.out.println("You do not have any " + chosenStock + " units. Would you like to buy some?");
        //     return;
        // }
        try {
            if(!quote.isPresent()) {
                System.out.println("You do not own " + chosenStock);
                return;
            }
            Position position = this.positionService.sell(chosenStock, numberToSell, quote.get().getPrice());
            System.out.println("You have sold " + numberToSell + " stock units of " + chosenStock + ". Your position is now");
            System.out.println(position.toUserString());
        } catch(TickerNotOwnedException e) {
            System.out.println("You do not own " + chosenStock + ". Would you like to buy some?");
        } catch(SellMoreThanOwnedException e) {
            System.out.println("You own less than " + numberToSell + " " + chosenStock + " units. Would you like to buy some?");
        } catch(RuntimeException e) {
            System.out.println("An unexpected error occured: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
