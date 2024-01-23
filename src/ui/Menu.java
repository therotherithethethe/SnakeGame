package ui;

import bl.GameLogic;
import dal.Cell;
import dal.Grid;
import java.io.IOException;
import java.io.InputStream;
import org.jline.terminal.Terminal;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingReader;

public class Menu {
    private Cell snakeHead;
    private Grid grid;
    private GameLogic logic;

    private Terminal terminal;
    private NonBlockingReader reader;

    int cellCounter;

    public Menu(GameLogic logic) {
        terminal = logic.getTerminal();
        reader = logic.getReader();
        grid = logic.getGrid();
        snakeHead = logic.getSnakeCells()[0];
        this.logic = logic;
        cellCounter = 1;
    }

    public void Print() throws Exception {

        while (true) {
            Grid gridBuffer = new Grid(grid.getXLength(), grid.getYLength());
            Cell snakeHead = new Cell(gridBuffer);

            GameLogic logicBuffer = new GameLogic(gridBuffer, snakeHead);
            logicBuffer.setReader(logic.getReader());

            terminal.puts(InfoCmp.Capability.clear_screen);
            System.out.println(getCenteredText("1. play"));
            InputStream inputStream = terminal.input();

            char key = (char) inputStream.read();

            switch (key) {
                case '1':
                    while (!logic.isGameOver() && cellCounter != grid.getYLength() * grid.getXLength() - 1) {
                        cellCounter = logic.getSnakeCells().length;
                        printCurrentGameStage();
                        Thread.sleep(400);
                        logic.updateGameTable();
                    }

                    terminal.puts(InfoCmp.Capability.clear_screen);
                    if (logic.isGameOver()) {
                        System.out.println("YOU ARE LOOSER");
                    } else {
                        System.out.println("YOU ARE WINNER");
                    }
                    break;
                default:
                    break;
            }
            Thread.sleep(1500);
            inputStream.read();

            cellCounter = 1;
            this.logic = logicBuffer;
            this.snakeHead = snakeHead;
            this.grid = gridBuffer;
        }
    }

    public void printCurrentGameStage()
    {
        terminal.puts(InfoCmp.Capability.clear_screen);
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
}