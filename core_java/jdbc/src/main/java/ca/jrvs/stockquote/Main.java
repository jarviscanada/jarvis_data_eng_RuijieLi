package ca.jrvs.stockquote;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        QuoteHttpHelper helper = new QuoteHttpHelper();
        // System.out.println(
            
        // );
        Quote quote = helper.fetchQuoteInfo("TSLA");
        Timestamp t = new Timestamp(Instant.now().toEpochMilli());
        quote.setTimestamp(t);
        // System.out.println(quote);
        String pgUsername = System.getenv("PGUSERNAME");
        String pgPassword = System.getenv("PGPASSWORD");
        Connection connection = DatabaseUtil.getConnection("localhost", "stock_quote", pgUsername, pgPassword);
        QuoteDao quoteDao = new QuoteDao(connection);
        quoteDao.createNewQuote(quote);
    }
}
