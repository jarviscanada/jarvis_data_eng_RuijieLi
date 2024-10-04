package ca.jrvs.stockquote.service.exceptions;

public class TickerNotOwnedException extends Exception {
    public TickerNotOwnedException(String msg) {
        super(msg);
    }
}
