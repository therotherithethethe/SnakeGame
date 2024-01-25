package bl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import dal.Account;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AccountService {
    private static AccountService instance;
    List<Account> accounts = new ArrayList<>();
    JsonArray jsonArray = new JsonArray();
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final String filePath = "Data/users.json";
    private AccountService() {
        readJson();
        jsonArrayIntoArrayList();
    }

    public static AccountService getInstance() {
        if(instance == null) {
            instance = new AccountService();
        }
        return instance;
    }
    private void readJson() {
        try (FileReader reader = new FileReader(filePath)) {
            JsonParser jsonParser = new JsonParser();
            jsonArray = (JsonArray) jsonParser.parse(reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void jsonArrayIntoArrayList() {
        for(JsonElement element : jsonArray) {
            JsonObject jsonObject = element.getAsJsonObject();
            Account account = gson.fromJson(jsonObject, Account.class);
            accounts.add(account);
        }
    }

    private void arrayListToJsonFile() {
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
    public void addAccount(Account acc) {
        accounts.add(acc);
    }
    public List<Account> getAccounts() {
        return accounts;
    }
}
