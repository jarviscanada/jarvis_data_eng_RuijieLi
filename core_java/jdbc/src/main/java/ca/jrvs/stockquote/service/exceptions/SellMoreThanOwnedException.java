package ca.jrvs.stockquote.service.exceptions;

public class SellMoreThanOwnedException extends RuntimeException {
    public SellMoreThanOwnedException() {
        super("Cannot sell more than owned");
    }
}
