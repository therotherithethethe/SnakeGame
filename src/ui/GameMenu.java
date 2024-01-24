package ui;

import bl.GameLogic;
import dal.Cell;
import dal.Direction;
import dal.Grid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingReader;

public class GameMenu {
    private Cell snakeHead;
    private Grid grid;
    private GameLogic logic;
    private NonBlockingReader reader;
    private Terminal terminal;
    private Direction currentDirection = Direction.RIGHT;
    private boolean isGameSuspended = false;

    int cellCounter;

    public GameMenu(GameLogic logic) throws IOException {
        terminal = TerminalBuilder.builder()
            .dumb(true)
            .encoding(StandardCharsets.UTF_8)
            .build();
        reader = terminal.reader();
        grid = logic.getGrid();
        this.logic = logic;
        cellCounter = 1;
    }

    public void displayMenu() throws Exception {
        while (true) {
            clearScreen();
            printOptions();
            handleUserInput();
        }
    }
    private void printOptions() {
        String gameMenuText = "1. Play\n"
            + "2. Continue\n"
            + "3. Records\n"
            + "ESC. Logout";
        System.out.print(getCenteredText(gameMenuText));

    }
    private void clearScreen() {
        terminal.puts(InfoCmp.Capability.clear_screen);
    }
    private void handleUserInput() throws Exception {
        char key = (char) terminal.input().read();
        switch (key) {
            case '1' -> startGame();
            case 27 -> System.exit(0);
        }
    }
    private void startGame() throws Exception {
        Grid gridBuffer = new Grid(grid.getXLength(), grid.getYLength());
        Cell snakeHead = new Cell(gridBuffer);
        GameLogic logicBuffer = new GameLogic(gridBuffer, snakeHead);

        gameLoop();
        printGameOverMessage();

        cellCounter = 1;
        this.logic = logicBuffer;
        this.snakeHead = snakeHead;
        this.grid = gridBuffer;
        currentDirection = Direction.RIGHT;
    }

    private void gameLoop() throws InterruptedException, IOException {
        while (!logic.isGameLose() && !logic.isGameWon()) {
            cellCounter = logic.getSnakeCells().length;
            printCurrentGameStage();
            Thread.sleep(400);
            setDirectionByKey();
            logic.updateGameTable(currentDirection);

            if(isGameSuspended) {
                isGameSuspended = false;
                break;
            }
        }
    }

    private void printGameOverMessage() throws InterruptedException, IOException {
        clearScreen();
        if (logic.isGameWon()) {
            System.out.println(getCenteredText("YOU ARE WINNER. press any key"));
        } else if (logic.isGameLose()) {
            System.out.println(getCenteredText("YOU ARE LOOSER. press any key"));
        }
        Thread.sleep(1500);
        terminal.input().read();
    }

    public void printCurrentGameStage()
    {
        clearScreen();
        StringBuilder txt = new StringBuilder();
        for (int y = 0; y < grid.getYLength(); y++)
        {
            txt.append("\n");
            for (int x = 0; x < grid.getXLength(); x++)
            {
                txt.append(grid.getTable()[y][x]);
            }
        }
        txt.append("\n");
        txt.append("\n" + cellCounter);

        System.out.println(getCenteredText(txt.toString()));
    }
    private String getCenteredText(String text) {
        int consoleWidth = terminal.getWidth();
        int consoleHeight = terminal.getHeight();

        String[] lines = text.split("\n");
        int maxLineLength = 0;
        for (String line : lines) {
            if (line.length() > maxLineLength)
                maxLineLength = line.length();
        }

        int startX = (consoleWidth - maxLineLength) / 2;
        int startY = (consoleHeight - lines.length) / 2;

        if (startX < 0) startX = 0;
        if (startY < 0) startY = 0;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < startY; i++) {
            sb.append("\n");
        }

        for (String line : lines) {
            sb.append(" ".repeat(Math.max(0, startX)));
            sb.append(line);
            sb.append("\n");
        }

        return sb.toString();
    }
    private void setDirectionByKey() throws IOException {
        if (reader.ready()) {
            int code = reader.read();
            if (code != -1) {
                char inputChar = (char) code;
                switch (inputChar) {
                    case 'w':
                        currentDirection =  Direction.UP;
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
                    case (char)27:
                        isGameSuspended = true;
                }
            }
        }
    }
}