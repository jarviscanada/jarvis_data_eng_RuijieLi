package ca.jrvs.stockquote;

import java.sql.Connection;
// import java.sql.Timestamp;
// import java.time.Instant;
import java.util.ArrayList;
// import java.util.List;
// import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        QuoteHttpHelper helper = new QuoteHttpHelper();
        String pgUsername = System.getenv("PGUSERNAME");
        String pgPassword = System.getenv("PGPASSWORD");
        Connection connection = DatabaseUtil.getConnection("localhost", "stock_quote", pgUsername, pgPassword);
        QuoteDao quoteDao = new QuoteDao(connection);

        ArrayList<Quote> quotes = (ArrayList<Quote>)quoteDao.findAll();
        for(Quote quote:quotes) {
            System.out.println(quote);
        }
    }
}
