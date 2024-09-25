package ca.jrvs.stockquote.access.database;

import java.sql.Date;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
/*
        : "TSLA",
        : "241.4900",
        : "243.9900",
        : "235.9200",
        : "238.2500",
        : "99766753",
        : "2024-09-20",
        : "243.9200",
        : "-5.6700",
        : "-2.3245%"
 */
public class Quote {

    @JsonProperty("01. symbol")
	private String ticker; //id
    @JsonProperty("02. open")
    private double open;
    @JsonProperty("03. high")
	private double high;
    @JsonProperty("04. low")
	private double low;
    @JsonProperty("05. price")
	private double price;
    @JsonProperty("06. volume")
	private int volume;
    @JsonProperty("07. latest trading day")
	private Date latestTradingDay;
    @JsonProperty("08. previous close")
	private double previousClose;
    @JsonProperty("09. change")
	private double change;
    @JsonProperty("10. change percent")
	private String changePercent;

	private Timestamp timestamp; //time when the info was pulled

    public Quote() {}

    public String getTicker() {
        return ticker;
    }
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
    public double getOpen() {
        return open;
    }
    public void setOpen(double open) {
        this.open = open;
    }
    public double getHigh() {
        return high;
    }
    public void setHigh(double high) {
        this.high = high;
    }
    public double getLow() {
        return low;
    }
    public void setLow(double low) {
        this.low = low;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public int getVolume() {
        return volume;
    }
    public void setVolume(int volume) {
        this.volume = volume;
    }
    public Date getLatestTradingDay() {
        return latestTradingDay;
    }
    public void setLatestTradingDay(Date latestTradingDay) {
        this.latestTradingDay = latestTradingDay;
    }
    public double getPreviousClose() {
        return previousClose;
    }
    public void setPreviousClose(double previousClose) {
        this.previousClose = previousClose;
    }
    public double getChange() {
        return change;
    }
    public void setChange(double change) {
        this.change = change;
    }
    public String getChangePercent() {
        return changePercent;
    }
    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public String toString() {
        return "{\n" +  
        "   ticker             :" + ticker              + "\n" +
        "   open               :" + open                + "\n" +
        "   high               :" + high                + "\n" +
        "   low                :" + low                 + "\n" +
        "   price              :" + price               + "\n" +
        "   volume             :" + volume              + "\n" +
        "   latestTradingDay   :" + latestTradingDay    + "\n" +
        "   previousClose      :" + previousClose       + "\n" +
        "   change             :" + change              + "\n" +
        "   changePercent      :" + changePercent       + "\n" +
        "   timestamp          :" + timestamp           + "\n" + 
        "}";
    }
    public boolean equals(Quote quote) {
        return 
            this.ticker.equals(quote.ticker) &&
            this.open == quote.open &&
            this.high == quote.high &&
            this.low == quote.low &&
            this.price == quote.price &&
            this.volume == quote.volume &&
            this.latestTradingDay.equals(quote.latestTradingDay) &&
            this.previousClose == quote.previousClose &&
            this.change == quote.change &&
            this.changePercent.equals(quote.changePercent) &&
            this.timestamp.equals(quote.timestamp);
    }
}