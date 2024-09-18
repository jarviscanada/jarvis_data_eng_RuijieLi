package ca.jrvs.exercise.jdbc;

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

public class OrderDAO extends DataAccessObject<Order>{
    public static final String GET_BY_ID = "SELECT " + 
        "c.first_name, c.last_name, c.email,\n" + //
        "o.order_id, o.creation_date, o.total_due, o.status,\n" + //
        "s.first_name, s.last_name, s.email,\n" + //
        "ol.quantity,\n" + //
        "p.code, p.name, p.size, p.variety, p.price\n" + //
        "FROM orders o\n" + //
        "JOIN customer c on o.customer_id = c.customer_id\n" + //
        "JOIN salesperson s on o.salesperson_id=s.salesperson_id\n" + //
        "JOIN order_item ol on ol.order_id = o.order_id\n" + //
        "JOIN product p on ol.product_id = p.product_id\n" + //
        "WHERE o.order_id = ?";
    
    public static final String GET_FOR_CUST = "SELECT * FROM get_orders_by_customer(?)";

    private Logger logger;
    public OrderDAO(Connection connection) {
        super(connection);
        logger = LoggerFactory.getLogger(OrderDAO.class);
        BasicConfigurator.configure();
    }

    @Override
    public Order findById(long id) {
        Order order = null;
        List<OrderItem> items = new ArrayList<>(); 
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_BY_ID)) {
            preparedStatement.setLong(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            Long order_id = null;
            order = new Order();

            while(rs.next()) {
                if(order_id == null) {
                    order_id = rs.getLong(4);
                    order.setId(order_id);
                    order.setCustomerFirstName(rs.getString(1));
                    order.setCustomerLastName(rs.getString(2));
                    order.setCustomerEmail(rs.getString(3));

                    order.setCreationDate(rs.getDate(5));
                    order.setTotalDue(rs.getBigDecimal(6));
                    order.setStatus(rs.getString(7));

                    order.setSalespersonFirstName(rs.getString(8));
                    order.setSalespersonLastName(rs.getString(9));
                    order.setSalespersonEmail(rs.getString(10));
                }
                OrderItem item = new OrderItem();
                item.setQuantity(rs.getInt(11)); 
                item.setProductCode(rs.getString(12));
                item.setProductName(rs.getString(13)); 
                item.setProductSize(rs.getInt(14)); 
                item.setProductVariety(rs.getString(15)); 
                item.setProductPrice(rs.getBigDecimal(16)); 
                items.add(item);
            }
            order.setItems(items);
        } catch (SQLException e) {
            logger.error("Error while reading order with id " + id, e);
        }
        return order;
    }

    @Override
    public List<Order> findAll() {
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public Order update(Order dto) {
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public Order create(Order dto) {
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public void delete(long id) {
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }
    public List<Order> getOrdersForCustomer(long customerID) {
        List<Order> orders = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(GET_FOR_CUST)) {
            preparedStatement.setLong(1, customerID);
            ResultSet rs = preparedStatement.executeQuery();
            Long orderID = null;
            Order currentOrder = null;
            while(rs.next()) {
                Long currentOrderID = rs.getLong(4);
                if(!currentOrderID.equals(orderID)) {
                    orderID = currentOrderID;

                    currentOrder = new Order();
                    logger.debug("currentOrderID " + currentOrderID + " orderID " + orderID );
                    currentOrder.setId(currentOrderID);
                    currentOrder.setCustomerFirstName(rs.getString(1));
                    currentOrder.setCustomerLastName(rs.getString(2));
                    currentOrder.setCustomerEmail(rs.getString(3));

                    currentOrder.setCreationDate(rs.getDate(5));
                    currentOrder.setTotalDue(rs.getBigDecimal(6));
                    currentOrder.setStatus(rs.getString(7));

                    currentOrder.setSalespersonFirstName(rs.getString(8));
                    currentOrder.setSalespersonLastName(rs.getString(9));
                    currentOrder.setSalespersonEmail(rs.getString(10));

                    currentOrder.setItems(new ArrayList<>());
                    orders.add(currentOrder);
                }
                OrderItem item = new OrderItem();
                item.setQuantity(rs.getInt(11)); 
                item.setProductCode(rs.getString(12));
                item.setProductName(rs.getString(13)); 
                item.setProductSize(rs.getInt(14)); 
                item.setProductVariety(rs.getString(15)); 
                item.setProductPrice(rs.getBigDecimal(16));
                currentOrder.getItems().add(item); 
            }
        } catch (Exception e) {
            logger.error("Error while getting orders for customer " + customerID, e);
        }
        return orders;
    }
}
