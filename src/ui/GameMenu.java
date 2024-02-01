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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingReader;

/**
 * Manages the game's user interface, including displaying menus, handling user input, and managing account login and registration.
 * This class interacts with the terminal for input/output operations and maintains the state of the game and current user.
 */
public class GameMenu {
    // Scanner for reading user input
    private Scanner scanner = new Scanner(System.in);
    // The grid of the game
    private Grid grid;
    // The logic handler of the game
    private GameLogic logic;
    // Reader for non-blocking input
    private NonBlockingReader reader;
    // Terminal interface for user interaction
    private Terminal terminal;
    // The current direction of the snake
    private Direction currentDirection = Direction.RIGHT;
    // Flag indicating if the game is suspended
    private boolean isGameSuspended = false;
    // Service for managing accounts
    private AccountService accService = AccountService.getInstance();
    // The currently logged-in account
    private Account logedAccount;
    // The speed of the game
    private int gameSpeed;
    // Counter for the cells
    int cellCounter;

    /**
     * Constructor for creating a new GameMenu.
     * Initializes the terminal, reader, and game logic components.
     *
     * @param logic The game logic to be used.
     * @throws IOException If an I/O error occurs while initializing the terminal or reader.
     */
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

    /**
     * Displays the main menu and handles user navigation through the menu options.
     *
     * @throws Exception If an error occurs during menu display or handling user input.
     */
    public void displayMenu() throws Exception { //

        clearScreen();
        loginSystem();
        while (true) {
            clearScreen();
            printOptions();
            handleUserInput();
        }
    }
    /**
     * Displays the main menu and handles user navigation through the menu options.
     */
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

    /**
     * Manages the system for user login, including user authentication and handling of new or existing user accounts.
     * Ensures secure and user-friendly login interactions.
     *
     * @throws IOException If an I/O error occurs during the login process, ensuring that any issues with user input or account verification are properly handled.
     */
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
    /**
     * Handles the login process for existing users, verifying user credentials and managing user session initiation.
     *
     * @param username The username of the existing user attempting to log in.
     * @throws IOException If an I/O error occurs during the login process for the existing user, ensuring robust handling of input and authentication errors.
     */
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
    /**
     * Handles the registration and login process for new users, guiding them through account creation and initialization.
     *
     * @param username The chosen username for the new user account.
     * @throws IOException If an I/O error occurs during the account creation process for the new user, ensuring a smooth and error-free account setup.
     */
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

    /**
     * Displays a message indicating that the provided username is invalid, guiding the user to correct the input.
     *
     * @throws IOException If an I/O error occurs while displaying the invalid username message, ensuring the message is properly presented to the user.
     */
    private void displayInvalidUsernameMessage() throws IOException {
        System.out.println("Invalid username.");
        reader.read();
    }
    /**
     * Displays a message indicating that the provided password is incorrect for the existing account, prompting the user for the correct password.
     *
     * @throws IOException If an I/O error occurs while displaying the incorrect password message, ensuring the message is properly communicated to the user.
     */
    private void displayIncorrectPasswordMessage() throws IOException {
        clearScreen();
        System.out.print(getCenteredText("Your password isn't correct. Provide the correct password for the current user:"));
        terminal.input().read();
    }

    /**
     * Displays a message indicating that the provided password for the new account is invalid, guiding the user to input a valid password.
     *
     * @throws IOException If an I/O error occurs while displaying the invalid password message, ensuring the message is effectively conveyed to the user.
     */
    private void displayInvalidPasswordMessage() throws IOException {
        clearScreen();
        System.out.println(getCenteredText("Password invalid. Please input another password:"));
        terminal.input().read();
    }
    /**
     * Prints the main game options available to the user, including game start, viewing records, account management, and session termination.
     * Formats and displays these options in a user-friendly manner, ensuring clear navigation through the game's features.
     */
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
    /**
     * Clears the screen in the terminal to provide a clean slate for displaying new information or game stages.
     */
    private void clearScreen() {
        terminal.puts(InfoCmp.Capability.clear_screen);
    }
    /**
     * Handles the user's input within the main game menu, responding to menu selection and facilitating transitions
     * between different game states and functionalities such as playing the game, viewing records, and managing the account.
     *
     * @throws Exception If an error occurs while processing the user input, ensuring robust handling of user interactions.
     */
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
    /**
     * Facilitates the deletion of the currently logged-in user's account, ensuring a secure and confirmative process for account removal.
     *
     * @throws Exception If an error occurs during the account deletion process, guaranteeing careful handling of user data and account termination.
     */
    private void deleteAccount() throws Exception {
        clearScreen();
        accService.deleteAccount(logedAccount);
        logedAccount = null;
        System.out.println(getCenteredText("Account successfully deleted"));
        terminal.input().read();
        loginSystem();
    }
    /**
     * Displays a list of all game records across all users, presenting a competitive leaderboard of high scores.
     *
     * @throws IOException If an I/O error occurs while gathering or displaying the records, ensuring accurate and efficient data presentation.
     */
    private void showAllRecords() throws IOException {
        //Map<Integer, String> userRecords = new TreeMap<>(Collections.reverseOrder());
        Map<String, Integer> userRecords = new TreeMap<>(Collections.reverseOrder());

        for (Account account : accService.getAccounts()) {
            for (int record : account.getRecords()) {
                userRecords.put(account.getUserName(), record);
            }
        }

        LinkedHashMap<String, Integer> sortedRecordsMap = new LinkedHashMap<>();
        userRecords.entrySet()
            .stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEachOrdered(x -> sortedRecordsMap.put(x.getKey(), x.getValue()));

        int i = 1;
        StringBuilder sb = new StringBuilder();
        for (String username : sortedRecordsMap.keySet()) {
            sb.append("\n" + i + ". " + sortedRecordsMap.get(username) + " — " + username);
            i++;
        }

        clearScreen();
        System.out.println(getCenteredText(sb.toString()));
        terminal.input().read();
    }
    /**
     * Displays the current user's game records, providing a personal view of their game history and achievements.
     *
     * @throws IOException If an I/O error occurs while gathering or displaying the records, ensuring a personalized and error-free presentation of user data.
     */
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
    /**
     * Initiates the game, setting up the game environment according to user preferences and starting the main game loop.
     *
     * @throws Exception If an error occurs during the game initialization or execution, ensuring a smooth and error-free game experience.
     */
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

    /**
     * Sets the user's preferences for the game, including grid dimensions and game speed, providing a customized game experience.
     *
     * @throws Exception If an error occurs while setting the preferences, ensuring user inputs are properly captured and validated.
     */
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

    /**
     * Reads an integer value from the user, ensuring numeric input for game settings and configurations.
     *
     * @return The integer value input by the user or -1 if the input is not a valid integer, ensuring robust input handling.
     */
    private int readIntegerFromUser() {
        if (!scanner.hasNextInt()) {
            scanner.nextLine();
            return -1;
        }
        int number = scanner.nextInt();
        scanner.nextLine();
        return number;
    }
    /**
     * Manages the main game loop, handling game updates, user input, and game state transitions to provide a dynamic and responsive gaming experience.
     *
     * @throws Exception If an error occurs during the game loop, ensuring smooth and uninterrupted game play.
     */
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
    /**
     * Displays a game over message indicating the outcome of the game (win or lose), providing closure and feedback on the player's performance.
     *
     * @throws InterruptedException If the thread is interrupted while pausing before displaying the message, ensuring a smooth transition and message display.
     * @throws IOException If an I/O error occurs while displaying the game over message, guaranteeing the message is properly presented to the player.
     */
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
    /**
     * Prints the current stage of the game, displaying the game grid and any relevant game information to the player, ensuring a real-time view of the game progress.
     */
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
    /**
     * Formats and centers a given text string within the terminal window, ensuring an aesthetically pleasing and readable display of textual information.
     *
     * @param text The text to be centered.
     * @return A string formatted to be centered within the terminal window.
     */
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
    /**
     * Sets the direction of the snake based on the player's keyboard input, ensuring responsive and intuitive control over the game character.
     *
     * @throws IOException If an I/O error occurs while reading the user input, guaranteeing smooth and responsive gameplay.
     */
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