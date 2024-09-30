package ca.jrvs.stockquote;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import ca.jrvs.stockquote.access.database.PositionDao;
import ca.jrvs.stockquote.access.database.QuoteDao;
import ca.jrvs.stockquote.access.httpexternalapi.QuoteHttpHelper;
import ca.jrvs.stockquote.controller.StockQuoteController;
import ca.jrvs.stockquote.controller.UserActions;
import ca.jrvs.stockquote.service.PositionService;
import ca.jrvs.stockquote.service.QuoteService;
import okhttp3.OkHttpClient;

public class Main {
    public static void main(String[] args) {        
        Map<String, String> properties = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/main/resources/properties.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(":");
                properties.put(tokens[0], tokens[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            Class.forName(properties.get("db-class"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();
        String url = "jdbc:postgresql://"+properties.get("server")+":"+properties.get("port")+"/"+properties.get("database");

        try (Connection c = DriverManager.getConnection(url, properties.get("username"), properties.get("password"))) {
            System.out.println("######################################################################################");
            System.out.println("#                           Welcome to the stock quote app                           #");
            System.out.println("######################################################################################");

            QuoteDao qRepo = new QuoteDao(c);
            PositionDao pRepo = new PositionDao(c);

            byte[] keyBytes = Files.readAllBytes(Paths.get(properties.get("api-key-path")));
            String apiKey = new String(keyBytes, StandardCharsets.UTF_8).strip();

            QuoteHttpHelper rcon = new QuoteHttpHelper(apiKey, client);
            QuoteService sQuote = new QuoteService(qRepo, rcon);
            PositionService sPos = new PositionService(pRepo, qRepo, sQuote);
            StockQuoteController controller = new StockQuoteController(sQuote, sPos);
            // controller.initClient();
            Console console = System.console();
            
            while(true) {
                System.out.println();
                System.out.println("Choose which action: ");
                System.out.println( UserActions.SELL_STOCK                  + " : Sell stock           " 
                                    + UserActions.BUY_STOCK                 + " : Buy stock            "
                                    + UserActions.DISPLAY_STOCK             + ": Display specific position   ");
                System.out.println( UserActions.QUIT                        + " : Quit program         "
                                    + UserActions.CLEAR                     + " : Clear Console        " 
                                    + UserActions.DISPLAY_STOCK             + ": Display specific stock      ");
                System.out.println( UserActions.DISPLAY_ALL_STOCKS_IN_DB    + ": Display all stocks in database                "
                                    + UserActions.DISPLAY_ALL_POSITION      + ": Display all owned positions ");
                System.out.println();
    
                String option = console.readLine();
                String chosenStock;
                String stockNumberString;
                int stockNumber;

                switch(option) {
                    case(UserActions.BUY_STOCK):
                        System.out.print("Choose a stock: ");
                        chosenStock = console.readLine();
                        System.out.print("How many to buy: ");
                        stockNumberString = console.readLine();
                        stockNumber = Integer.parseInt(stockNumberString);
                        controller.buy(chosenStock, stockNumber);
                        break;
                    case(UserActions.SELL_STOCK):
                        System.out.print("Choose a stock: ");
                        chosenStock = console.readLine();
                        System.out.print("How many to sell: ");
                        stockNumberString = console.readLine();
                        stockNumber = Integer.parseInt(stockNumberString);
                        controller.sell(chosenStock, stockNumber);;
                        break;
                    case(UserActions.DISPLAY_STOCK):
                        System.out.print("Choose a stock: ");
                        chosenStock = console.readLine();
                        controller.displayStock(chosenStock);
                        break;
                    case(UserActions.DISPLAY_POSITION):
                        System.out.print("Choose a stock: ");
                        chosenStock = console.readLine();
                        controller.displayPosition(chosenStock);
                        break;
                    case(UserActions.DISPLAY_ALL_POSITION):
                        controller.displayAllPositions();
                        break;
                    case(UserActions.DISPLAY_ALL_STOCKS_IN_DB):
                        controller.displayAllStocksInDB();
                        break;
                    case(UserActions.QUIT):
                        System.exit(0);
                        break;
                    case(UserActions.CLEAR):
                        controller.clear();
                        break;
                    default:
                        System.out.println("Please choose a valid option");
                        break;
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
