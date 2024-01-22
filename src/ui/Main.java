package ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.InfoCmp;

public class Main {

    public static void main(String[] args) throws IOException {
        java.util.logging.Logger.getLogger("org.jline").setLevel(Level.FINEST);

        Terminal terminal = TerminalBuilder.builder()
            .dumb(true)
            .build();

        InputStream inputStream = terminal.input();
        char key = (char) inputStream.read();


        switch (key) {
            case (char)209:
            case (char)210: //up
                System.out.println("mama up");
                break;
            case (char)208:
            case (char)100:
                System.out.println("mama right");
        }


        //terminal.puts(InfoCmp.Capability.clear_screen);

        terminal.close();
    }
}