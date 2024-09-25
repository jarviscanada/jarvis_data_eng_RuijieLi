package ca.jrvs.stockquote.service;

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
}
