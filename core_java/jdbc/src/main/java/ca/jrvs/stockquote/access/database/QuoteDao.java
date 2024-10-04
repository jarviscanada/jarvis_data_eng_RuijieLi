package ca.jrvs.stockquote.access.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import org.apache.log4j.Logger;

import ca.jrvs.stockquote.util.StackTraceUtil;

// import org.slf4j.LoggerFactory;

public class QuoteDao implements CrudDao<Quote, String> {

    private Connection connection;

    private static Logger logger;
    private static final String FIND_BY_ID = "SELECT" +
        " symbol, open, high, low, price, volume, latest_trading_day, previous_close, change, change_percent, timestamp " +
        "FROM quote WHERE symbol = ?";

    private static final String INSERT_INTO = "INSERT INTO quote " + 
        "(symbol, open, high, low, price, volume, latest_trading_day, previous_close, change, change_percent, timestamp) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    
    private static final String UPDATE = "UPDATE quote SET " + 
        "open = ?, high = ?, low = ?, price = ?, volume = ?, latest_trading_day = ?, previous_close = ?, change = ?, change_percent = ?, timestamp = ? " + 
        "WHERE symbol = ?";
    
    private static final String DELETE = "DELETE FROM quote WHERE symbol = ?";

    private static final String DELETE_ALL = "DELETE FROM quote WHERE 1=1";
    private static final String SELECT_ALL = "SELECT " + 
        " symbol, open, high, low, price, volume, latest_trading_day, previous_close, change, change_percent, timestamp " +
        " FROM quote";

    public QuoteDao(Connection connection) {
        this.connection = connection;
        logger = Logger.getLogger(QuoteDao.class);
        logger.info("QuoteDao initialized");
        // BasicConfigurator.configure();
    }

    private void setUpdateStatement(PreparedStatement preparedStatement, Quote quote) throws SQLException {
        preparedStatement.setDouble(1, quote.getOpen());
        preparedStatement.setDouble(2, quote.getHigh());
        preparedStatement.setDouble(3, quote.getLow());
        preparedStatement.setDouble(4, quote.getPrice());
        preparedStatement.setDouble(5, quote.getVolume());
        preparedStatement.setDate(6, quote.getLatestTradingDay());
        preparedStatement.setDouble(7, quote.getPreviousClose());
        preparedStatement.setDouble(8, quote.getChange());
        preparedStatement.setString(9, quote.getChangePercent());
        preparedStatement.setTimestamp(10, quote.getTimestamp());
        preparedStatement.setString(11, quote.getTicker());
    }

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

    private static void logUpdateStatement(Quote quote) {
        logger.info("Excecuting query: " + String.format(UPDATE.replaceAll("\\?", "%s"),
            quote.getOpen() + "",
            quote.getHigh() + "",
            quote.getLow() + "",
            quote.getPrice() + "",
            quote.getVolume() + "",
            quote.getLatestTradingDay() + "",
            quote.getPreviousClose() + "",
            quote.getChange() + "",
            quote.getChangePercent() + "",
            quote.getTimestamp() + "",
            quote.getTicker() + ""
        ));
    }

    private static void logInsertStatement(Quote quote) {
        logger.info("Excecuting query: " + String.format(INSERT_INTO.replaceAll("\\?", "%s"),
            quote.getTicker() + "",
            quote.getOpen() + "",
            quote.getHigh() + "",
            quote.getLow() + "",
            quote.getPrice() + "",
            quote.getVolume() + "",
            quote.getLatestTradingDay() + "",
            quote.getPreviousClose() + "",
            quote.getChange() + "",
            quote.getChangePercent() + "",
            quote.getTimestamp() + ""
        ));
    }

    public Quote createNew(Quote quote) {
        logInsertStatement(quote);
        try(PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO)) {
            this.setInsertStatement(preparedStatement, quote);
            preparedStatement.execute();
            return this.findById(quote.getTicker()).get();
        } catch (SQLException e) {
            logger.error("Error while inserting new value " + quote + "\n" + StackTraceUtil.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    public Quote saveExisting(Quote quote) {
        logUpdateStatement(quote);
        try(PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
            this.setUpdateStatement(preparedStatement, quote);
            preparedStatement.execute();
            return this.findById(quote.getTicker()).get();
        } catch (SQLException e) {
            logger.error("Error while updating value " + quote.getTicker() + "\n" + StackTraceUtil.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }
    @Override
    public Quote save(Quote quote) throws IllegalArgumentException {
        if(quote == null) {
            logger.info("Quote is null, throwing IllegalArgumentException");
            throw new IllegalArgumentException("Quote cannot be null");
        }
        logger.info("Saving quote " + quote.getTicker());
        boolean exists = this.findById(quote.getTicker()).isPresent();
        return exists ? this.saveExisting(quote) : this.createNew(quote); 
    }

    private Quote getQuoteFromRS(ResultSet rs) throws SQLException {
        Quote quote = new Quote();
        quote.setTicker(            rs.getString(1)     );
        quote.setOpen(              rs.getDouble(2)     );
        quote.setHigh(              rs.getDouble(3)     );
        quote.setLow(               rs.getDouble(4)     );
        quote.setPrice(             rs.getDouble(5)     );
        quote.setVolume(            rs.getInt(6)        );
        quote.setLatestTradingDay(  rs.getDate(7)       );
        quote.setPreviousClose(     rs.getDouble(8)     );
        quote.setChange(            rs.getDouble(9)     );
        quote.setChangePercent(     rs.getString(10)    );
        quote.setTimestamp(         rs.getTimestamp(11) );
        return quote;
    }

    @Override
    public Optional<Quote> findById(String id) throws IllegalArgumentException {
        logger.info("Executing query: " + FIND_BY_ID.replace("?", "'"+ id + "'"));
        try(PreparedStatement preparedStatement = this.connection.prepareStatement(FIND_BY_ID)) {
            preparedStatement.setString(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            Quote quote = null;
            while(rs.next()) {
                quote = this.getQuoteFromRS(rs);
            }
            logger.info("Found " + (quote == null ? 0:1));
            return quote == null ? Optional.empty() : Optional.of(quote);
        } catch(SQLException e) {
            logger.error("Error while finding quote with ID " + id + "\n" + StackTraceUtil.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Quote> findAll() {
        logger.info("Executing query: " + SELECT_ALL);
        ArrayList<Quote> quotes = new ArrayList<>();
        try(PreparedStatement preparedStatement = this.connection.prepareStatement(SELECT_ALL)) {
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                quotes.add(this.getQuoteFromRS(rs));
            }
            logger.info("Found " + quotes.size() + " quotes in DB");
            return quotes;
        } catch(SQLException e) {
            logger.error("Error while selecting all\n" + StackTraceUtil.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(String id) throws IllegalArgumentException {
        logger.info("Executing query " + DELETE.replace("?", "'" + id + "'"));
        try(PreparedStatement preparedStatement = this.connection.prepareStatement(DELETE)) {
            preparedStatement.setString(1, id);
            preparedStatement.execute();
        } catch(SQLException e) {
            logger.error("Error while deleting quote with id " + id + "\n" + StackTraceUtil.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll() {
        logger.info("Executing query: " + DELETE_ALL);
        try(PreparedStatement preparedStatement = this.connection.prepareStatement(DELETE_ALL)) {
            preparedStatement.execute();
        } catch(SQLException e) {
            logger.error("Error while deleting all quotes\n" + StackTraceUtil.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

}