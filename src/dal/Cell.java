package dal;

/**
 * Represents a single cell in the grid of the snake game.
 * A cell is a basic unit in the grid, and it can represent part of the snake, an empty space, or a fruit.
 */
public class Cell {
    // X-coordinate of the cell in the grid
    private int x = 1;
    // Y-coordinate of the cell in the grid
    private int y = 1;
    // The grid to which this cell belongs
    private final Grid grid;
    // The texture or symbol representing the cell's appearance
    private String texture = "■";

    /**
     * Constructor for creating a new Cell with a reference to the grid it belongs to.
     *
     * @param grid The grid in which this cell is located.
     */
    public Cell(Grid grid)
    {
        this.grid = grid;
    }

    /**
     * Sets the X-coordinate of the cell.
     *
     * @param x The X-coordinate to set for this cell.
     */
    public void setX(int x) {
        this.x = x;
    }
    /**
     * Sets the Y-coordinate of the cell.
     *
     * @param y The Y-coordinate to set for this cell.
     */

    public void setY(int y) {
        this.y = y;
    }

    /**
     * Sets the texture or symbol that visually represents this cell.
     *
     * @param texture The string representing the texture or symbol of this cell.
     */
    public void setTexture(String texture) {
        this.texture = texture;
    }
    /**
     * Gets the X-coordinate of this cell.
     *
     * @return The X-coordinate of this cell.
     */

    public int getX() {
        return x;
    }
    /**
     * Gets the Y-coordinate of this cell.
     *
     * @return The Y-coordinate of this cell.
     */

    public int getY() {
        return y;
    }
    /**
     * Gets the texture or symbol representing this cell's appearance.
     *
     * @return A string representing the texture or symbol of this cell.
     */
    public String getTexture() {
        return texture;
    }
}
