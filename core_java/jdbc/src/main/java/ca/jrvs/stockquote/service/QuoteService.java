package ca.jrvs.stockquote.service;

import java.util.List;
import java.util.Optional;

import ca.jrvs.stockquote.access.database.Quote;
import ca.jrvs.stockquote.access.database.QuoteDao;
import ca.jrvs.stockquote.access.httpexternalapi.QuoteHttpHelper;

public class QuoteService {
    private QuoteDao dao;
    private QuoteHttpHelper httpHelper;

    public QuoteService(QuoteDao dao, QuoteHttpHelper helper) {
        this.dao = dao;
        this.httpHelper = helper;
    }

    /**
     * Fetches latest quote data from endpoint
     * @param ticker
     * @return Latest quote information or empty optional if ticker symbol not found
     */
    public Optional<Quote> fetchQuoteDataFromAPI(String ticker) {
        Quote quote = this.httpHelper.fetchQuoteInfo(ticker);
        return quote == null ? Optional.empty() : Optional.of(this.dao.save(quote));
    }

    public Optional<Quote> fetchFromDB(String ticker) {
        return this.dao.findById(ticker);
    }

    public Optional<Quote> fetch(String ticker) {
        Optional<Quote> quote = this.fetchFromDB(ticker);
        return quote.isPresent() ? quote : this.fetchQuoteDataFromAPI(ticker);
    }

    public List<Quote> fetchAll() {
        return (List<Quote>)dao.findAll();
    }

    public void updateAll() {
        List<Quote> quotes = this.fetchAll();
        for(Quote quote: quotes) {
            System.out.println("Updating: " + quote.getTicker() + " (last updated: " + quote.getTimestamp() + " )");
            Optional<Quote> updated = this.fetchQuoteDataFromAPI(quote.getTicker());
            if(updated.isPresent()) {
                System.out.println("Updated: " + updated.get().getTicker() + " (last updated: " + updated.get().getTimestamp() + " )");
            } else {
                System.out.println("Updating " + quote.getTicker() + " failed");
            }
        }
    }
}
