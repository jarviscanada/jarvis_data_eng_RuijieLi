package ca.jrvs.stockquote.access.database;

import ca.jrvs.stockquote.controller.StringUtil;

public class Position {
    private String ticker; //id
    private int numOfShares;
    private double valuePaid; //total amount paid for shares
    private double currentValue;

    public double getCurrentValue() {
        return currentValue;
    }
    public void setCurrentValue(double currentValue) {
        this.currentValue = currentValue;
    }

    public String getTicker() {
        return ticker;
    }
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
    public int getNumOfShares() {
        return numOfShares;
    }
    public void setNumOfShares(int numOfShares) {
        this.numOfShares = numOfShares;
    }
    public double getValuePaid() {
        return valuePaid;
    }
    public void setValuePaid(double valuePaid) {
        this.valuePaid = valuePaid;
    }
    public boolean equals(Position position) {
        return this.ticker.equals(position.ticker) &&
            this.numOfShares == position.numOfShares &&
            this.valuePaid == position.valuePaid;
    }

    public String toString() {
        return "{\n" +
            "   ticker      : " + ticker        + "\n" +
            "   numOfShares : " + numOfShares   + "\n" +
            "   valuePaid   : " + valuePaid     + "\n" +
        "}";
    }
    public static String[] getAttributeTitles() {
        String[] titles = {
            "Ticker",
            "# of shares",
            "Value paid",
            "Current value"
        };
        return titles;
    }

    public String[] getAttributeValues() {
        String[] values = {
            ticker,
            numOfShares + "",
            valuePaid + "",
            currentValue + ""
        };
        return values;
    }

    public String toUserString() {
        String[] titles = Position.getAttributeTitles();
        String[] values = this.getAttributeValues();
        return StringUtil.toUserString(values, titles);
    }
}