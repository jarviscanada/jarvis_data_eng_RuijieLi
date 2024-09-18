package ca.jrvs.exercise.jdbc;

import java.math.BigDecimal;

public class OrderItem {
    private int quantity;
    private String productName;
    private String productCode;
    private int productSize;
    private String productVariety;
    private BigDecimal productPrice;

   public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getProductCode() {
        return productCode;
    }
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    public int getProductSize() {
        return productSize;
    }
    public void setProductSize(int productSize) {
        this.productSize = productSize;
    }
    public String getProductVariety() {
        return productVariety;
    }
    public void setProductVariety(String productVariety) {
        this.productVariety = productVariety;
    }
    public BigDecimal getProductPrice() {
        return productPrice;
    }
    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }
    public String toString() {
        return "\n           {\n" +
            "           quantity        : " + quantity          + ",\n" +
            "           productName     : " + productName       + ",\n" +
            "           productCode     : " + productCode       + ",\n" +
            "           productSize     : " + productSize       + ",\n" +
            "           productVariety  : " + productVariety    + ",\n" +
            "           productPrice    : " + productPrice      + ",\n" +
        "           }\n";
    }
}
