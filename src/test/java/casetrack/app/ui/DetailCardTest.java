package casetrack.app.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import casetrack.app.model.person.PersonAttribute;
import javafx.application.Platform;
import javafx.scene.control.Label;

/**
 * Contains tests for DetailCard.
 * Tests use JavaFX Platform to properly test UI components.
 */
public class DetailCardTest {

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
    public void constructor_validInputs_success() throws Exception {
        runOnJavaFxThread(() -> {
            PersonAttribute attribute = new PersonAttribute("Name", "John Doe");
            DetailCard detailCard = new DetailCard(attribute, 1);
            assertNotNull(detailCard);
        });
    }

    @Test
    public void constructor_setsAttributeField() throws Exception {
        runOnJavaFxThread(() -> {
            PersonAttribute attribute = new PersonAttribute("Email", "test@example.com");
            DetailCard detailCard = new DetailCard(attribute, 1);
            assertEquals(attribute, detailCard.attribute);
        });
    }

    @Test
    public void constructor_setsTitleLabel() throws Exception {
        runOnJavaFxThread(() -> {
            PersonAttribute attribute = new PersonAttribute("Phone", "91234567");
            DetailCard detailCard = new DetailCard(attribute, 1);

            Label titleLabel = getTitleLabel(detailCard);
            assertEquals("Phone", titleLabel.getText());
        });
    }

    @Test
    public void constructor_setsValueLabel() throws Exception {
        runOnJavaFxThread(() -> {
            PersonAttribute attribute = new PersonAttribute("Address", "123 Main St");
            DetailCard detailCard = new DetailCard(attribute, 1);

            Label valueLabel = getValueLabel(detailCard);
            assertEquals("123 Main St", valueLabel.getText());
        });
    }

    @Test
    public void constructor_differentIndex_success() throws Exception {
        runOnJavaFxThread(() -> {
            PersonAttribute attribute = new PersonAttribute("Income", "$50000");

            DetailCard detailCard1 = new DetailCard(attribute, 0);
            assertNotNull(detailCard1);

            DetailCard detailCard2 = new DetailCard(attribute, 5);
            assertNotNull(detailCard2);

            DetailCard detailCard3 = new DetailCard(attribute, 100);
            assertNotNull(detailCard3);
        });
    }

    @Test
    public void constructor_emptyValue_success() throws Exception {
        runOnJavaFxThread(() -> {
            PersonAttribute attribute = new PersonAttribute("Notes", "");
            DetailCard detailCard = new DetailCard(attribute, 1);

            Label valueLabel = getValueLabel(detailCard);
            assertEquals("", valueLabel.getText());
        });
    }

    @Test
    public void constructor_longValues_success() throws Exception {
        runOnJavaFxThread(() -> {
            String longValue = "This is a very long value that might cause UI issues if not handled properly. "
                    + "It contains multiple sentences and should be displayed correctly in the label.";
            PersonAttribute attribute = new PersonAttribute("Description", longValue);
            DetailCard detailCard = new DetailCard(attribute, 1);

            Label valueLabel = getValueLabel(detailCard);
            assertEquals(longValue, valueLabel.getText());
        });
    }

    @Test
    public void constructor_specialCharacters_success() throws Exception {
        runOnJavaFxThread(() -> {
            PersonAttribute attribute = new PersonAttribute("Special", "Test!@#$%^&*(){}[]<>?/\\|");
            DetailCard detailCard = new DetailCard(attribute, 1);

            Label titleLabel = getTitleLabel(detailCard);
            Label valueLabel = getValueLabel(detailCard);

            assertEquals("Special", titleLabel.getText());
            assertEquals("Test!@#$%^&*(){}[]<>?/\\|", valueLabel.getText());
        });
    }

    @Test
    public void constructor_unicodeCharacters_success() throws Exception {
        runOnJavaFxThread(() -> {
            PersonAttribute attribute = new PersonAttribute("Language", "中文测试 日本語 한국어");
            DetailCard detailCard = new DetailCard(attribute, 1);

            Label valueLabel = getValueLabel(detailCard);
            assertEquals("中文测试 日本語 한국어", valueLabel.getText());
        });
    }

    @Test
    public void constructor_multipleAttributes_independentCards() throws Exception {
        runOnJavaFxThread(() -> {
            PersonAttribute attribute1 = new PersonAttribute("Name", "Alice");
            PersonAttribute attribute2 = new PersonAttribute("Email", "bob@test.com");
            PersonAttribute attribute3 = new PersonAttribute("Phone", "12345678");

            DetailCard card1 = new DetailCard(attribute1, 1);
            DetailCard card2 = new DetailCard(attribute2, 2);
            DetailCard card3 = new DetailCard(attribute3, 3);

            assertEquals("Name", getTitleLabel(card1).getText());
            assertEquals("Alice", getValueLabel(card1).getText());

            assertEquals("Email", getTitleLabel(card2).getText());
            assertEquals("bob@test.com", getValueLabel(card2).getText());

            assertEquals("Phone", getTitleLabel(card3).getText());
            assertEquals("12345678", getValueLabel(card3).getText());
        });
    }

    @Test
    public void constructor_whitespaceInValues_preserved() throws Exception {
        runOnJavaFxThread(() -> {
            PersonAttribute attribute = new PersonAttribute("Address", "  123 Main St  ");
            DetailCard detailCard = new DetailCard(attribute, 1);

            Label valueLabel = getValueLabel(detailCard);
            assertEquals("  123 Main St  ", valueLabel.getText());
        });
    }

    @Test
    public void constructor_newlineInValue_preserved() throws Exception {
        runOnJavaFxThread(() -> {
            PersonAttribute attribute = new PersonAttribute("Notes", "Line 1\nLine 2\nLine 3");
            DetailCard detailCard = new DetailCard(attribute, 1);

            Label valueLabel = getValueLabel(detailCard);
            assertEquals("Line 1\nLine 2\nLine 3", valueLabel.getText());
        });
    }

    @Test
    public void constructor_numericValues_displayedAsString() throws Exception {
        runOnJavaFxThread(() -> {
            PersonAttribute attribute = new PersonAttribute("Age", "25");
            DetailCard detailCard = new DetailCard(attribute, 1);

            Label valueLabel = getValueLabel(detailCard);
            assertEquals("25", valueLabel.getText());
        });
    }

    @Test
    public void constructor_negativeIndex_stillWorks() throws Exception {
        runOnJavaFxThread(() -> {
            PersonAttribute attribute = new PersonAttribute("Test", "Value");
            DetailCard detailCard = new DetailCard(attribute, -1);
            assertNotNull(detailCard);
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
     * Gets the title label from DetailCard using reflection.
     */
    private Label getTitleLabel(DetailCard detailCard) {
        try {
            java.lang.reflect.Field field = DetailCard.class.getDeclaredField("title");
            field.setAccessible(true);
            return (Label) field.get(detailCard);
        } catch (Exception e) {
            fail("Failed to get title label: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gets the value label from DetailCard using reflection.
     */
    private Label getValueLabel(DetailCard detailCard) {
        try {
            java.lang.reflect.Field field = DetailCard.class.getDeclaredField("value");
            field.setAccessible(true);
            return (Label) field.get(detailCard);
        } catch (Exception e) {
            fail("Failed to get value label: " + e.getMessage());
            return null;
        }
    }
}
