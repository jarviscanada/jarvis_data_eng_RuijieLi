package ca.jrvs.exercise.jdbc;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import ca.jrvs.exercise.jdbc.util.DataTransferObject;

public class Order implements DataTransferObject {
    private long id;
    private Date creationDate;
    private BigDecimal totalDue;

    private String status;
    private String salespersonFirstName;
    private String salespersonLastName;
    private String salespersonEmail;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;

    private List<OrderItem> items;


    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {

        return "Order" + " {\n" + 
            "   id                      : " + id                    + "\n" +
            "   creationDate            : " + creationDate          + "\n" +
            "   totalDue                : " + totalDue              + "\n" +
            "   status                  : " + status                + "\n" +
            "   salespersonFirstName    : " + salespersonFirstName  + "\n" +
            "   salespersonLastName     : " + salespersonLastName   + "\n" +
            "   salesperson_email       : " + salespersonEmail      + "\n" +
            "   customerFirstName       : " + customerFirstName     + "\n" +
            "   customerLastName        : " + customerLastName      + "\n" +
            "   customerEmail           : " + customerEmail         + "\n" +
            "   items                   : " + items                 + "\n" +
        "}";
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public BigDecimal getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(BigDecimal totalDue) {
        this.totalDue = totalDue;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSalespersonFirstName() {
        return salespersonFirstName;
    }

    public void setSalespersonFirstName(String salespersonFirstName) {
        this.salespersonFirstName = salespersonFirstName;
    }

    public String getSalespersonLastName() {
        return salespersonLastName;
    }

    public void setSalespersonLastName(String salespersonLastName) {
        this.salespersonLastName = salespersonLastName;
    }

    public String getSalespersonEmail() {
        return salespersonEmail;
    }

    public void setSalespersonEmail(String salespersonEmail) {
        this.salespersonEmail = salespersonEmail;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Override
    public long getId() {
        return this.id;
    }

}
