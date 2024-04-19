package org.LightweightDBMS.files.tables;

import org.LightweightDBMS.Commands.Create;
import org.LightweightDBMS.Logs;
import org.LightweightDBMS.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateTest {
    private Create create;
    private User user;
    private final ByteArrayOutputStream consoleOut = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;



    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(consoleOut));
        user = new User("1", "testUser", "testPassword");

        // Initialize the Logs for each test
        Logs.startLogs(false);

        create = new Create(user, "table TestTable (column1 int, column2 varchar);");
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);  // Restore the original System.out
        Logs.deleteLog();  // Clean up the log file after each test
        // Ensure test table file is also cleaned up if created
        File tableFile = new File("src/main/java/org/LightweightDBMS/files/tables/TestTable.txt");
        if (tableFile.exists()) {
            tableFile.delete();
        }
    }

    @Test
    public void testCreateContinue() {
        create.createContinue();
        String expectedOutput = "->Table TestTable Created\n";
        assertEquals(expectedOutput.trim(), consoleOut.toString().trim());

        // Check if the table was created by checking file existence
        File tableFile = new File("src/main/java/org/LightweightDBMS/files/tables/TestTable.txt");
        assertTrue(tableFile.exists());
    }

    @Test
    public void testCreateExistingTable() {
        create.createContinue(); // First creation
        consoleOut.reset(); // Clear the output for the second call
        create.createContinue(); // Attempt to create the same table again
        String expectedOutput = "->Table TestTable already exists" + System.lineSeparator();
        assertEquals(expectedOutput, consoleOut.toString());
    }


    @Test
    public void testCreateSyntaxError() {
        create = new Create(user, "invalid_query;");
        create.createContinue();
        String expectedOutput = "->Syntax Error\n";
        assertEquals(expectedOutput.trim(), consoleOut.toString().trim());
    }

    @Test
    public void testCreateInvalidDataType() {
        create = new Create(user, "table InvalidTable (column1 unknown_type);");
        create.createContinue();
        String expectedOutput = "->Syntax Error, wrong datatype\n";
        assertEquals(expectedOutput.trim(), consoleOut.toString().trim());
    }
}
