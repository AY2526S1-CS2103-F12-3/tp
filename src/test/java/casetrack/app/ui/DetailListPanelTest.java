package casetrack.app.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import casetrack.app.model.person.Note;
import casetrack.app.model.person.Person;
import casetrack.app.model.person.PersonAttribute;
import casetrack.app.testutil.PersonBuilder;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * Contains tests for DetailListPanel.
 * Tests use JavaFX Platform to properly test UI components.
 */
public class DetailListPanelTest {

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
            DetailListPanel panel = new DetailListPanel();
            assertNotNull(panel);
        });
    }

    @Test
    public void showDetails_personWithNoNotes_displaysNone() throws Exception {
        runOnJavaFxThread(() -> {
            DetailListPanel panel = new DetailListPanel();
            Person person = new PersonBuilder().build();

            panel.showDetails(person);

            ListView<PersonAttribute> listView = getDetailListView(panel);
            ObservableList<PersonAttribute> items = listView.getItems();

            assertEquals(4, items.size(), "Should display 4 attributes");

            PersonAttribute notesAttribute = items.get(3);
            assertEquals("Notes", notesAttribute.name);
            assertEquals("None", notesAttribute.value);
        });
    }

    @Test
    public void showDetails_personWithOneNote_displaysNumberedNote() throws Exception {
        runOnJavaFxThread(() -> {
            DetailListPanel panel = new DetailListPanel();
            Person person = new PersonBuilder()
                    .withNotes(new Note("First note"))
                    .build();

            panel.showDetails(person);

            ListView<PersonAttribute> listView = getDetailListView(panel);
            ObservableList<PersonAttribute> items = listView.getItems();

            PersonAttribute notesAttribute = items.get(3);
            assertEquals("Notes", notesAttribute.name);
            assertTrue(notesAttribute.value.contains("1. First note"),
                    "Should contain numbered note");
        });
    }

    @Test
    public void showDetails_personWithMultipleNotes_displaysAllNumbered() throws Exception {
        runOnJavaFxThread(() -> {
            DetailListPanel panel = new DetailListPanel();
            Person person = new PersonBuilder()
                    .withNotes(
                        new Note("First note"),
                        new Note("Second note"),
                        new Note("Third note")
                    )
                    .build();

            panel.showDetails(person);

            ListView<PersonAttribute> listView = getDetailListView(panel);
            ObservableList<PersonAttribute> items = listView.getItems();

            PersonAttribute notesAttribute = items.get(3);
            assertEquals("Notes", notesAttribute.name);
            assertTrue(notesAttribute.value.contains("1. First note"),
                    "Should contain first numbered note");
            assertTrue(notesAttribute.value.contains("2. Second note"),
                    "Should contain second numbered note");
            assertTrue(notesAttribute.value.contains("3. Third note"),
                    "Should contain third numbered note");
        });
    }

    @Test
    public void showDetails_displaysPhoneAttribute() throws Exception {
        runOnJavaFxThread(() -> {
            DetailListPanel panel = new DetailListPanel();
            Person person = new PersonBuilder()
                    .withPhone("91234567")
                    .build();

            panel.showDetails(person);

            ListView<PersonAttribute> listView = getDetailListView(panel);
            ObservableList<PersonAttribute> items = listView.getItems();

            PersonAttribute phoneAttribute = items.get(0);
            assertEquals("Phone", phoneAttribute.name);
            assertEquals("91234567", phoneAttribute.value);
        });
    }

    @Test
    public void showDetails_displaysAddressAttribute() throws Exception {
        runOnJavaFxThread(() -> {
            DetailListPanel panel = new DetailListPanel();
            Person person = new PersonBuilder()
                    .withAddress("123 Test Street")
                    .build();

            panel.showDetails(person);

            ListView<PersonAttribute> listView = getDetailListView(panel);
            ObservableList<PersonAttribute> items = listView.getItems();

            PersonAttribute addressAttribute = items.get(1);
            assertEquals("Address", addressAttribute.name);
            assertEquals("123 Test Street", addressAttribute.value);
        });
    }

    @Test
    public void showDetails_displaysEmailAttribute() throws Exception {
        runOnJavaFxThread(() -> {
            DetailListPanel panel = new DetailListPanel();
            Person person = new PersonBuilder()
                    .withEmail("test@example.com")
                    .build();

            panel.showDetails(person);

            ListView<PersonAttribute> listView = getDetailListView(panel);
            ObservableList<PersonAttribute> items = listView.getItems();

            PersonAttribute emailAttribute = items.get(2);
            assertEquals("Email", emailAttribute.name);
            assertEquals("test@example.com", emailAttribute.value);
        });
    }

    @Test
    public void showDetails_updatesExistingList() throws Exception {
        runOnJavaFxThread(() -> {
            DetailListPanel panel = new DetailListPanel();
            Person person1 = new PersonBuilder()
                    .withPhone("11111111")
                    .withEmail("first@test.com")
                    .build();

            Person person2 = new PersonBuilder()
                    .withPhone("22222222")
                    .withEmail("second@test.com")
                    .build();

            panel.showDetails(person1);
            ListView<PersonAttribute> listView = getDetailListView(panel);
            ObservableList<PersonAttribute> items = listView.getItems();
            assertEquals("11111111", items.get(0).value);

            panel.showDetails(person2);
            assertEquals("22222222", items.get(0).value);
            assertEquals("second@test.com", items.get(2).value);
        });
    }

    @Test
    public void showDetails_allAttributesPresent() throws Exception {
        runOnJavaFxThread(() -> {
            DetailListPanel panel = new DetailListPanel();
            Person person = new PersonBuilder().build();

            panel.showDetails(person);

            ListView<PersonAttribute> listView = getDetailListView(panel);
            ObservableList<PersonAttribute> items = listView.getItems();

            assertEquals(4, items.size(), "Should have exactly 4 attributes");
            assertEquals("Phone", items.get(0).name);
            assertEquals("Address", items.get(1).name);
            assertEquals("Email", items.get(2).name);
            assertEquals("Notes", items.get(3).name);
        });
    }

    @Test
    public void showDetails_longAddress_displaysCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            DetailListPanel panel = new DetailListPanel();
            String longAddress = "Block 123, Very Long Street Name With Many Words, "
                    + "Unit #12-345, Singapore 678901";
            Person person = new PersonBuilder()
                    .withAddress(longAddress)
                    .build();

            panel.showDetails(person);

            ListView<PersonAttribute> listView = getDetailListView(panel);
            ObservableList<PersonAttribute> items = listView.getItems();

            assertEquals(longAddress, items.get(1).value);
        });
    }

    @Test
    public void showDetails_specialCharactersInNotes_displaysCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            DetailListPanel panel = new DetailListPanel();
            Person person = new PersonBuilder()
                    .withNotes(
                        new Note("Note with special chars: !@#$%^&*()"),
                        new Note("Note with unicode: 你好世界")
                    )
                    .build();

            panel.showDetails(person);

            ListView<PersonAttribute> listView = getDetailListView(panel);
            ObservableList<PersonAttribute> items = listView.getItems();

            PersonAttribute notesAttribute = items.get(3);
            assertTrue(notesAttribute.value.contains("!@#$%^&*()"));
            assertTrue(notesAttribute.value.contains("你好世界"));
        });
    }

    @Test
    public void showDetails_manyNotes_allDisplayed() throws Exception {
        runOnJavaFxThread(() -> {
            DetailListPanel panel = new DetailListPanel();
            Person person = new PersonBuilder()
                    .withNotes(
                        new Note("Note 1"),
                        new Note("Note 2"),
                        new Note("Note 3"),
                        new Note("Note 4"),
                        new Note("Note 5"),
                        new Note("Note 6"),
                        new Note("Note 7"),
                        new Note("Note 8"),
                        new Note("Note 9"),
                        new Note("Note 10")
                    )
                    .build();

            panel.showDetails(person);

            ListView<PersonAttribute> listView = getDetailListView(panel);
            ObservableList<PersonAttribute> items = listView.getItems();

            PersonAttribute notesAttribute = items.get(3);

            assertTrue(notesAttribute.value.contains("1. Note 1"));
            assertTrue(notesAttribute.value.contains("10. Note 10"));
        });
    }

    @Test
    public void showDetails_differentPersons_independentUpdates() throws Exception {
        runOnJavaFxThread(() -> {
            DetailListPanel panel1 = new DetailListPanel();
            DetailListPanel panel2 = new DetailListPanel();

            Person person1 = new PersonBuilder()
                    .withName("Alice")
                    .withPhone("11111111")
                    .build();

            Person person2 = new PersonBuilder()
                    .withName("Bob")
                    .withPhone("22222222")
                    .build();

            panel1.showDetails(person1);
            panel2.showDetails(person2);

            ListView<PersonAttribute> listView1 = getDetailListView(panel1);
            ListView<PersonAttribute> listView2 = getDetailListView(panel2);

            assertEquals("11111111", listView1.getItems().get(0).value);
            assertEquals("22222222", listView2.getItems().get(0).value);
        });
    }

    @Test
    public void cellFactory_emptyCell_noGraphic() throws Exception {
        runOnJavaFxThread(() -> {
            DetailListPanel panel = new DetailListPanel();
            ListView<PersonAttribute> listView = getDetailListView(panel);

            // Create a cell and test empty state
            DetailListPanel.DetailListViewCell cell = panel.new DetailListViewCell();
            cell.updateItem(null, true);

            assertEquals(null, cell.getGraphic());
            assertEquals(null, cell.getText());
        });
    }

    @Test
    public void cellFactory_nonEmptyCell_hasGraphic() throws Exception {
        runOnJavaFxThread(() -> {
            DetailListPanel panel = new DetailListPanel();
            ListView<PersonAttribute> listView = getDetailListView(panel);

            PersonAttribute attribute = new PersonAttribute("Test", "Value");
            DetailListPanel.DetailListViewCell cell = panel.new DetailListViewCell();
            cell.updateItem(attribute, false);

            assertNotNull(cell.getGraphic(), "Cell should have graphic when not empty");
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
     * Gets the detail list view from DetailListPanel using reflection.
     */
    private ListView<PersonAttribute> getDetailListView(DetailListPanel panel) {
        try {
            java.lang.reflect.Field field = DetailListPanel.class.getDeclaredField("detailListView");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            ListView<PersonAttribute> listView = (ListView<PersonAttribute>) field.get(panel);
            return listView;
        } catch (Exception e) {
            fail("Failed to get detailListView: " + e.getMessage());
            return null;
        }
    }
}
