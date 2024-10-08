package ca.jrvs.stockquote;

import java.io.BufferedReader;
import java.io.Console;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import ca.jrvs.stockquote.access.database.PositionDao;
import ca.jrvs.stockquote.access.database.QuoteDao;
import ca.jrvs.stockquote.access.httpexternalapi.QuoteHttpHelper;
import ca.jrvs.stockquote.controller.StockQuoteController;
import ca.jrvs.stockquote.controller.UserActions;
import ca.jrvs.stockquote.service.PositionService;
import ca.jrvs.stockquote.service.QuoteService;
import ca.jrvs.stockquote.util.StackTraceUtil;
import okhttp3.OkHttpClient;

public class Main {
    static Logger logger = Logger.getLogger(Main.class);
    
    public static void main(String[] args) {
        
        PropertyConfigurator.configure("src/main/resources/log4j.properties");
        logger.info("======= Program started =======");
        Map<String, String> properties = new HashMap<>();
        final String databasePrameters = "src/main/resources/properties.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(databasePrameters))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(":");
                properties.put(tokens[0], tokens[1]);
            }
            logger.info("Finished reading " + databasePrameters);
        } catch (FileNotFoundException e) {
            logger.error("Error: file not found exception\n" + StackTraceUtil.getStackTrace(e));
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("IO Exception \n" + StackTraceUtil.getStackTrace(e));
            throw new RuntimeException(e);            
        } catch(Exception e) {
            logger.error("Unexpected exception occured: \n" + StackTraceUtil.getStackTrace(e));
            throw new RuntimeException(e);            
        }

        String apiKey = System.getenv("API_KEY");
        if(apiKey == null) {
            logger.error("API KEY NOT SET");
            throw new RuntimeException("Error: API_KEY environment variable not set");
        }

        OkHttpClient client = new OkHttpClient();
        String url = "jdbc:postgresql://"+properties.get("server")+":"+properties.get("port")+"/"+properties.get("database");
        logger.info("Database initialized to" + url);
        try (Connection c = DriverManager.getConnection(url, properties.get("username"), properties.get("password"))) {
            System.out.println("######################################################################################");
            System.out.println("#                           Welcome to the stock quote app                           #");
            System.out.println("######################################################################################");

            QuoteDao qRepo = new QuoteDao(c);
            PositionDao pRepo = new PositionDao(c);

            // byte[] keyBytes = Files.readAllBytes(Paths.get(properties.get("api-key-path")));

            QuoteHttpHelper rcon = new QuoteHttpHelper(apiKey, client);
            QuoteService sQuote = new QuoteService(qRepo, rcon);
            PositionService sPos = new PositionService(pRepo, qRepo, sQuote);
            StockQuoteController controller = new StockQuoteController(sQuote, sPos);
            controller.initClient();
            Console console = System.console();
            
            while(true) {
                System.out.println();
                System.out.println("Choose which action: ");
                System.out.println( UserActions.SELL_STOCK                  + " : Sell stock           " 
                                    + UserActions.BUY_STOCK                 + " : Buy stock            "
                                    + UserActions.DISPLAY_POSITION          + ": Display specific position   ");
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
                        logger.info("User chose option: BUY" + stockNumberString + " units of " + chosenStock);
                        stockNumber = Integer.parseInt(stockNumberString);
                        controller.buy(chosenStock, stockNumber);
                        break;
                    case(UserActions.SELL_STOCK):
                        System.out.print("Choose a stock: ");
                        chosenStock = console.readLine();
                        System.out.print("How many to sell: ");
                        stockNumberString = console.readLine();
                        logger.info("User chose option: SELL " + stockNumberString + " units of " + chosenStock);
                        stockNumber = Integer.parseInt(stockNumberString);
                        controller.sell(chosenStock, stockNumber);;
                        break;
                    case(UserActions.DISPLAY_STOCK):
                        System.out.print("Choose a stock: ");
                        chosenStock = console.readLine();
                        logger.info("User chose option: DISPLAY STOCK " + chosenStock);
                        controller.displayStock(chosenStock);
                        break;
                    case(UserActions.DISPLAY_POSITION):
                        System.out.print("Choose a stock: ");
                        chosenStock = console.readLine();
                        logger.info("User chose option: DISPLAY POSITION " + chosenStock);
                        controller.displayPosition(chosenStock);
                        break;
                    case(UserActions.DISPLAY_ALL_POSITION):
                        logger.info("User chose option: DISPLAY ALL POSITIONS");
                        controller.displayAllPositions();
                        break;
                    case(UserActions.DISPLAY_ALL_STOCKS_IN_DB):
                        logger.info("User chose option: DISPLAY ALL STOCKS IN THE DATABASE");
                        controller.displayAllStocksInDB();
                        break;
                    case(UserActions.QUIT):
                        logger.info("User chose: QUIT program");
                        System.exit(0);
                        break;
                    case(UserActions.CLEAR):
                        logger.info("User chose: CLEAR console");
                        controller.clear();
                        break;
                    default:
                        logger.info("User chose: invalid option " + option);
                        System.out.println("Please choose a valid option");
                        break;
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Exception: " + e.getCause() + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error: \n" + StackTraceUtil.getStackTrace(e));
        }
    }
}
