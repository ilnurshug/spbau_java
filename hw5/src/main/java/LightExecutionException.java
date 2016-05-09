
public class LightExecutionException extends Exception {
    private final Throwable cause;

    public LightExecutionException(Throwable cause) {
        this.cause = cause;
    }

    public Throwable getCause() {
        return cause;
    }
}