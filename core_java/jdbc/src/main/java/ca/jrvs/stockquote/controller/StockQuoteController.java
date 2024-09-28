package ca.jrvs.stockquote.controller;

import java.util.List;
import java.util.Optional;

import ca.jrvs.stockquote.access.database.Position;
import ca.jrvs.stockquote.access.database.Quote;
import ca.jrvs.stockquote.service.PositionService;
import ca.jrvs.stockquote.service.QuoteService;

public class StockQuoteController {

    private QuoteService quoteService;
    private PositionService positionService;

    public StockQuoteController(QuoteService quoteService, PositionService positionService) {
        this.quoteService = quoteService;
        this.positionService = positionService;
    }

    public void initClient() {
        System.out.println("Updating all existing stocks...");
        // List<Position> positions = this.positionService.
        this.positionService.updateAll();
    }

    public void displayStock(String chosenStock) {
        Optional<Quote> quote = quoteService.fetch(chosenStock);
        if(quote.isPresent()) {
            System.out.println(quote.get().toUserString());
        } else {
            System.out.println(chosenStock + " does not exist");
        }
    }

    public void displayPosition() {

    }

    public void displayAllPositions() {

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
            System.out.println(position);
        } catch(RuntimeException e) {
            if(e.getMessage() == null) {
                System.out.println("An unknown error has occured: " + e.getCause());
            }
            if(e.getMessage().equals(PositionService.getTickerNotPresentMsg(chosenStock))) {
                System.out.println("Error: " + PositionService.getTickerNotPresentMsg(chosenStock));
            } else if(e.getMessage().equals(PositionService.getTooManyVolumeMsg())) {
                System.out.println("Error: " + PositionService.getTooManyVolumeMsg());
            } else {
                System.out.println("An unexpected error has occured: " + e.getMessage());
            }
        }
    }

    public void sell(String chosenStock, int numberToSell) {
        Optional<Quote> quote = this.quoteService.fetchFromDB(chosenStock);
        // if(!quote.isPresent()) {
        //     System.out.println("You do not have any " + chosenStock + " units. Would you like to buy some?");
        //     return;
        // }
        try {
            Position position = this.positionService.sell(chosenStock, numberToSell, quote.get().getPrice());
            System.out.println("You have sold " + numberToSell + " stock units of " + chosenStock + ". Your position is now");
            System.out.println(position);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
