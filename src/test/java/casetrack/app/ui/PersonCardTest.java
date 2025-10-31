package casetrack.app.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import casetrack.app.model.person.Person;
import casetrack.app.testutil.PersonBuilder;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

/**
 * Contains tests for PersonCard.
 * Tests use JavaFX Platform to properly test UI components.
 */
public class PersonCardTest {

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
            Person person = new PersonBuilder().build();
            PersonCard personCard = new PersonCard(person, 1);
            assertNotNull(personCard);
        });
    }

    @Test
    public void constructor_setsPersonField() throws Exception {
        runOnJavaFxThread(() -> {
            Person person = new PersonBuilder().withName("John Doe").build();
            PersonCard personCard = new PersonCard(person, 1);
            assertEquals(person, personCard.person);
        });
    }

    @Test
    public void constructor_setsIdLabel() throws Exception {
        runOnJavaFxThread(() -> {
            Person person = new PersonBuilder().build();
            PersonCard personCard = new PersonCard(person, 5);

            Label idLabel = getIdLabel(personCard);
            assertEquals("5. ", idLabel.getText());
        });
    }

    @Test
    public void constructor_setsNameLabel() throws Exception {
        runOnJavaFxThread(() -> {
            Person person = new PersonBuilder().withName("Alice Wonder").build();
            PersonCard personCard = new PersonCard(person, 1);

            Label nameLabel = getNameLabel(personCard);
            assertEquals("Alice Wonder", nameLabel.getText());
        });
    }

    @Test
    public void constructor_setsPhoneLabel() throws Exception {
        runOnJavaFxThread(() -> {
            Person person = new PersonBuilder().withPhone("91234567").build();
            PersonCard personCard = new PersonCard(person, 1);

            Label phoneLabel = getPhoneLabel(personCard);
            assertEquals("91234567", phoneLabel.getText());
        });
    }

    @Test
    public void constructor_setsAddressLabel() throws Exception {
        runOnJavaFxThread(() -> {
            Person person = new PersonBuilder()
                    .withAddress("123 Test Street, #01-01")
                    .build();
            PersonCard personCard = new PersonCard(person, 1);

            Label addressLabel = getAddressLabel(personCard);
            assertEquals("123 Test Street, #01-01", addressLabel.getText());
        });
    }

    @Test
    public void constructor_noTags_emptyTagsPane() throws Exception {
        runOnJavaFxThread(() -> {
            Person person = new PersonBuilder().withTags().build();
            PersonCard personCard = new PersonCard(person, 1);

            FlowPane tagsPane = getTagsPane(personCard);
            assertEquals(0, tagsPane.getChildren().size(),
                    "Tags pane should be empty when person has no tags");
        });
    }

    @Test
    public void constructor_withTags_displaysAllTags() throws Exception {
        runOnJavaFxThread(() -> {
            Person person = new PersonBuilder()
                    .withTags("friend", "colleague")
                    .build();
            PersonCard personCard = new PersonCard(person, 1);

            FlowPane tagsPane = getTagsPane(personCard);
            assertEquals(2, tagsPane.getChildren().size(),
                    "Tags pane should have 2 tag labels");
        });
    }

    @Test
    public void constructor_withTags_sortedAlphabetically() throws Exception {
        runOnJavaFxThread(() -> {
            Person person = new PersonBuilder()
                    .withTags("zebra", "apple", "middle")
                    .build();
            PersonCard personCard = new PersonCard(person, 1);

            FlowPane tagsPane = getTagsPane(personCard);
            assertEquals(3, tagsPane.getChildren().size());

            Label firstTag = (Label) tagsPane.getChildren().get(0);
            Label secondTag = (Label) tagsPane.getChildren().get(1);
            Label thirdTag = (Label) tagsPane.getChildren().get(2);

            assertEquals("apple", firstTag.getText());
            assertEquals("middle", secondTag.getText());
            assertEquals("zebra", thirdTag.getText());
        });
    }

    @Test
    public void constructor_differentIndexes_correctIdDisplay() throws Exception {
        runOnJavaFxThread(() -> {
            Person person = new PersonBuilder().build();

            PersonCard card1 = new PersonCard(person, 1);
            assertEquals("1. ", getIdLabel(card1).getText());

            PersonCard card10 = new PersonCard(person, 10);
            assertEquals("10. ", getIdLabel(card10).getText());

            PersonCard card100 = new PersonCard(person, 100);
            assertEquals("100. ", getIdLabel(card100).getText());
        });
    }

    @Test
    public void constructor_longName_displaysCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            String longName = "Alexander Benjamin Christopher Davidson Edwards";
            Person person = new PersonBuilder().withName(longName).build();
            PersonCard personCard = new PersonCard(person, 1);

            Label nameLabel = getNameLabel(personCard);
            assertEquals(longName, nameLabel.getText());
        });
    }

    @Test
    public void constructor_longAddress_displaysCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            String longAddress = "Block 123, Very Long Street Name Avenue, "
                    + "#12-345, Apartment Complex Building, Singapore 678901";
            Person person = new PersonBuilder().withAddress(longAddress).build();
            PersonCard personCard = new PersonCard(person, 1);

            Label addressLabel = getAddressLabel(personCard);
            assertEquals(longAddress, addressLabel.getText());
        });
    }

    @Test
    public void constructor_nameWithNumbers_displaysCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            Person person = new PersonBuilder()
                    .withName("John Doe 3rd")
                    .build();
            PersonCard personCard = new PersonCard(person, 1);

            Label nameLabel = getNameLabel(personCard);
            assertEquals("John Doe 3rd", nameLabel.getText());
        });
    }

    @Test
    public void constructor_multipleSpacesInName_displaysCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            Person person = new PersonBuilder()
                    .withName("Mary Jane Watson")
                    .build();
            PersonCard personCard = new PersonCard(person, 1);

            Label nameLabel = getNameLabel(personCard);
            assertEquals("Mary Jane Watson", nameLabel.getText());
        });
    }

    @Test
    public void constructor_multiplePeople_independentCards() throws Exception {
        runOnJavaFxThread(() -> {
            Person alice = new PersonBuilder()
                    .withName("Alice")
                    .withPhone("11111111")
                    .build();

            Person bob = new PersonBuilder()
                    .withName("Bob")
                    .withPhone("22222222")
                    .build();

            PersonCard aliceCard = new PersonCard(alice, 1);
            PersonCard bobCard = new PersonCard(bob, 2);

            assertEquals("Alice", getNameLabel(aliceCard).getText());
            assertEquals("11111111", getPhoneLabel(aliceCard).getText());

            assertEquals("Bob", getNameLabel(bobCard).getText());
            assertEquals("22222222", getPhoneLabel(bobCard).getText());
        });
    }

    @Test
    public void constructor_zeroIndex_displaysCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            Person person = new PersonBuilder().build();
            PersonCard personCard = new PersonCard(person, 0);

            Label idLabel = getIdLabel(personCard);
            assertEquals("0. ", idLabel.getText());
        });
    }

    @Test
    public void constructor_manyTags_allDisplayed() throws Exception {
        runOnJavaFxThread(() -> {
            Person person = new PersonBuilder()
                    .withTags("tag1", "tag2", "tag3", "tag4", "tag5", "tag6")
                    .build();
            PersonCard personCard = new PersonCard(person, 1);

            FlowPane tagsPane = getTagsPane(personCard);
            assertEquals(6, tagsPane.getChildren().size(),
                    "All 6 tags should be displayed");
        });
    }

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

    private Label getIdLabel(PersonCard personCard) {
        try {
            java.lang.reflect.Field field = PersonCard.class.getDeclaredField("id");
            field.setAccessible(true);
            return (Label) field.get(personCard);
        } catch (Exception e) {
            fail("Failed to get id label: " + e.getMessage());
            return null;
        }
    }

    private Label getNameLabel(PersonCard personCard) {
        try {
            java.lang.reflect.Field field = PersonCard.class.getDeclaredField("name");
            field.setAccessible(true);
            return (Label) field.get(personCard);
        } catch (Exception e) {
            fail("Failed to get name label: " + e.getMessage());
            return null;
        }
    }

    private Label getPhoneLabel(PersonCard personCard) {
        try {
            java.lang.reflect.Field field = PersonCard.class.getDeclaredField("phone");
            field.setAccessible(true);
            return (Label) field.get(personCard);
        } catch (Exception e) {
            fail("Failed to get phone label: " + e.getMessage());
            return null;
        }
    }

    private Label getAddressLabel(PersonCard personCard) {
        try {
            java.lang.reflect.Field field = PersonCard.class.getDeclaredField("address");
            field.setAccessible(true);
            return (Label) field.get(personCard);
        } catch (Exception e) {
            fail("Failed to get address label: " + e.getMessage());
            return null;
        }
    }

    private FlowPane getTagsPane(PersonCard personCard) {
        try {
            java.lang.reflect.Field field = PersonCard.class.getDeclaredField("tags");
            field.setAccessible(true);
            return (FlowPane) field.get(personCard);
        } catch (Exception e) {
            fail("Failed to get tags pane: " + e.getMessage());
            return null;
        }
    }
}
