public class RadhaError extends Exception {
    // Default constructor
    public RadhaError() {
        super();
    }

    @Override
    public String toString() {
        return "RadhaError: " + getMessage();
    }
    // Constructor that accepts a custom message
    public RadhaError(String message) {
        super(message);
    }

    // Constructor that accepts a custom message and a cause
    public RadhaError(String message, Throwable cause) {
        super(message, cause);
    }

    // Constructor that accepts a cause
    public RadhaError(Throwable cause) {
        super(cause);
    }
}