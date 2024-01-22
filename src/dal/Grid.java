package dal;

public class Grid
{
    private static volatile Grid instance;
    private String[][] table;
    private final int xLength;
    private final int yLength;
    private final String texture = "⬜";
    private Grid(int xLength, int yLength) throws Exception {
        if (xLength <= 0 || yLength <= 0)
            throw new Exception("Lengths must be positive.");


        table = new String[yLength][xLength];
        this.xLength = xLength;
        this.yLength = yLength;
        tableInitialize();
    }

    public static Grid getInstance(int xLength, int yLength) throws Exception {
        Grid localInstance = instance;

        if (localInstance == null) {
            synchronized (Grid.class) {
                localInstance = instance;

                if (localInstance == null) {
                    instance = localInstance = new Grid(xLength, yLength);
                }
            }
        }
        return localInstance;
    }
    private void tableInitialize()
    {
        for (int i = 0; i < yLength; i++)
        {
            for (int j = 0; j < xLength; j++)
            {
                table[i][j] = texture;
            }
        }
    }

    public void setTextureToGrid(int x, int y, String texture) throws Exception {
        if(x <= 0 || y <= 0 || x >= xLength + 1 || y >= yLength + 1)
        {
            // do nothing
        }
        else
        {
            table[y - 1][x - 1] = texture;
        }

    }

    public String[][] getTable() {
        return table;
    }

    public int getxLength() {
        return xLength;
    }

    public int getyLength() {
        return yLength;
    }

    public String getTexture() {
        return texture;
    }
}
