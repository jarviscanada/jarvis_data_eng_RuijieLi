package ca.jrvs.stockquote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuoteDao implements CrudDao<Quote, String> {

	private Connection connection;

    private Logger logger;
    private static final String FIND_BY_ID = "SELECT * FROM quote WHERE symbole = ?";

    private static final String INSERT_INTO = "INSERT INTO quote " + 
        "(symbol, open, high, low, price, volume, latest_trading_day, previous_close, change, change_percent, timestamp) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    
    private static final String UPDATE = "UPDATE quote SET " + 
        "open = ?, high = ?, low = ?, price = ?, volume = ?, latest_trading_day = ?, previous_close = ?, change = ?, change_percent = ?, timestamp = ? " + 
        "WHERE symbol = ?";
    
    private static final String DELETE = "DELETE FROM quote WHERE symbol = ?";

    private static final String DELETE_ALL = "DELETE FROM quote WHERE 1=1";

    QuoteDao(Connection connection) {
        this.connection = connection;
        this.logger = LoggerFactory.getLogger(QuoteDao.class);
        BasicConfigurator.configure();
    }

    // TODO
    private void setUpdateStatement(PreparedStatement preparedStatement, Quote quote) {}

    // TODO
    private void setInsertStatement(PreparedStatement preparedStatement, Quote quote) throws SQLException {
        preparedStatement.setString(1, quote.getTicker());
        preparedStatement.setDouble(2, quote.getOpen());
        preparedStatement.setDouble(3, quote.getHigh());
        preparedStatement.setDouble(4, quote.getLow());
        preparedStatement.setDouble(5, quote.getPrice());
        preparedStatement.setDouble(6, quote.getVolume());
        preparedStatement.setDate(7, quote.getLatestTradingDay());
        preparedStatement.setDouble(8, quote.getPreviousClose());
        preparedStatement.setDouble(9, quote.getChange());
        preparedStatement.setString(10, quote.getChangePercent());
        preparedStatement.setTimestamp(11, quote.getTimestamp());
    }

    // TODO
    public Quote createNewQuote(Quote entity) {
        try(PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO)) {
            this.setInsertStatement(preparedStatement, entity);
            preparedStatement.execute();
        } catch (SQLException e) {
            logger.error("Error while inserting new value " + entity, e);
            throw new RuntimeException(e);
        }
        return null;
    }

    // TODO
    public Quote saveExisting(Quote entity) {
        try(PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
        } catch (SQLException e) {
            logger.error("Error while inserting new value " + entity, e);
        }
        return null;
    }
    @Override
    public Quote save(Quote entity) throws IllegalArgumentException {
        // throw new UnsupportedOperationException("Unimplemented method 'save'");
        if(entity == null) {
            throw new IllegalArgumentException();
        }
        boolean exists = this.findById(entity.getTicker()).isPresent();
        return exists ? this.saveExisting(entity) : this.createNewQuote(entity); 
    }

    @Override
    public Optional<Quote> findById(String id) throws IllegalArgumentException {
        // throw new UnsupportedOperationException("Unimplemented method 'findById'");
        return Optional.empty();
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