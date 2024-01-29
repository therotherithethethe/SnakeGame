package dal;

public class Cell {
    private int x = 1;
    private int y = 1;
    private final Grid grid;
    private String texture = "■";

    public Cell(Grid grid)
    {
        this.grid = grid;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getTexture() {
        return texture;
    }
}
