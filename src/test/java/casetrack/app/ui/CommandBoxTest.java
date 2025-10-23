package casetrack.app.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import casetrack.app.logic.commands.CommandResult;
import casetrack.app.logic.commands.exceptions.CommandException;
import casetrack.app.logic.parser.exceptions.ParseException;
import javafx.application.Platform;
import javafx.scene.control.TextField;

/**
 * Contains tests for CommandBox.
 * Tests use JavaFX Platform to properly test UI components.
 */
public class CommandBoxTest {

    private static boolean jfxInitialized = false;

    @BeforeAll
    public static void initJfx() throws Exception {
        String osName = System.getProperty("os.name").toLowerCase();
        assumeFalse(osName.contains("linux"),
                "Skipping JavaFX tests on Ubuntu/Linux");

        if (!jfxInitialized) {
            CountDownLatch latch = new CountDownLatch(1);
            try {
                Platform.startup(latch::countDown);
            } catch (IllegalStateException | UnsupportedOperationException e) {
                latch.countDown();
            }
            latch.await(5, TimeUnit.SECONDS);
            jfxInitialized = true;
        }
    }

    @Test
    public void errorStyleClass_correctValue() {
        assertEquals("error", CommandBox.ERROR_STYLE_CLASS);
    }

    @Test
    public void constructor_validExecutor_success() throws Exception {
        runOnJavaFxThread(() -> {
            CommandBox.CommandExecutor executor = commandText -> new CommandResult("Success");
            CommandBox commandBox = new CommandBox(executor);
            assertNotNull(commandBox);
        });
    }

    @Test
    public void handleCommandEntered_successfulCommand_textFieldCleared() throws Exception {
        runOnJavaFxThread(() -> {
            boolean[] executed = {false};
            CommandBox.CommandExecutor executor = commandText -> {
                executed[0] = true;
                return new CommandResult("Success");
            };

            CommandBox commandBox = new CommandBox(executor);
            TextField textField = getCommandTextField(commandBox);
            textField.setText("test command");

            invokeHandleCommandEntered(commandBox);

            assertTrue(executed[0], "Command should be executed");
            assertEquals("", textField.getText(), "Text field should be cleared after successful execution");
        });
    }

    @Test
    public void handleCommandEntered_commandException_errorStyleApplied() throws Exception {
        runOnJavaFxThread(() -> {
            CommandBox.CommandExecutor executor = commandText -> {
                throw new CommandException("Test error");
            };

            CommandBox commandBox = new CommandBox(executor);
            TextField textField = getCommandTextField(commandBox);
            textField.setText("invalid command");

            invokeHandleCommandEntered(commandBox);

            assertTrue(textField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS),
                    "Error style should be applied after CommandException");
        });
    }

    @Test
    public void handleCommandEntered_parseException_errorStyleApplied() throws Exception {
        runOnJavaFxThread(() -> {
            CommandBox.CommandExecutor executor = commandText -> {
                throw new ParseException("Parse error");
            };

            CommandBox commandBox = new CommandBox(executor);
            TextField textField = getCommandTextField(commandBox);
            textField.setText("malformed command");

            invokeHandleCommandEntered(commandBox);

            assertTrue(textField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS),
                    "Error style should be applied after ParseException");
        });
    }

    @Test
    public void handleCommandEntered_emptyCommand_noExecution() throws Exception {
        runOnJavaFxThread(() -> {
            boolean[] executed = {false};
            CommandBox.CommandExecutor executor = commandText -> {
                executed[0] = true;
                return new CommandResult("Should not execute");
            };

            CommandBox commandBox = new CommandBox(executor);
            TextField textField = getCommandTextField(commandBox);
            textField.setText("");

            invokeHandleCommandEntered(commandBox);

            assertFalse(executed[0], "Empty command should not be executed");
        });
    }

    @Test
    public void setStyleToDefault_removesErrorStyle() throws Exception {
        runOnJavaFxThread(() -> {
            CommandBox.CommandExecutor executor = commandText -> {
                throw new CommandException("Error");
            };

            CommandBox commandBox = new CommandBox(executor);
            TextField textField = getCommandTextField(commandBox);
            textField.setText("error command");

            invokeHandleCommandEntered(commandBox);
            assertTrue(textField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS),
                    "Error style should be present");

            textField.setText("new text");

            assertFalse(textField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS),
                    "Error style should be removed when text changes");
        });
    }

    @Test
    public void setStyleToIndicateCommandFailure_noDuplicateErrorStyle() throws Exception {
        runOnJavaFxThread(() -> {
            CommandBox.CommandExecutor executor = commandText -> {
                throw new CommandException("Error");
            };

            CommandBox commandBox = new CommandBox(executor);
            TextField textField = getCommandTextField(commandBox);

            textField.setText("error1");
            invokeHandleCommandEntered(commandBox);

            textField.setText("error2");
            invokeHandleCommandEntered(commandBox);

            textField.setText("error3");
            invokeHandleCommandEntered(commandBox);

            long errorCount = textField.getStyleClass().stream()
                    .filter(s -> s.equals(CommandBox.ERROR_STYLE_CLASS))
                    .count();

            assertEquals(1, errorCount, "Error style should not be duplicated");
        });
    }

    @Test
    public void handleCommandEntered_commandTextPassedToExecutor() throws Exception {
        runOnJavaFxThread(() -> {
            String[] capturedCommand = {null};
            CommandBox.CommandExecutor executor = commandText -> {
                capturedCommand[0] = commandText;
                return new CommandResult("OK");
            };

            CommandBox commandBox = new CommandBox(executor);
            TextField textField = getCommandTextField(commandBox);
            String testCommand = "test command with parameters";
            textField.setText(testCommand);

            invokeHandleCommandEntered(commandBox);

            assertEquals(testCommand, capturedCommand[0],
                    "Command text should be passed correctly to executor");
        });
    }

    @Test
    public void textPropertyListener_resetsStyleOnAnyTextChange() throws Exception {
        runOnJavaFxThread(() -> {
            CommandBox.CommandExecutor executor = commandText -> {
                throw new CommandException("Error");
            };

            CommandBox commandBox = new CommandBox(executor);
            TextField textField = getCommandTextField(commandBox);

            // Add error style
            textField.setText("error");
            invokeHandleCommandEntered(commandBox);
            assertTrue(textField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));

            // Append text
            textField.setText(textField.getText() + "a");
            assertFalse(textField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS),
                    "Error style should be removed on text append");

            // Add error again
            invokeHandleCommandEntered(commandBox);
            assertTrue(textField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));

            // Delete text
            textField.setText("");
            assertFalse(textField.getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS),
                    "Error style should be removed on text deletion");
        });
    }

    /**
     * Runs a runnable on the JavaFX application thread and waits for completion.
     */
    private void runOnJavaFxThread(Runnable runnable) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Exception[] exception = {null};

        Platform.runLater(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                exception[0] = e;
            } finally {
                latch.countDown();
            }
        });

        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail("Test timed out waiting for JavaFX thread");
        }

        if (exception[0] != null) {
            throw exception[0];
        }
    }

    /**
     * Gets the command text field from CommandBox using reflection.
     */
    private TextField getCommandTextField(CommandBox commandBox) {
        try {
            java.lang.reflect.Field field = CommandBox.class.getDeclaredField("commandTextField");
            field.setAccessible(true);
            return (TextField) field.get(commandBox);
        } catch (Exception e) {
            fail("Failed to get commandTextField: " + e.getMessage());
            return null;
        }
    }

    /**
     * Invokes the handleCommandEntered method using reflection.
     */
    private void invokeHandleCommandEntered(CommandBox commandBox) {
        try {
            java.lang.reflect.Method method = CommandBox.class.getDeclaredMethod("handleCommandEntered");
            method.setAccessible(true);
            method.invoke(commandBox);
        } catch (Exception e) {
            fail("Failed to invoke handleCommandEntered: " + e.getMessage());
        }
    }
}
