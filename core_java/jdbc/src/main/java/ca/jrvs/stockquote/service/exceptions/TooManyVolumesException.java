package ca.jrvs.stockquote.service.exceptions;

public class TooManyVolumesException extends Exception {
    public TooManyVolumesException() {
        super("Cannot buy more than available volume");
    }
}
