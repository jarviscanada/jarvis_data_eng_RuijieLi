package ca.jrvs.exercise.json;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Quote {
        // "01. symbol": "MSFT",
        // "02. open": "441.2250",
        // "03. high": "441.5000",
        // "04. low": "436.9000",
        // "05. price": "438.6900",
        // "06. volume": "21706559",
        // "07. latest trading day": "2024-09-19",
        // "08. previous close": "430.8100",
        // "09. change": "7.8800",
        // "10. change percent": "1.8291%"
        @JsonProperty("01. symbol")
        String symbol;

        @JsonProperty("02. open")
        Double open;

        @JsonProperty("03. high")
        Double high;

        @JsonProperty("04. low")
        Double low;

        @JsonProperty("05. price")
        Double price;

        @JsonProperty("06. volume")
        Integer volume;

        @JsonProperty("07. latest trading day")
        Date latestTradingDay;

        @JsonProperty("08. previous close")
        Double previousClose;

        @JsonProperty("09. change")
        Double change;

        @JsonProperty("10. change percent")
        String changePercentStr;

        Double changePercent;

        public void setChangePercent(String percent) {
            changePercent = Double.parseDouble(percent.replace("%", ""));
        }
        public void setChangePercent(Double percent) {
            changePercent = percent;
        }
        public Double getChangePercent() {
            return this.changePercent;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public Double getOpen() {
            return open;
        }

        public void setOpen(Double open) {
            this.open = open;
        }

        public Double getHigh() {
            return high;
        }

        public void setHigh(Double high) {
            this.high = high;
        }

        public Double getLow() {
            return low;
        }

        public void setLow(Double low) {
            this.low = low;
        }

        public Double getPrice() {
            return price;
        }

        public void setPrice(Double price) {
            this.price = price;
        }

        public Integer getVolume() {
            return volume;
        }

        public void setVolume(Integer volume) {
            this.volume = volume;
        }

        public Date getLatestTradingDay() {
            return latestTradingDay;
        }

        public void setLatestTradingDay(Date latestTradingDay) {
            this.latestTradingDay = latestTradingDay;
        }

        public Double getPreviousClose() {
            return previousClose;
        }

        public void setPreviousClose(Double previousClose) {
            this.previousClose = previousClose;
        }

        public Double getChange() {
            return change;
        }

        public void setChange(Double change) {
            this.change = change;
        }

        public String getChangePercentStr() {
            return changePercentStr;
        }

        public void setChangePercentStr(String changePercentStr) {
            this.changePercentStr = changePercentStr;
        }

}
