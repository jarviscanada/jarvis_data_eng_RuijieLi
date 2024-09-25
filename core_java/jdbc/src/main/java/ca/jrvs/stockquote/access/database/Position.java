package ca.jrvs.stockquote.access.database;

public class Position {
	private String ticker; //id
	private int numOfShares;
	private double valuePaid; //total amount paid for shares

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
}