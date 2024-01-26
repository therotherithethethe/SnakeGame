package dal.exception;

public class AccountIsNotFounded extends RuntimeException {
    public AccountIsNotFounded(String message) {
        super(message);
    }

}
