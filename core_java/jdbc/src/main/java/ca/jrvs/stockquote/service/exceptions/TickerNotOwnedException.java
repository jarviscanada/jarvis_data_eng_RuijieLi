package ca.jrvs.stockquote.service.exceptions;

public class TickerNotOwnedException extends RuntimeException {
    public TickerNotOwnedException(String msg) {
        super(msg);
    }
}
