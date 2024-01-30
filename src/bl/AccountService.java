package bl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dal.Account;
import dal.exception.AccountIsNotFounded;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AccountService {
    private static AccountService instance;
    List<Account> accounts = new ArrayList<>();
    JsonArray jsonArray = new JsonArray();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String filePath = "Data/users.json";
    private AccountService() throws IOException {
        readJson();
        jsonArrayIntoArrayList();
    }
    public boolean isUsernameExistInDb(String username) {
        for(Account acc : accounts) {
            if(Objects.equals(acc.getUserName(), username)) {
                return true;
            }
        }
        return false;
    }
    public boolean isPassCorrectForCurrentUser(String username, String password) {
        for(Account acc : accounts) {
            if(Objects.equals(acc.getUserName(), username) && Objects.equals(acc.getPassword(),
                String.valueOf(password.hashCode()))) {
                return true;
            }
        }
        return false;
    }

    public static AccountService getInstance() throws IOException {
        if(instance == null) {
            instance = new AccountService();
        }
        return instance;
    }
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
    private void jsonArrayIntoArrayList() {
        for(JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();
            Account account = gson.fromJson(jsonObject, Account.class);
            accounts.add(account);
        }
    }

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
    public Account getAccountByUserName(String username) {
        for(Account acc : accounts) {
            if(Objects.equals(acc.getUserName(), username)) {
                return acc;
            }
        }
        throw new AccountIsNotFounded("acc isnt founded");
    }
    public void addAccount(Account acc) {
        accounts.add(acc);
    }
    public List<Account> getAccounts() {
        return accounts;
    }
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
