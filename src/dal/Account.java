package dal;

import dal.exception.AccountValidException;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a user account, storing user details and game records.
 * This class manages account creation, validation of credentials, and storage of game performance records.
 */
public class Account {

    // Unique identifier for the account
    private UUID id;
    // Username of the account
    private String userName;
    // Hashed password of the account
    private String password;
    // Sorted set of integers to store game records
    private SortedSet<Integer> records = new TreeSet<>(Comparator.naturalOrder());
    // The number of runs (games played) by the account
    public int runCount;
    // Flag to indicate if this is a default account
    public boolean isDefault = false;
    // The number of times the account has been accessed
    public int entersCount;
    /**
     * Constructor for creating a new Account.
     * Initializes the account with a UUID, username, and password.
     *
     * @param nickName The nickname for the account.
     * @param password The password for the account.
     */

    public Account(String nickName, String password) {
        this.id = UUID.randomUUID();
        setUsername(nickName);
        setPass(password);
        runCount = 0;
    }

    /**
     * Sets the account password after validating it.
     * Throws AccountValidException if the password is invalid.
     *
     * @param password The password to set for the account.
     */
    private void setPass(String password) {
        if (!isUsernameValid(password)) {
            throw new AccountValidException(
                "pass is invalid. use noncyrilic or bigger than 3 length symbols");
        }

        this.password = String.valueOf(password.hashCode());

    }

    /**
     * Sets the account username after validating it.
     * Throws AccountValidException if the username is invalid.
     *
     * @param username The username to set for the account.
     */
    private void setUsername(String username) {
        if (!isUsernameValid(username)) {
            throw new AccountValidException(
                "username is invalid. use noncyrilic or bigger than 2 length symbols");
        }
        this.userName = username;

    }

    /**
     * Validates the username using a regex pattern.
     *
     * @param username The username to validate.
     * @return true if the username is valid, false otherwise.
     */
    public static boolean isUsernameValid(String username) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9_-]{2,30}$");

        Matcher matcher = pattern.matcher(username);

        return matcher.matches();
    }

    /**
     * Validates the password using a regex pattern.
     *
     * @param pass The password to validate.
     * @return true if the password is valid, false otherwise.
     */
    public static boolean isPassValid(String pass) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9_-]{2,30}$");

        Matcher matcher = pattern.matcher(pass);

        return matcher.matches();
    }
    /**
     * Gets the unique identifier of the account.
     *
     * @return The UUID of the account.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the username of the account.
     *
     * @return The username of the account.
     */
    public String getUserName() {
        return userName;
    }
    /**
     * Gets the hashed password of the account.
     *
     * @return The hashed password of the account.
     */

    public String getPassword() {
        return password;
    }
    /**
     * Adds a new game record to the account.
     *
     * @param record The new game record to add.
     */

    public void addRecord(int record) {
        records.add(record);
    }
    /**
     * Gets the sorted set of game records of the account.
     *
     * @return The sorted set of game records.
     */
    public SortedSet<Integer> getRecords() {
        return records;
    }
}