package casetrack.app.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * Contains tests for StatusBarFooter.
 * Tests use JavaFX Platform to properly test UI components.
 */
public class StatusBarFooterTest {

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
    public void constructor_validPath_success() throws Exception {
        runOnJavaFxThread(() -> {
            Path savePath = Paths.get("data", "addressbook.json");
            StatusBarFooter statusBarFooter = new StatusBarFooter(savePath);
            assertNotNull(statusBarFooter);
        });
    }

    @Test
    public void constructor_setsSaveLocationLabel() throws Exception {
        runOnJavaFxThread(() -> {
            Path savePath = Paths.get("data", "addressbook.json");
            StatusBarFooter statusBarFooter = new StatusBarFooter(savePath);

            Label saveLocationLabel = getSaveLocationLabel(statusBarFooter);
            assertNotNull(saveLocationLabel.getText());
        });
    }

    @Test
    public void constructor_resolvesRelativePath() throws Exception {
        runOnJavaFxThread(() -> {
            Path savePath = Paths.get("data", "addressbook.json");
            StatusBarFooter statusBarFooter = new StatusBarFooter(savePath);

            Label saveLocationLabel = getSaveLocationLabel(statusBarFooter);
            String expectedPath = Paths.get(".").resolve(savePath).toString();
            assertEquals(expectedPath, saveLocationLabel.getText());
        });
    }

    @Test
    public void constructor_simplePath_displaysCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            Path savePath = Paths.get("addressbook.json");
            StatusBarFooter statusBarFooter = new StatusBarFooter(savePath);

            Label saveLocationLabel = getSaveLocationLabel(statusBarFooter);
            String expectedPath = Paths.get(".").resolve(savePath).toString();
            assertEquals(expectedPath, saveLocationLabel.getText());
        });
    }

    @Test
    public void constructor_nestedPath_displaysCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            Path savePath = Paths.get("data", "subfolder", "addressbook.json");
            StatusBarFooter statusBarFooter = new StatusBarFooter(savePath);

            Label saveLocationLabel = getSaveLocationLabel(statusBarFooter);
            String expectedPath = Paths.get(".").resolve(savePath).toString();
            assertEquals(expectedPath, saveLocationLabel.getText());
        });
    }

    @Test
    public void constructor_absolutePath_displaysCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            Path savePath = Paths.get("/", "absolute", "path", "addressbook.json");
            StatusBarFooter statusBarFooter = new StatusBarFooter(savePath);

            Label saveLocationLabel = getSaveLocationLabel(statusBarFooter);
            // Absolute path should be resolved from current directory
            String expectedPath = Paths.get(".").resolve(savePath).toString();
            assertEquals(expectedPath, saveLocationLabel.getText());
        });
    }

    @Test
    public void constructor_pathWithSpaces_displaysCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            Path savePath = Paths.get("data folder", "my addressbook.json");
            StatusBarFooter statusBarFooter = new StatusBarFooter(savePath);

            Label saveLocationLabel = getSaveLocationLabel(statusBarFooter);
            assertTrue(saveLocationLabel.getText().contains("data folder"));
            assertTrue(saveLocationLabel.getText().contains("my addressbook.json"));
        });
    }

    @Test
    public void constructor_differentPaths_independent() throws Exception {
        runOnJavaFxThread(() -> {
            Path path1 = Paths.get("data", "file1.json");
            Path path2 = Paths.get("backup", "file2.json");

            StatusBarFooter footer1 = new StatusBarFooter(path1);
            StatusBarFooter footer2 = new StatusBarFooter(path2);

            Label label1 = getSaveLocationLabel(footer1);
            Label label2 = getSaveLocationLabel(footer2);

            String expected1 = Paths.get(".").resolve(path1).toString();
            String expected2 = Paths.get(".").resolve(path2).toString();

            assertEquals(expected1, label1.getText());
            assertEquals(expected2, label2.getText());
        });
    }

    @Test
    public void constructor_currentDirectoryPath_displaysCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            Path savePath = Paths.get(".");
            StatusBarFooter statusBarFooter = new StatusBarFooter(savePath);

            Label saveLocationLabel = getSaveLocationLabel(statusBarFooter);
            assertNotNull(saveLocationLabel.getText());
        });
    }

    @Test
    public void constructor_pathWithSpecialCharacters_displaysCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            Path savePath = Paths.get("data-2024", "addressbook_v1.json");
            StatusBarFooter statusBarFooter = new StatusBarFooter(savePath);

            Label saveLocationLabel = getSaveLocationLabel(statusBarFooter);
            assertTrue(saveLocationLabel.getText().contains("data-2024"));
            assertTrue(saveLocationLabel.getText().contains("addressbook_v1.json"));
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
     * Gets the save location label using reflection.
     */
    private Label getSaveLocationLabel(StatusBarFooter statusBarFooter) {
        try {
            java.lang.reflect.Field field = StatusBarFooter.class.getDeclaredField("saveLocationStatus");
            field.setAccessible(true);
            return (Label) field.get(statusBarFooter);
        } catch (Exception e) {
            fail("Failed to get saveLocationStatus label: " + e.getMessage());
            return null;
        }
    }
}
