package dal;

import dal.exception.AccountValidException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Account {
    private UUID id;
    private String userName;
    private String password;
    private SortedSet<Integer> records = new TreeSet<>(Comparator.naturalOrder());
    public int runCount;
    public boolean isDefault = false;
    public int entersCount;
    public Account(String nickName, String password) {
        this.id = UUID.randomUUID();
        setUsername(nickName);
        setPass(password);
        runCount = 0;
    }
       private void setPass(String password) {
           if(!isUsernameValid(password)) {
               throw new AccountValidException("pass is invalid. use noncyrilic or bigger than 3 length symbols");
           }

           this.password = String.valueOf(password.hashCode());

    }
    private void setUsername(String username) {
        if(!isUsernameValid(username)) {
            throw new AccountValidException("username is invalid. use noncyrilic or bigger than 2 length symbols");
        }
        this.userName = username;

    }

    public static boolean isUsernameValid(String username) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9_-]{2,30}$");

        Matcher matcher = pattern.matcher(username);

        return matcher.matches();
    }
    public static boolean isPassValid(String pass) {
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9][a-zA-Z0-9_-]{2,30}$");

        Matcher matcher = pattern.matcher(pass);

        return matcher.matches();
    }
    public UUID getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void addRecord(int record) {
        records.add(record);
    }

    public SortedSet<Integer> getRecords() {
        return records;
    }

    public int getRunCount() {
        return runCount;
    }
}
