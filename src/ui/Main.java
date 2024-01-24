package ui;

import bl.GameLogic;
import dal.Cell;
import dal.Grid;
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