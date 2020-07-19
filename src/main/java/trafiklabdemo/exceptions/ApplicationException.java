package trafiklabdemo.exceptions;

public abstract class ApplicationException extends Exception {
    public ApplicationException(String message) {
        super(message);
    }
}
