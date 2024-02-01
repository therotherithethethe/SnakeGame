package bl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dal.Account;
import dal.exception.AccountIsNotFoundedException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Provides services for managing user accounts.
 * This class handles operations such as account creation, deletion, authentication, and persisting account data in a JSON file.
 */
public class AccountService {
    // Singleton instance of AccountService
    private static AccountService instance;
    // List of accounts
    List<Account> accounts = new ArrayList<>();
    // JsonArray to hold the accounts data
    JsonArray jsonArray = new JsonArray();
    // Gson instance for JSON processing
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    // File path for the JSON file containing account data
    private final String filePath = "Data/users.json";
    /**
     * Private constructor for creating a singleton instance of AccountService.
     * Reads the JSON file containing account data and converts it into a list of Account objects.
     *
     * @throws IOException If an I/O error occurs while reading the JSON file.
     */
    private AccountService() throws IOException {
        readJson();
        jsonArrayIntoArrayList();
    }
    /**
     * Checks if a username exists in the database (in-memory list).
     *
     * @param username The username to check.
     * @return true if the username exists, false otherwise.
     */
    public boolean isUsernameExistInDb(String username) {
        for(Account acc : accounts) {
            if(Objects.equals(acc.getUserName(), username)) {
                return true;
            }
        }
        return false;
    }
    /**
     * Checks if the password is correct for the current user.
     *
     * @param username The username of the account.
     * @param password The password to validate.
     * @return true if the password is correct for the given username, false otherwise.
     */
    public boolean isPassCorrectForCurrentUser(String username, String password) {
        for(Account acc : accounts) {
            if(Objects.equals(acc.getUserName(), username) && Objects.equals(acc.getPassword(),
                String.valueOf(password.hashCode()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the singleton instance of AccountService, creating it if it does not exist.
     *
     * @return The singleton instance of AccountService.
     * @throws IOException If an I/O error occurs while creating the instance.
     */
    public static AccountService getInstance() throws IOException {
        if(instance == null) {
            instance = new AccountService();
        }
        return instance;
    }
    /**
     * Reads the JSON file containing account data into a JsonArray.
     *
     * @throws IOException If an I/O error occurs while reading the file.
     */
    private void readJson() throws IOException {
        File usersFile = new File(filePath);
        if(usersFile.exists() && usersFile.length() != 0) {
            try (FileReader reader = new FileReader(filePath)) {
                JsonParser jsonParser = new JsonParser();
                jsonArray = (JsonArray) jsonParser.parse(reader);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            File parentDir = usersFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            usersFile.createNewFile();
        }
    }
    /**
     * Converts the JsonArray containing account data into a list of Account objects.
     */
    private void jsonArrayIntoArrayList() {
        for(JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();
            Account account = gson.fromJson(jsonObject, Account.class);
            accounts.add(account);
        }
    }
    /**
     * Converts the list of Account objects into a JsonArray and writes it to the JSON file.
     */
    public void arrayListToJsonFile() {
        JsonArray jsonArray = new JsonArray();
        for (Account account : accounts) {
            JsonObject accountJson = gson.toJsonTree(account).getAsJsonObject();
            jsonArray.add(accountJson);
        }
        String json = gson.toJson(jsonArray);

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Retrieves an account by its username.
     *
     * @param username The username of the account to retrieve.
     * @return The Account object if found.
     * @throws AccountIsNotFoundedException If the account is not found.
     */
    public Account getAccountByUserName(String username) {
        for(Account acc : accounts) {
            if(Objects.equals(acc.getUserName(), username)) {
                return acc;
            }
        }
        throw new AccountIsNotFoundedException("acc isnt founded");
    }
    /**
     * Adds a new account to the list and persists it to the JSON file.
     *
     * @param acc The account to add.
     */
    public void addAccount(Account acc) {
        accounts.add(acc);
    }
    /**
     * Gets the list of accounts.
     *
     * @return The list of accounts.
     */
    public List<Account> getAccounts() {
        return accounts;
    }
    /**
     * Deletes an account from the list and updates the JSON file.
     *
     * @param account The account to delete.
     * @throws IOException If an I/O error occurs while updating the file.
     */
    public void deleteAccount(Account account) throws IOException {
        readJson();
        for(JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();
            Account tmpaccount = gson.fromJson(jsonObject, Account.class);
            if(account.getId().equals(tmpaccount.getId())) {
                jsonArray.remove(jsonObject);
                accounts.remove(account);
                break;
            }
        }
        arrayListToJsonFile();
    }
}
