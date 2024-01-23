package aws.example.exception;

public class LambdaException extends RuntimeException {
    public LambdaException(String message) {
        super(message);
    }
}
