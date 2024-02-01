package bl;

import dal.Cell;
import dal.Direction;
import dal.Grid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Manages the game logic for the snake game.
 * This class handles the movement of the snake, collisions with food, and game state (win/lose conditions).
 */
public class GameLogic {
    // The grid on which the game is played
    private Grid grid;
    // Array representing the snake's cells
    private Cell[] snakeCells = new Cell[1];
    // The cell representing the food
    private Cell food;
    // The current direction of the snake's movement
    private Direction currentDirection = Direction.RIGHT;

    // A map that associates each direction with its corresponding texture
    private Map<Direction, String> keyTextures = new HashMap<>();
    /**
     * Constructor for creating a new GameLogic instance.
     *
     * @param grid The grid on which the game is played.
     * @param snakeHead The initial cell of the snake.
     * @throws Exception If initialization fails.
     */
    public GameLogic(Grid grid, Cell snakeHead) throws Exception {

        keyTextures = initializeKeyTextures();
        this.grid = grid;
        snakeCells[0] = snakeHead;
        snakeHead.setTexture(keyTextures.get(currentDirection));

        food = new Cell(grid);
        food.setTexture("X");

        initializeGameTable();
        //spawnFood();
    }
    /**
     * Initializes the game table by setting the texture of the snake's initial position.
     */
    private void initializeGameTable() throws Exception {
        grid.setTexture(snakeCells[0].getX(), snakeCells[0].getY(), snakeCells[0].getTexture());
    }

    /**
     * Sets the grid for the game and initializes the game table and food.
     *
     * @param grid The new grid to set for the game.
     * @throws Exception If initialization fails.
     */
    public void setGrid(Grid grid) throws Exception {
        this.grid = grid;
        initializeGameTable();
        spawnFood();
    }

    /**
     * Initializes the key textures for each direction.
     *
     * @return A HashMap associating each direction with its corresponding texture.
     */
    private static HashMap<Direction, String> initializeKeyTextures() {
        HashMap<Direction, String> keyTextures = new HashMap<>();
        keyTextures.put(Direction.LEFT, "◄");
        keyTextures.put(Direction.RIGHT, "►");
        keyTextures.put(Direction.UP, "▲");
        keyTextures.put(Direction.DOWN, "▼");

        return keyTextures;
    }
    /**
     * Randomly places food on the grid, ensuring it does not overlap with the snake.
     */
    private void spawnFood() {
        Random r = new Random();
        boolean isFoodXYEqualSnakeXY;
        int x, y;
        int cellCounter = snakeCells.length;

        if(cellCounter != grid.getXLength()*grid.getYLength())
        {
            do
            {
                isFoodXYEqualSnakeXY = false;
                x = r.nextInt(1, grid.getXLength() + 1);
                y = r.nextInt(1, grid.getYLength() + 1);

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
            grid.setTexture(food.getX(), food.getY(), food.getTexture());
        }

    }
    /**
     * Updates the game table based on the current direction of the snake.
     * Handles the movement of the snake and checks for collisions with food.
     *
     * @param direction The new direction in which the snake is moving.
     * @throws IOException If an I/O error occurs.
     */
    public void updateGameTable(Direction direction) throws IOException {
        Cell[] coordinatesBuffer = bufferCoordinates();
        Cell lastCell = storeLastCell();

        currentDirection = direction;
        updateHeadPositionBasedOnDirection();
        moveBody(coordinatesBuffer);
        updateGridTexture(coordinatesBuffer);
        checkAndHandleFoodCollision(lastCell);
    }
    /**
     * Buffers the current coordinates of all snake cells.
     *
     * @return An array of cells representing the buffered coordinates.
     */
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
    /**
     * Stores the last cell of the snake.
     *
     * @return The last cell of the snake before the snake moves.
     */
    private Cell storeLastCell() {
        Cell cell = new Cell(grid);
        cell.setX(snakeCells[snakeCells.length-1].getX());
        cell.setY(snakeCells[snakeCells.length-1].getY());
        return cell;
    }

    /**
     * Updates the head position of the snake based on the current direction.
     */
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
    /**
     * Moves the body of the snake, following the head.
     *
     * @param coordinatesBuffer A buffer of the snake cells' coordinates before the move.
     */
    private void moveBody(Cell[] coordinatesBuffer) {
        for (int i = 1; i < coordinatesBuffer.length; i++)
        {
            snakeCells[i].setY(coordinatesBuffer[i - 1].getY());
            snakeCells[i].setX(coordinatesBuffer[i - 1].getX());
        }
    }
    /**
     * Updates the textures on the grid based on the current positions of the snake cells.
     *
     * @param coordinatesBuffer A buffer of the snake cells' coordinates before the move.
     */
    private void updateGridTexture(Cell[] coordinatesBuffer) {
        for(var cell : snakeCells) {
            grid.setTexture(cell.getX(), cell.getY(), cell.getTexture());
        }

        Cell tailCell = coordinatesBuffer[coordinatesBuffer.length - 1];
        if((snakeCells[0].getX() == coordinatesBuffer[coordinatesBuffer.length - 1].getX())
            && (snakeCells[0].getY() == coordinatesBuffer[coordinatesBuffer.length - 1].getY())
            && (snakeCells.length != 1)) {
            grid.setTexture(snakeCells[0].getX(), snakeCells[0].getY(), snakeCells[0].getTexture());
        }
        else {
            grid.setTexture(tailCell.getX(), tailCell.getY(), grid.getDefaultTexture());
        }

    }
    /**
     * Checks for a collision between the snake's head and the food. If a collision occurs, expands the snake.
     *
     * @param lastCell The last cell of the snake before the move.
     */
    private void checkAndHandleFoodCollision(Cell lastCell) {
        if (snakeCells[0].getX() == food.getX() && snakeCells[0].getY() == food.getY())
        {
            expandSnake(lastCell);
            spawnFood();
        }
    }
    /**
     * Expands the snake by adding a new cell at the end.
     *
     * @param lastCell The cell to be added to the end of the snake.
     */
    private void expandSnake(Cell lastCell)
    {
        Cell[] newSnakeCells = new Cell[snakeCells.length + 1];
        System.arraycopy(snakeCells, 0, newSnakeCells, 0, snakeCells.length);

        newSnakeCells[newSnakeCells.length - 1] = new Cell(grid);
        newSnakeCells[newSnakeCells.length - 1].setX(lastCell.getX());
        newSnakeCells[newSnakeCells.length - 1].setY(lastCell.getY());

        snakeCells = newSnakeCells;
        grid.setTexture(lastCell.getX(), lastCell.getY(), lastCell.getTexture());
    }
    /**
     * Checks if the game is lost, which occurs if the snake collides with itself or the walls.
     *
     * @return true if the game is lost, false otherwise.
     */
    public boolean isGameLose()
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
        return snakeCells[0].getX() == grid.getXLength() + 1 ||
            snakeCells[0].getX() < 1 ||
            snakeCells[0].getY() == grid.getYLength() + 1 ||
            snakeCells[0].getY() < 1;
    }
    /**
     * Checks if the game is won, which occurs if the snake fills the entire grid.
     *
     * @return true if the game is won, false otherwise.
     */
    public boolean isGameWon() {
        return getCellCounter() == grid.getXLength() * grid.getYLength();
    }
    /**
     * Counts the number of cells occupied by the snake.
     *
     * @return The number of cells occupied by the snake.
     */
    private int getCellCounter() {
        return snakeCells.length;
    }

    /**
     * Gets the current grid.
     *
     * @return The current grid.
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Gets the cells that form the snake.
     *
     * @return An array of cells representing the snake.
     */
    public Cell[] getSnakeCells() {
        return snakeCells;
    }
}
