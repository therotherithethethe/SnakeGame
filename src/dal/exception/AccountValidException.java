package dal.exception;

public class AccountValidException extends RuntimeException {
    public AccountValidException(String message) {
        super(message);
    }

}
