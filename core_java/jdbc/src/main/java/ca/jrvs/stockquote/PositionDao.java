package ca.jrvs.stockquote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionDao implements CrudDao<Position, String> {

	private Connection connection;
    private static final String INSERT_INTO = "INSERT INTO position " + 
        " (symbol, number_of_shares, value_paid) " +
        "VALUES (?, ?, ?);";
    private static final String UPDATE = "UPDATE position SET " + 
        "number_of_shares = ?, value_paid = ? " + 
        "WHERE symbol = ?";
    private static final String DELETE = "DELETE FROM position WHERE symbol = ?";
    private static final String DELETE_ALL = "DELETE FROM position WHERE 1=1";
    private static final String FIND_BY_ID = "SELECT" +
        " symbol, number_of_shares, value_paid " +
        "FROM position WHERE symbol = ?";
    private static final String SELECT_ALL = "SELECT " + 
        "  symbol, number_of_shares, value_paid " +
        " FROM position";

    private Logger logger;

    PositionDao(Connection connection) {
        this.connection = connection;
        this.logger = LoggerFactory.getLogger(PositionDao.class);
        BasicConfigurator.configure();        
    }

    public void setUpdateStatement(PreparedStatement preparedStatement, Position position) throws SQLException {
        preparedStatement.setInt(1, position.getNumOfShares());
        preparedStatement.setDouble(2, position.getValuePaid());
        preparedStatement.setString(3, position.getTicker());
    }

    public void setInsertStatement(PreparedStatement preparedStatement, Position position) throws SQLException {
        preparedStatement.setString(1, position.getTicker());
        preparedStatement.setInt(2, position.getNumOfShares());
        preparedStatement.setDouble(3, position.getValuePaid());
    }

    public Position saveExisting(Position position) {
        try(PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
            this.setUpdateStatement(preparedStatement, position);
            preparedStatement.execute();
            return this.findById(position.getTicker()).get();
        } catch (SQLException e) {
            logger.error("Error while updating value " + position, e);
            throw new RuntimeException(e);
        }
    }
    
    public Position createNew(Position position) {
        try(PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO)) {
            this.setInsertStatement(preparedStatement, position);
            preparedStatement.execute();
            return this.findById(position.getTicker()).get();
        } catch (SQLException e) {
            logger.error("Error while creating value " + position, e);
            throw new RuntimeException(e);
        }    }

    @Override
    public Position save(Position entity) throws IllegalArgumentException {
        // throw new UnsupportedOperationException("Unimplemented method 'save'");
        if(entity == null) {
            throw new IllegalArgumentException();
        }
        boolean exists = this.findById(entity.getTicker()).isPresent();
        return exists ? this.saveExisting(entity) : this.createNew(entity); 
    }

    private Position getPositionFromRS(ResultSet rs) throws SQLException {
        Position position = new Position();
        position.setTicker(rs.getString(1));
        position.setNumOfShares(rs.getInt(2));
        position.setValuePaid(rs.getDouble(3));
        return position;
    }

    @Override
    public Optional<Position> findById(String id) throws IllegalArgumentException {
        // throw new UnsupportedOperationException("Unimplemented method 'findById'");
        try(PreparedStatement preparedStatement = this.connection.prepareStatement(FIND_BY_ID)) {
            preparedStatement.setString(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            Position position = null;
            while(rs.next()) {
                position = this.getPositionFromRS(rs);
            }
            return position == null ? Optional.empty() : Optional.of(position);
        } catch(SQLException e) {
            logger.error("Error while finding position with ID " + id, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Iterable<Position> findAll() {
        // throw new UnsupportedOperationException("Unimplemented method 'findAll'");
        ArrayList<Position> positions = new ArrayList<>();
        try(PreparedStatement preparedStatement = this.connection.prepareStatement(SELECT_ALL)) {
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                positions.add(this.getPositionFromRS(rs));
            }
            return positions;
        } catch(SQLException e) {
            logger.error("Error while selecting all", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(String id) throws IllegalArgumentException {
        try(PreparedStatement preparedStatement = this.connection.prepareStatement(DELETE)) {
            preparedStatement.setString(1, id);
            preparedStatement.execute();
        } catch(SQLException e) {
            logger.error("Error while deleting position with id " + id, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAll() {
        try(PreparedStatement preparedStatement = this.connection.prepareStatement(DELETE_ALL)) {
            preparedStatement.execute();
        } catch(SQLException e) {
            logger.error("Error while deleting all positions", e);
            throw new RuntimeException(e);
        }
    }

}