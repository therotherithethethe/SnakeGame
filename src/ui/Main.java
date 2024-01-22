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


        Grid grid = Grid.getInstance(10, 10);
        Cell snakeHead = new Cell(grid);
        GameLogic logic = GameLogic.getInstance(grid, snakeHead);

        Menu menu = new Menu(logic);
        menu.Print();
    }
}