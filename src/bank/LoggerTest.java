package bank;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LoggerTest {

    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = new Logger();
    }

    @Test
    void testLogInfoAndError() throws Exception {
        logger.logInfo("Test INFO message");
        logger.logError("Test ERROR message");

        Path p = Path.of("bank.log");
        List<String> lines = Files.readAllLines(p);

        assertTrue(lines.get(lines.size() - 2).contains("INFO"));
        assertTrue(lines.get(lines.size() - 2).contains("Test INFO message"));
        assertTrue(lines.get(lines.size() - 1).contains("ERROR"));
        assertTrue(lines.get(lines.size() - 1).contains("Test ERROR message"));
    }
}
