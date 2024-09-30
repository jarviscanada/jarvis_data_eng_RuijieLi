package ca.jrvs.stockquote.service.exceptions;

public class InvalidTickerException extends RuntimeException {
    public InvalidTickerException(String msg) {
        super(msg);
    }
}
