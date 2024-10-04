package ca.jrvs.stockquote.service.exceptions;

public class InvalidTickerException extends Exception {
    public InvalidTickerException(String msg) {
        super(msg);
    }
}
