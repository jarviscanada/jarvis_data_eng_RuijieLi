package ca.jrvs.stockquote.service.exceptions;

public class TooManyVolumesException extends RuntimeException {
    public TooManyVolumesException() {
        super("Cannot buy more than available volume");
    }
}
