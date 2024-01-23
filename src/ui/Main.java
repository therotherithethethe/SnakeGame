package ui;

import bl.GameLogic;
import dal.Cell;
import dal.Grid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

public class Main {

    public static void main(String[] args) throws Exception {
        java.util.logging.Logger.getLogger("org.jline").setLevel(Level.FINEST);


        Grid grid = new Grid(6, 4);
        Cell snakeHead = new Cell(grid);
        GameLogic logic = new GameLogic(grid, snakeHead);

        Menu menu = new Menu(logic);
        menu.Print();
    }
}