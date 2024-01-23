package dal;

public class Grid {
    private String[][] table;
    private final int xLength;
    private final int yLength;
    private final String defaultTexture = ".";

    public Grid(int xLength, int yLength) {
        if (xLength <= 0 || yLength <= 0) {
            throw new IllegalArgumentException("Lengths must be positive.");
        }
        this.xLength = xLength;
        this.yLength = yLength;
        this.table = new String[yLength][xLength];
        initializeTable();
    }

    private void initializeTable() {
        for (int i = 0; i < yLength; i++) {
            for (int j = 0; j < xLength; j++) {
                table[i][j] = defaultTexture;
            }
        }
    }

    public void setTexture(int x, int y, String texture) {
        if (isPositionValid(x, y)) {
            table[y - 1][x - 1] = texture;
        }
    }
    public String getDefaultTexture() {
        return defaultTexture;
    }

    private boolean isPositionValid(int x, int y) {
        return x > 0 && y > 0 && x <= xLength && y <= yLength;
    }

    public String[][] getTable() {
        return table;
    }

    public int getXLength() {
        return xLength;
    }

    public int getYLength() {
        return yLength;
    }
}

