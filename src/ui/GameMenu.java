package ui;

import bl.AccountService;
import bl.GameLogic;
import dal.Account;
import dal.Cell;
import dal.Direction;
import dal.Grid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingReader;

public class GameMenu {
    private Scanner scanner = new Scanner(System.in);
    private Grid grid;
    private GameLogic logic;
    private NonBlockingReader reader;
    private Terminal terminal;
    private Direction currentDirection = Direction.RIGHT;
    private boolean isGameSuspended = false;
    private AccountService accService = AccountService.getInstance();
    private Account logedAccount;
    private int gameSpeed;
    int cellCounter;

    public GameMenu(GameLogic logic) throws IOException { //
        terminal = TerminalBuilder.builder()
            .dumb(true)
            .encoding(StandardCharsets.UTF_8)
            .build();
        reader = terminal.reader();
        grid = logic.getGrid();
        this.logic = logic;
        cellCounter = 1;
    }

    public void displayMenu() throws Exception { //

        clearScreen();
        loginSystem();
        while (true) {
            clearScreen();
            printOptions();
            handleUserInput();
        }
    }
    private void handleLoggedUser() {
        for(Account account : accService.getAccounts()) {
            if(account.isDefault) {
               if(account.entersCount % 6 == 5) {
                   account.entersCount = 0;
                   return;
               }
               else {
                   account.entersCount++;
                   logedAccount = account;
                   accService.arrayListToJsonFile();
                   return;
               }
            }
        }
    }
    private void loginSystem() throws IOException {
        handleLoggedUser();
        while (logedAccount == null) {
            clearScreen();
            System.out.print(getCenteredText("Provide your username:"));
            String usernameInput = scanner.nextLine();

            if (!Account.isUsernameValid(usernameInput)) {
                displayInvalidUsernameMessage();
                continue;
            }

            if (accService.isUsernameExistInDb(usernameInput)) {
                handleExistingUser(usernameInput);
            } else {
                handleNewUser(usernameInput);
            }
        }

    }

    private void handleExistingUser(String username) throws IOException {
        clearScreen();
        System.out.print(getCenteredText("Provide password for current account:"));
        String passwordInput = scanner.nextLine();

        if (!accService.isPassCorrectForCurrentUser(username, passwordInput)) {
            displayIncorrectPasswordMessage();
            return;
        }

        logedAccount = accService.getAccountByUserName(username);
        logedAccount.isDefault = true;
        clearScreen();
    }

    private void handleNewUser(String username) throws IOException {
        clearScreen();
        System.out.println(getCenteredText("Provide password for new user:"));
        String passwordInput = scanner.nextLine();

        if (!Account.isPassValid(passwordInput)) {
            displayInvalidPasswordMessage();
            return;
        }

        logedAccount = new Account(username, passwordInput);
        logedAccount.isDefault = true;
        accService.addAccount(logedAccount);
        accService.arrayListToJsonFile();
        clearScreen();
        System.out.println(getCenteredText("Thanks for registering. Click any button."));
        terminal.input().read();
        clearScreen();
    }

    private void displayInvalidUsernameMessage() throws IOException {
        System.out.println("Invalid username.");
        reader.read();
    }

    private void displayIncorrectPasswordMessage() throws IOException {
        clearScreen();
        System.out.print(getCenteredText("Your password isn't correct. Provide the correct password for the current user:"));
        terminal.input().read();
    }

    private void displayInvalidPasswordMessage() throws IOException {
        clearScreen();
        System.out.println(getCenteredText("Password invalid. Please input another password:"));
        terminal.input().read();
    }
    private void printOptions() { //
        String gameMenuText = String.format("""
            %s
            1. Play
            2. Your records
            3. World records
            4. Delete account
            ESC. Exit
            Enter. Logout""", logedAccount.getUserName());

        System.out.print(getCenteredText(gameMenuText));

    }
    private void clearScreen() {
        terminal.puts(InfoCmp.Capability.clear_screen);
    } //
    private void handleUserInput() throws Exception {
        accService.arrayListToJsonFile();
        char key = (char) terminal.input().read();
        switch (key) {
            case '1' -> startGame();
            case '2' -> showCurrentAccRecords();
            case '3' -> showAllRecords();
            case '4' -> deleteAccount();
            case '\r' ->  {
                logedAccount.isDefault = false;
                logedAccount.entersCount = 0;
                accService.arrayListToJsonFile();
                logedAccount = null;
                loginSystem();
            }
            case 27 -> System.exit(0);
        }
    }
    private void deleteAccount() throws Exception {
        clearScreen();
        accService.deleteAccount(logedAccount);
        logedAccount = null;
        System.out.println(getCenteredText("Account successfully deleted"));
        terminal.input().read();
        loginSystem();
    }
    private void showAllRecords() throws IOException {
        Map<Integer, String> userRecords = new TreeMap<>(Collections.reverseOrder());

        for (Account account : accService.getAccounts()) {
            for (int record : account.getRecords()) {
                userRecords.put(record, account.getUserName());
            }
        }

        int i = 1;
        StringBuilder sb = new StringBuilder();
        for (int key : userRecords.keySet()) {
            sb.append("\n" + i + ". " + key + " — " + userRecords.get(key));
            i++;
        }

        clearScreen();
        System.out.println(getCenteredText(sb.toString()));
        terminal.input().read();
    }
    private void showCurrentAccRecords() throws IOException {
        clearScreen();
        StringBuilder recordslist = new StringBuilder(
            String.format("%s records:", logedAccount.getUserName()));

        for(int record : logedAccount.getRecords().reversed()) {
            recordslist.append("\n" + record + ",");
        }
        recordslist.deleteCharAt(recordslist.length() - 1);
        System.out.println(getCenteredText(recordslist.toString()));
        terminal.input().read();
    }
    private void startGame() throws Exception {
        setPreference();
        Grid gridBuffer = new Grid(grid.getXLength(), grid.getYLength());
        Cell snakeHead = new Cell(gridBuffer);
        GameLogic logicBuffer = new GameLogic(gridBuffer, snakeHead);
        logedAccount.runCount++;

        gameLoop();
        printGameOverMessage();

        cellCounter = 1;
        this.logic = logicBuffer;
        this.grid = gridBuffer;
        currentDirection = Direction.RIGHT;
    }

    private void setPreference() throws Exception {
        clearScreen();
        try {
            System.out.print(getCenteredText("provide width length for grid:"));
            int width = readIntegerFromUser();
            if (width == -1)
                throw new InputMismatchException();

            clearScreen();
            System.out.println(getCenteredText("provide height for grid:"));
            int height = readIntegerFromUser();
            if (height == -1)
                throw new InputMismatchException();

            clearScreen();
            System.out.println(getCenteredText("provide game speed (game updates per second):"));
            int speed = readIntegerFromUser();
            if (speed == -1)
                throw new InputMismatchException();

            gameSpeed = speed;
            grid.setxLength(width);
            grid.setyLength(height);
            logic.setGrid(grid);
            clearScreen();
        } catch (Exception ex) {
            clearScreen();
            System.out.println(getCenteredText("oops. your input is wrong. Check provided values. Returning.."));
            terminal.input().read();
            displayMenu();
        }
    }

    private int readIntegerFromUser() {
        if (!scanner.hasNextInt()) {
            scanner.nextLine();
            return -1;
        }
        int number = scanner.nextInt();
        scanner.nextLine();
        return number;
    }

    private void gameLoop() throws Exception {
        while (!logic.isGameLose() && !logic.isGameWon() && !isGameSuspended) {
            cellCounter = logic.getSnakeCells().length;
            printCurrentGameStage();
            Thread.sleep((long) ((float)1/gameSpeed * 1000)); //1000ms in 1 second
            setDirectionByKey();
            logic.updateGameTable(currentDirection);
        }
        if(isGameSuspended) {
            isGameSuspended = false;
            clearScreen();
            System.out.println(getCenteredText("Exiting to menu...."));
            terminal.input().read();
            displayMenu();
        }

    }

    private void printGameOverMessage() throws InterruptedException, IOException {
        clearScreen();
        if (logic.isGameWon()) {
            logedAccount.addRecord(cellCounter);
            System.out.println(getCenteredText("YOU ARE WINNER. Victory. Press any key"));
        } else if (logic.isGameLose()) {
            System.out.println(getCenteredText("YOU ARE LOOSER. Game is over. Press any key"));
        }
        Thread.sleep(1500);
        terminal.input().read();
    }

    private void printCurrentGameStage()
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
        txt.append("\n" + cellCounter);

        System.out.println(getCenteredText(txt.toString()));
    }
    private String getCenteredText(String text) { //
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
                    case 27:
                        isGameSuspended = true;
                }
            }
        }
    }

}