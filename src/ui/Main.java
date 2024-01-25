package ui;

import bl.AccountService;
import bl.GameLogic;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dal.Account;
import dal.Cell;
import dal.Grid;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;


public class Main {

    public static void main(String[] args) throws Exception {
        java.util.logging.Logger.getLogger("org.jline").setLevel(Level.FINEST);

        Grid grid = new Grid(6, 4);
        Cell snakeHead = new Cell(grid);
        GameLogic logic = new GameLogic(grid, snakeHead);

        GameMenu gameMenu = new GameMenu(logic);
        gameMenu.displayMenu();


    }
}