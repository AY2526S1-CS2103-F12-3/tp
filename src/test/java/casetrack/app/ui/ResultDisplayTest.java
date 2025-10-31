package casetrack.app.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * Contains tests for ResultDisplay.
 * Tests use JavaFX Platform to properly test UI components.
 */
public class ResultDisplayTest {

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
    public void constructor_success() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();
            assertNotNull(resultDisplay);
        });
    }

    @Test
    public void setFeedbackToUser_validText_setsTextArea() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();
            String feedback = "Command executed successfully";

            resultDisplay.setFeedbackToUser(feedback);

            TextArea textArea = getResultDisplayTextArea(resultDisplay);
            assertEquals(feedback, textArea.getText());
        });
    }

    @Test
    public void setFeedbackToUser_emptyString_setsEmptyText() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();

            resultDisplay.setFeedbackToUser("");

            TextArea textArea = getResultDisplayTextArea(resultDisplay);
            assertEquals("", textArea.getText());
        });
    }

    @Test
    public void setFeedbackToUser_longText_setsCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();
            String longFeedback = "This is a very long feedback message that contains multiple "
                    + "sentences and should be displayed correctly in the result display area. "
                    + "It tests whether the component can handle longer text without issues.";

            resultDisplay.setFeedbackToUser(longFeedback);

            TextArea textArea = getResultDisplayTextArea(resultDisplay);
            assertEquals(longFeedback, textArea.getText());
        });
    }

    @Test
    public void setFeedbackToUser_multilineText_preservesNewlines() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();
            String multilineFeedback = "Line 1\nLine 2\nLine 3";

            resultDisplay.setFeedbackToUser(multilineFeedback);

            TextArea textArea = getResultDisplayTextArea(resultDisplay);
            assertEquals(multilineFeedback, textArea.getText());
        });
    }

    @Test
    public void setFeedbackToUser_specialCharacters_displaysCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();
            String specialFeedback = "Special characters: !@#$%^&*(){}[]<>?";

            resultDisplay.setFeedbackToUser(specialFeedback);

            TextArea textArea = getResultDisplayTextArea(resultDisplay);
            assertEquals(specialFeedback, textArea.getText());
        });
    }

    @Test
    public void setFeedbackToUser_unicodeCharacters_displaysCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();
            String unicodeFeedback = "Unicode test: 你好 世界 こんにちは 안녕하세요";

            resultDisplay.setFeedbackToUser(unicodeFeedback);

            TextArea textArea = getResultDisplayTextArea(resultDisplay);
            assertEquals(unicodeFeedback, textArea.getText());
        });
    }

    @Test
    public void setFeedbackToUser_multipleCalls_updatesText() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();
            TextArea textArea = getResultDisplayTextArea(resultDisplay);

            resultDisplay.setFeedbackToUser("First message");
            assertEquals("First message", textArea.getText());

            resultDisplay.setFeedbackToUser("Second message");
            assertEquals("Second message", textArea.getText());

            resultDisplay.setFeedbackToUser("Third message");
            assertEquals("Third message", textArea.getText());
        });
    }

    @Test
    public void setFeedbackToUser_overwritesPreviousText() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();
            TextArea textArea = getResultDisplayTextArea(resultDisplay);

            resultDisplay.setFeedbackToUser("Old message");
            resultDisplay.setFeedbackToUser("New message");

            assertEquals("New message", textArea.getText(),
                    "New message should overwrite old message");
        });
    }

    @Test
    public void setFeedbackToUser_errorMessage_displaysCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();
            String errorMessage = "Error: Invalid command format";

            resultDisplay.setFeedbackToUser(errorMessage);

            TextArea textArea = getResultDisplayTextArea(resultDisplay);
            assertEquals(errorMessage, textArea.getText());
        });
    }

    @Test
    public void setFeedbackToUser_successMessage_displaysCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();
            String successMessage = "New person added: John Doe";

            resultDisplay.setFeedbackToUser(successMessage);

            TextArea textArea = getResultDisplayTextArea(resultDisplay);
            assertEquals(successMessage, textArea.getText());
        });
    }

    @Test
    public void setFeedbackToUser_whitespace_preserved() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay resultDisplay = new ResultDisplay();
            String whitespaceText = "  Text with spaces  ";

            resultDisplay.setFeedbackToUser(whitespaceText);

            TextArea textArea = getResultDisplayTextArea(resultDisplay);
            assertEquals(whitespaceText, textArea.getText());
        });
    }

    @Test
    public void multipleResultDisplays_independent() throws Exception {
        runOnJavaFxThread(() -> {
            ResultDisplay display1 = new ResultDisplay();
            ResultDisplay display2 = new ResultDisplay();

            display1.setFeedbackToUser("Message 1");
            display2.setFeedbackToUser("Message 2");

            TextArea textArea1 = getResultDisplayTextArea(display1);
            TextArea textArea2 = getResultDisplayTextArea(display2);

            assertEquals("Message 1", textArea1.getText());
            assertEquals("Message 2", textArea2.getText());
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
     * Gets the result display text area using reflection.
     */
    private TextArea getResultDisplayTextArea(ResultDisplay resultDisplay) {
        try {
            java.lang.reflect.Field field = ResultDisplay.class.getDeclaredField("resultDisplay");
            field.setAccessible(true);
            return (TextArea) field.get(resultDisplay);
        } catch (Exception e) {
            fail("Failed to get resultDisplay TextArea: " + e.getMessage());
            return null;
        }
    }
}
