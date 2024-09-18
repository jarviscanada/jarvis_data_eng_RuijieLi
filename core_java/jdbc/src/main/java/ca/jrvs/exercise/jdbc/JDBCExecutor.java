package ca.jrvs.exercise.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public class JDBCExecutor {
    public static void main(String... args){
        DatabaseConnectionManager dcm = new DatabaseConnectionManager(
            "localhost:5432",
            "hplussport",
            "postgres",
            "password"
        );
        try {
            Connection connection = dcm.getConnection();
            CustomerDAO customerDAO = new CustomerDAO(connection);
            // System.out.println(
            //     customerDAO.findAllSorted(5)
            // );
            for(int i = 1; i < 4; i++) {
                System.out.println(customerDAO.findAllPaged(10, i));
                System.out.println("========================== page " + i + " ==========================");
            }
            // Customer customer = new Customer(
            //     "George",
            //     "Washington",
            //     "george.washington@polymtl.ca",
            //     "123-456-789",
            //     "12345 Wall Street",
            //     "St-jean-sur-Richelieu",
            //     "Quebec",
            //     "1q2 w3e"
            // );
            // customerDAO.create(customer);
            // System.out.println(customerDAO.findById(10002));

            // customerDAO.delete(10002);

            // System.out.println(customerDAO.findById(10002));
            // OrderDAO orderDAO = new OrderDAO(connection);
            // Order order = orderDAO.findById(1000);
            // List<Order> orders = orderDAO.getOrdersForCustomer(789);
            // System.out.println(orders);
        } catch(SQLException e) {

        }
    }
}