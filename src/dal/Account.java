package dal;

import dal.exception.AccountValidException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Account {
    private UUID id;
    private String userName;
    private String password;
    private Set<Integer> records = new HashSet();
    private int runCount;
    public Account(String nickName, String password) {
        this.id = UUID.randomUUID();
        setUsername(nickName);
        setPass(password);
        runCount = 0;
    }
       private void setPass(String password) {
        Pattern pattern = Pattern.compile("^[а-яА-Я]{0,3}$");
        Matcher matcher = pattern.matcher(password);
        if(matcher.matches()) {
            throw new AccountValidException("pass is invalid. use noncyrilic or bigger than 3 length symbols");
        }
        this.password = String.valueOf(password.hashCode());

    }
    private void setUsername(String username) {
        Pattern pattern = Pattern.compile("^[а-яА-Я]{0,2}$");
        Matcher matcher = pattern.matcher(username);

        if(matcher.matches()) {
            throw new AccountValidException("username is invalid. use noncyrilic or bigger than 2 length symbols");
        }
        this.userName = username;
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

    public Set getRecords() {
        return records;
    }

    public int getRunCount() {
        return runCount;
    }
}
