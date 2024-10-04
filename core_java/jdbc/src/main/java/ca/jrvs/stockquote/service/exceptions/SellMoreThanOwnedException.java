package ca.jrvs.stockquote.service.exceptions;

public class SellMoreThanOwnedException extends Exception {
    public SellMoreThanOwnedException() {
        super("Cannot sell more than owned");
    }
}
