package ca.jrvs.stockquote;

import java.sql.Connection;
import java.util.Optional;

import ca.jrvs.stockquote.access.database.DatabaseUtil;
import ca.jrvs.stockquote.access.database.Quote;
import ca.jrvs.stockquote.access.database.QuoteDao;
import ca.jrvs.stockquote.access.httpexternalapi.QuoteHttpHelper;
import ca.jrvs.stockquote.service.QuoteService;

public class Main {
    public static void main(String[] args) {
        QuoteHttpHelper helper = new QuoteHttpHelper();
        String pgUsername = System.getenv("PGUSERNAME");
        String pgPassword = System.getenv("PGPASSWORD");
        Connection connection = DatabaseUtil.getConnection("localhost", "stock_quote", pgUsername, pgPassword);
        // PositionDao positionDao = new PositionDao(connection);
        QuoteDao quoteDao = new QuoteDao(connection);
        QuoteService quoteService = new QuoteService(quoteDao, helper);
        Optional<Quote> quote = quoteService.fetchQuoteDataFromAPI("TSLA");
        System.out.println(quote.isPresent() ? quote.get() : null);
        // positionDao.deleteAll();
        // Position tsla = new Position();
        // tsla.setNumOfShares(10);
        // tsla.setTicker("TSLA");
        // tsla.setValuePaid(10000.12);
        // positionDao.save(tsla);

        // Position ford = new Position();
        // ford.setTicker("F");
        // ford.setNumOfShares(20);
        // ford.setValuePaid(2000);
        // positionDao.save(ford);

        // Position microsoft = new Position();
        // microsoft.setTicker("MSFT");
        // microsoft.setNumOfShares(10);
        // microsoft.setValuePaid(3425.23);
        // positionDao.save(microsoft);

        // Position doge = new Position();
        // doge.setTicker("DOGE");
        // doge.setNumOfShares(1000);
        // doge.setValuePaid(200);
        // positionDao.save(doge);

        // ArrayList<Position> positions = (ArrayList<Position>)positionDao.findAll();
        // for(Position position:positions) {
        //     System.out.println(position);
        // }
        // positionDao.deleteById("TSLA");
        // System.out.println("============================ DELETED TESLA ============================");
        // positions = (ArrayList<Position>)positionDao.findAll();
        // for(Position position:positions) {
        //     System.out.println(position);
        // }
    }
}
