package ca.jrvs.stockquote;

import java.sql.Connection;
import java.util.Optional;

public class PositionDao implements CrudDao<Position, String> {

	private Connection connection;

    @Override
    public Position save(Position entity) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Unimplemented method 'save'");
    }

    @Override
    public Optional<Position> findById(String id) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public Iterable<Position> findAll() {
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