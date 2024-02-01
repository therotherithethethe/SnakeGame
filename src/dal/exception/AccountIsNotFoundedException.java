package dal.exception;

public class AccountIsNotFoundedException extends RuntimeException {
    public AccountIsNotFoundedException(String message) {
        super(message);
    }

}
