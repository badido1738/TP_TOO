package bank;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final String LOG_FILE = "bank.log";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void logInfo(String msg) {
        log("INFO", msg);
    }

    public void logError(String msg) {
        log("ERROR", msg);
    }

    private void log(String level, String msg) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String line = String.format("[%s] %s - %s", timestamp, level, msg);

        try (PrintWriter out = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            out.println(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
