package aws.example.exception;

public class SQSException extends RuntimeException {
    public SQSException(String message) {
        super(message);
    }

}
