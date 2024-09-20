package ca.jrvs.stockquote;

import java.sql.Connection;
import java.util.Optional;

public class QuoteDao implements CrudDao<Quote, String> {

	private Connection connection;

    QuoteDao(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Quote save(Quote entity) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public Optional<Quote> findById(String id) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public Iterable<Quote> findAll() {
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public void deleteById(String id) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Unimplemented method 'deleteById'");
    }

    @Override
    public void deleteAll() {
        throw new UnsupportedOperationException("Unimplemented method 'deleteAll'");
    }

}