package bl;

import dal.Cell;
import dal.Direction;
import dal.Grid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.jline.keymap.KeyMap;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import org.jline.utils.InfoCmp.Capability;
import org.jline.utils.NonBlockingReader;

public class GameLogic {
    private static volatile GameLogic instance;
    private Grid grid;
    private Cell[] snakeCells = new Cell[1];
    private Cell food;
    private Direction currentDirection = Direction.RIGHT;

    private NonBlockingReader reader;
    private KeyMap<Capability> keyMap;
    private Terminal terminal;
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
    public GameLogic(Grid grid, Cell snakeHead) throws Exception {
        terminal = TerminalBuilder.builder()
            .dumb(true)
            .encoding(StandardCharsets.UTF_8)
            .build();
        reader = terminal.reader();

        keyTextures = initializeKeyTextures();
        this.grid = grid;
        snakeCells[0] = snakeHead;
        snakeHead.setTexture("▶");

        food = new Cell(grid);
        food.setTexture("X");

        initializeGameTable();
        spawnFood();
    }
    private void initializeGameTable() throws Exception {
        grid.setTextureToGrid(snakeCells[0].getX(), snakeCells[0].getY(), snakeCells[0].getTexture());
    }

    private static HashMap<Direction, String> initializeKeyTextures() {
        HashMap<Direction, String> keyTextures = new HashMap<>();
        keyTextures.put(Direction.LEFT, "<");
        keyTextures.put(Direction.RIGHT, ">");
        keyTextures.put(Direction.UP, "^");
        keyTextures.put(Direction.DOWN, "v");

        return keyTextures;
    }
    private void spawnFood() {
        Random r = new Random();
        boolean isFoodXYEqualSnakeXY;
        int x, y;
        int cellCounter = snakeCells.length;

        if( cellCounter == grid.getxLength()*grid.getyLength())
        {
            //do nothing, cause loop would be infinity
        }
        else
        {
            do
            {
                isFoodXYEqualSnakeXY = false;
                x = r.nextInt(1, grid.getxLength() + 1);
                y = r.nextInt(1, grid.getyLength() + 1);

                for (int i = 0; i < snakeCells.length; i++)
                {
                    if (x == snakeCells[i].getX() && y == snakeCells[i].getY())
                    {
                        isFoodXYEqualSnakeXY = true;
                    }

                }
            } while (isFoodXYEqualSnakeXY);



            food.setX(x);
            food.setY(y);
            grid.setTextureToGrid(food.getX(), food.getY(), food.getTexture());
        }

    }
    public void UpdateGameTable() throws IOException {
        Cell[] coordinatesBuffer = bufferCoordinates();
        Cell lastCell = storeLastCell();

        updateDirectionIfKeyPressed();

        updateHeadPositionBasedOnDirection();

        moveBody(coordinatesBuffer);

        updateGridTexture(coordinatesBuffer);

        checkAndHandleFoodCollision(lastCell);
    }
    private Cell[] bufferCoordinates() {
        Cell[] buffer = new Cell[snakeCells.length];
        for (int i = 0; i < buffer.length; i++)
        {
            buffer[i] = new Cell(grid);
            buffer[i].setX(snakeCells[i].getX());
            buffer[i].setY(snakeCells[i].getY());
        }
        return buffer;
    }
    private Cell storeLastCell() {
        Cell cell = new Cell(grid);
        cell.setX(snakeCells[snakeCells.length-1].getX());
        cell.setY(snakeCells[snakeCells.length-1].getY());
        return cell;
    }
    private void updateDirectionIfKeyPressed() throws IOException {
        if (reader.ready()) {
            int code = reader.read();
            if (code != -1) {
                char inputChar = (char) code;
                switch (inputChar) {
                    case 'w':
                        currentDirection = Direction.UP;
                        break;
                    case 's':
                        currentDirection = Direction.DOWN;
                        break;
                    case 'a':
                        currentDirection = Direction.LEFT;
                        break;
                    case 'd':
                        currentDirection = Direction.RIGHT;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void updateHeadPositionBasedOnDirection() {
        switch (currentDirection)
        {
            case UP:
                snakeCells[0].setY(snakeCells[0].getY() - 1);
                break;
            case DOWN:
                snakeCells[0].setY(snakeCells[0].getY() + 1);
                break;
            case LEFT:
                snakeCells[0].setX(snakeCells[0].getX() - 1);
                break;
            case RIGHT:
                snakeCells[0].setX(snakeCells[0].getX() + 1);
                break;
        }
        snakeCells[0].setTexture(keyTextures.get(currentDirection));
    }
    private void moveBody(Cell[] coordinatesBuffer) {
        for (int i = 1; i < coordinatesBuffer.length; i++)
        {
            snakeCells[i].setY(coordinatesBuffer[i - 1].getY());
            snakeCells[i].setX(coordinatesBuffer[i - 1].getX());
        }
    }
    private void updateGridTexture(Cell[] coordinatesBuffer) {
        for(var cell : snakeCells) {
            grid.setTextureToGrid(cell.getX(), cell.getY(), cell.getTexture());
        }

        Cell tailCell = coordinatesBuffer[coordinatesBuffer.length - 1];
        grid.setTextureToGrid(tailCell.getX(), tailCell.getY(), grid.getTexture());
    }
    private void checkAndHandleFoodCollision(Cell lastCell) {
        if (snakeCells[0].getX() == food.getX() && snakeCells[0].getY() == food.getY())
        {
            ExpandSnake(lastCell);
            spawnFood();
        }
    }
    private void ExpandSnake(Cell lastCell)
    {
        Cell[] newSnakeCells = new Cell[snakeCells.length + 1];
        System.arraycopy(snakeCells, 0, newSnakeCells, 0, snakeCells.length);

        newSnakeCells[newSnakeCells.length - 1] = new Cell(grid);
        newSnakeCells[newSnakeCells.length - 1].setX(lastCell.getX());
        newSnakeCells[newSnakeCells.length - 1].setY(lastCell.getY());

        snakeCells = newSnakeCells;
        grid.setTextureToGrid(lastCell.getX(), lastCell.getY(), lastCell.getTexture());
    }
    public boolean IsGameOver()
    {
        for (int i = 0; i < snakeCells.length; i++)
        {
            for (int j = 0; j < snakeCells.length; j++)
            {
                if (i == j || snakeCells.length == 1)
                {
                    continue;
                }
                if ((snakeCells[j].getX() == snakeCells[i].getX()) && (snakeCells[j].getY() == snakeCells[i].getY()))
                {
                    return true;
                }
            }
        }
        return snakeCells[0].getX() == grid.getxLength() + 1 || snakeCells[0].getX() < 1 || snakeCells[0].getY() == grid.getyLength() + 1 || snakeCells[0].getY() < 1;

    }

    public Grid getGrid() {
        return grid;
    }

    public Cell[] getSnakeCells() {
        return snakeCells;
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public NonBlockingReader getReader() {
        return reader;
    }
}
