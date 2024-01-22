package dal;

public enum Direction {
    UP,
    DOWN,
    RIGHT,
    LEFT;
    public static char getDirectionByString(Direction direction) { //maybe directory would be better?
        switch (direction) {
            case UP -> {
                return (char) 210;
            }
            case DOWN -> {
                return (char) 115;
            }
            case RIGHT -> {
                return (char) 100;
            }
            case LEFT -> {
                return (char) 97;
            }
        }
        return 0;
    }
}
