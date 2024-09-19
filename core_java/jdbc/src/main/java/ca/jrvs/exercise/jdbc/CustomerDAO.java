package ca.jrvs.exercise.jdbc;
// this is an exercise file from Linkedin.
// I mostly copied it as is except using the logger instead of print stack trace

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jrvs.exercise.jdbc.util.DataAccessObject;

public class CustomerDAO extends DataAccessObject<Customer>{
    Logger logger;
    private static final String INSERT = "INSERT INTO customer "  +
        "(first_name, last_name, email, phone, address, city, state, zipcode) " + 
        "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String GET_BY_ID = "SELECT * FROM customer WHERE customer_id=?";

    private static final String UPDATE = "UPDATE customer SET " + 
        "first_name=?, last_name=?, email=?, phone=?, address=?, city=?, state=?, zipcode=? WHERE customer_id=?";

    private static final String DELETE = "DELETE FROM customer WHERE customer_id = ?";
    
    private static final String GET_ALL_LIMIT = "SELECT customer_id, first_name, last_name, email, phone, address, city, state, zipcode " 
        + "FROM customer ORDER BY last_name, first_name LIMIT ?";

    private static final String GET_ALL_PAGED = "SELECT customer_id, first_name, last_name, email, phone, address, city, state, zipcode " 
        + "FROM customer ORDER BY last_name, first_name LIMIT ? OFFSET ?";

    public CustomerDAO(Connection connection) {
        super(connection);
        logger = LoggerFactory.getLogger(CustomerDAO.class);
        BasicConfigurator.configure();
    }
    
    public List<Customer> findAllSorted(int limit) {
        List<Customer> customers = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_LIMIT)) {
            preparedStatement.setInt(1, limit);
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                Customer customer = new Customer(
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("city"),
                    rs.getString("state"),
                    rs.getString("zipcode")
                );
                customer.setId(rs.getLong("customer_id"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            logger.error("Error while retrieving " + limit + " customers: ", e);
            throw new RuntimeException(e);
        }
        return customers;
    }

    public List<Customer> findAllPaged(int limit, int pageNumber) {
        List<Customer> customers = new ArrayList<>();
        int offset = (pageNumber - 1) * limit;

        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_ALL_PAGED)) {

            preparedStatement.setInt(1, limit);
            preparedStatement.setInt(2, offset);

            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()) {
                Customer customer = new Customer(
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("city"),
                    rs.getString("state"),
                    rs.getString("zipcode")
                );
                customer.setId(rs.getLong("customer_id"));
                customers.add(customer);
            }
        } catch (SQLException e) {
            logger.error("Error while retrieving " + limit + " customers: ", e);
            throw new RuntimeException(e);
        }
        return customers;
    }


    @Override
    public Customer findById(long id) {
        Customer customer = null;
        try(PreparedStatement statement = connection.prepareStatement(GET_BY_ID)) {
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                customer = new Customer(
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("city"),
                    rs.getString("state"),
                    rs.getString("zipcode")
                );
                customer.setId(rs.getLong("customer_id"));
            }
            return customer;
        } catch(SQLException e) {
            logger.error("error while finding customer with id " + id, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Customer> findAll() {
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public Customer update(Customer dto) {
        // Customer customer = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE)) {
            setStatementAttr(preparedStatement, dto);
            preparedStatement.setLong(9, dto.getId());
            preparedStatement.execute();
            return this.findById(dto.getId());
        } catch (SQLException e) {
            logger.error("Error while updating customer with id " + dto.getId(), e);
            throw new RuntimeException(e);
        }
    }

    private void setStatementAttr(PreparedStatement preparedStatement, Customer dto) throws SQLException {
        preparedStatement.setString(1, dto.getFirstName());
        preparedStatement.setString(2, dto.getLastName());
        preparedStatement.setString(3, dto.getEmail());
        preparedStatement.setString(4, dto.getPhone());
        preparedStatement.setString(5, dto.getAddress());
        preparedStatement.setString(6, dto.getCity());
        preparedStatement.setString(7, dto.getState());
        preparedStatement.setString(8, dto.getZipCode());
    }

    @Override
    public Customer create(Customer dto) {
        // throw new UnsupportedOperationException("Unimplemented method 'create'");
        try(PreparedStatement statement = this.connection.prepareStatement(INSERT)) {
            this.setStatementAttr(statement, dto);
            statement.execute();
            int lastID = this.getLastVal(CUSTOMER_SEQUENCE);
            return this.findById(lastID);
        } catch(SQLException e) {
            logger.error("Error while accessing customer " + dto.toString(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(long id) {
        // throw new UnsupportedOperationException("Unimplemented method 'delete'");
        try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE)) {
            preparedStatement.setLong(1, id);
            preparedStatement.execute();
        } catch (Exception e) {
            logger.error("Error while deleting customer with id " + id, e);
            throw new RuntimeException(e);
        }
    }
}
