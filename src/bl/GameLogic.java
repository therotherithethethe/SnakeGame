package bl;

import dal.Cell;
import dal.Direction;
import dal.Grid;
import java.util.HashMap;
import java.util.Map;

public class GameLogic {
    private static volatile GameLogic instance;
    private Grid grid;
    private Cell[] snakeCells = new Cell[1];
    private Cell food;
    private Direction currentDirection = Direction.RIGHT;
    private Map<Direction, String> keyTextures = new HashMap<>();
    public static GameLogic getInstance(Grid grid, Cell snakeHead) throws Exception {
        GameLogic localInstance = instance;

        if (localInstance == null) {
            synchronized (GameLogic.class) {
                localInstance = instance;

                if (localInstance == null) {
                    instance = localInstance = new GameLogic(grid, snakeHead);
                }
            }
        }
        return localInstance;
    }
    private GameLogic(Grid grid, Cell snakeHead) throws Exception {
        keyTextures = initializeKeyTextures();
        this.grid = grid;
        snakeCells[0] = snakeHead;
        snakeHead.setTexture("▶");

        food = new Cell(grid);
        food.setTexture("X");

        initializeGameTable();
        SpawnFood();
    }
    private void initializeGameTable() throws Exception {
        grid.setTextureToGrid(snakeCells[0].getX(), snakeCells[0].getY(), snakeCells[0].getTexture());
    }

    private static HashMap<Direction, String> initializeKeyTextures()
    {
        HashMap<Direction, String> keyTextures = new HashMap<>();
        keyTextures.put(Direction.LEFT, "◀");
        keyTextures.put(Direction.RIGHT, "▶");
        keyTextures.put(Direction.UP, "▲");
        keyTextures.put(Direction.DOWN, "▼");

        return keyTextures;
    }
}
