package example.core;

public class PyException extends RuntimeException {

    public PyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PyException(String message) { super(message); }

    public static PyException wrapped(Throwable t) {
        if (t instanceof PyException pe) {
            return pe;
        } else {
            return new PyException("Internal error", t);
        }
    }
}
