package dal;
/**
 * Represents the grid or play area of the snake game.
 * The grid is composed of cells arranged in a 2D array, and it defines the space in which the snake can move.
 */
public class Grid {
    // 2D array representing the cells of the grid
    private String[][] table;
    // The length of the grid in the X-direction
    private int xLength;
    // The length of the grid in the Y-direction
    private int yLength;
    // Default texture to represent an empty cell
    private static final String defaultTexture = ".";
    // Error message for invalid grid dimensions
    private static final String TYPICAL_EXCEPTION = "Lengths must be reserved.";

    /**
     * Constructor for creating a new Grid.
     * Validates the provided dimensions and initializes the grid with the default texture.
     *
     * @param xLength The length of the grid in the X-direction.
     * @param yLength The length of the grid in the Y-direction.
     * @throws IllegalArgumentException If the provided dimensions are not within the specified limits.
     */
    public Grid(int xLength, int yLength) {
        if (xLength <= 1 || yLength <= 1 || xLength >70 || yLength > 30) {
            throw new IllegalArgumentException(TYPICAL_EXCEPTION);
        }
        this.xLength = xLength;
        this.yLength = yLength;
        this.table = new String[yLength][xLength];
        initializeTable();
    }

    /**
     * Initializes the grid by filling it with the default texture.
     */
    private void initializeTable() {
        for (int i = 0; i < yLength; i++) {
            for (int j = 0; j < xLength; j++) {
                table[i][j] = defaultTexture;
            }
        }
    }

    /**
     * Sets the texture at a specific position in the grid.
     *
     * @param x The X-coordinate of the position.
     * @param y The Y-coordinate of the position.
     * @param texture The texture to set at the specified position.
     */
    public void setTexture(int x, int y, String texture) {
        if (isPositionValid(x, y)) {
            table[y - 1][x - 1] = texture;
        }
    }
    /**
     * Gets the default texture.
     *
     * @return The default texture.
     */
    public String getDefaultTexture() {
        return defaultTexture;
    }
    /**
     * Validates if the given position is within the bounds of the grid.
     *
     * @param x The X-coordinate of the position.
     * @param y The Y-coordinate of the position.
     * @return true if the position is valid, false otherwise.
     */
    private boolean isPositionValid(int x, int y) {
        return x > 0 && y > 0 && x <= xLength && y <= yLength;
    }

    /**
     * Gets the current state of the grid.
     *
     * @return A 2D array representing the current state of the grid.
     */
    public String[][] getTable() {
        return table;
    }

    /**
     * Gets the length of the grid in the X-direction.
     *
     * @return The length of the grid in the X-direction.
     */
    public int getXLength() {
        return xLength;
    }

    /**
     * Gets the length of the grid in the Y-direction.
     *
     * @return The length of the grid in the Y-direction.
     */
    public int getYLength() {
        return yLength;
    }

    /**
     * Sets the length of the grid in the X-direction and reinitializes the grid.
     *
     * @param xLength The new length of the grid in the X-direction.
     * @throws IllegalArgumentException If the provided length is not within the specified limits.
     */
    public void setxLength(int xLength) {
        if(xLength <= 1 || xLength >70)
            throw new IllegalArgumentException(TYPICAL_EXCEPTION);

        this.xLength = xLength;
        this.table = new String[yLength][xLength];
        initializeTable();
    }
    /**
     * Sets the length of the grid in the Y-direction and reinitializes the grid.
     *
     * @param yLength The new length of the grid in the Y-direction.
     * @throws IllegalArgumentException If the provided length is not within the specified limits.
     */

    public void setyLength(int yLength) {
        if(yLength <= 1 || yLength >30)
            throw new IllegalArgumentException(TYPICAL_EXCEPTION);

        this.yLength = yLength;
        this.table = new String[yLength][xLength];
        initializeTable();
    }
}

