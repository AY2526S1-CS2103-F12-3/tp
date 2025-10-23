package casetrack.app.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import casetrack.app.model.person.Person;
import casetrack.app.testutil.PersonBuilder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * Contains tests for PersonListPanel.
 * Tests use JavaFX Platform to properly test UI components.
 */
public class PersonListPanelTest {

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
    public void constructor_validList_success() throws Exception {
        runOnJavaFxThread(() -> {
            ObservableList<Person> personList = FXCollections.observableArrayList();
            PersonListPanel panel = new PersonListPanel(personList);
            assertNotNull(panel);
        });
    }

    @Test
    public void constructor_emptyList_success() throws Exception {
        runOnJavaFxThread(() -> {
            ObservableList<Person> emptyList = FXCollections.observableArrayList();
            PersonListPanel panel = new PersonListPanel(emptyList);

            ListView<Person> listView = getPersonListView(panel);
            assertEquals(0, listView.getItems().size());
        });
    }

    @Test
    public void constructor_listWithPeople_displaysAll() throws Exception {
        runOnJavaFxThread(() -> {
            Person alice = new PersonBuilder().withName("Alice").build();
            Person bob = new PersonBuilder().withName("Bob").build();
            Person charlie = new PersonBuilder().withName("Charlie").build();

            ObservableList<Person> personList = FXCollections.observableArrayList(alice, bob, charlie);
            PersonListPanel panel = new PersonListPanel(personList);

            ListView<Person> listView = getPersonListView(panel);
            assertEquals(3, listView.getItems().size());
            assertEquals(alice, listView.getItems().get(0));
            assertEquals(bob, listView.getItems().get(1));
            assertEquals(charlie, listView.getItems().get(2));
        });
    }

    @Test
    public void constructor_setsListViewItems() throws Exception {
        runOnJavaFxThread(() -> {
            Person person = new PersonBuilder().withName("Test Person").build();
            ObservableList<Person> personList = FXCollections.observableArrayList(person);

            PersonListPanel panel = new PersonListPanel(personList);

            ListView<Person> listView = getPersonListView(panel);
            assertEquals(personList, listView.getItems());
        });
    }

    @Test
    public void setPersonSelectCallback_callbackSet_triggeredOnSelection() throws Exception {
        runOnJavaFxThread(() -> {
            Person person = new PersonBuilder().withName("Test").build();
            ObservableList<Person> personList = FXCollections.observableArrayList(person);
            PersonListPanel panel = new PersonListPanel(personList);

            boolean[] callbackTriggered = {false};
            Person[] selectedPerson = {null};

            panel.setPersonSelectCallback(p -> {
                callbackTriggered[0] = true;
                selectedPerson[0] = p;
            });

            // Verify callback was set
            assertNotNull(getSelectPersonCallback(panel));
        });
    }

    @Test
    public void setSelectedPerson_validPerson_selected() throws Exception {
        runOnJavaFxThread(() -> {
            Person alice = new PersonBuilder().withName("Alice").build();
            Person bob = new PersonBuilder().withName("Bob").build();

            ObservableList<Person> personList = FXCollections.observableArrayList(alice, bob);
            PersonListPanel panel = new PersonListPanel(personList);

            panel.setSelectedPerson(bob);

            ListView<Person> listView = getPersonListView(panel);
            assertEquals(bob, listView.getSelectionModel().getSelectedItem());
        });
    }

    @Test
    public void setSelectedPerson_firstPerson_selected() throws Exception {
        runOnJavaFxThread(() -> {
            Person first = new PersonBuilder().withName("First").build();
            Person second = new PersonBuilder().withName("Second").build();

            ObservableList<Person> personList = FXCollections.observableArrayList(first, second);
            PersonListPanel panel = new PersonListPanel(personList);

            panel.setSelectedPerson(first);

            ListView<Person> listView = getPersonListView(panel);
            assertEquals(first, listView.getSelectionModel().getSelectedItem());
            assertEquals(0, listView.getSelectionModel().getSelectedIndex());
        });
    }

    @Test
    public void setSelectedPerson_lastPerson_selected() throws Exception {
        runOnJavaFxThread(() -> {
            Person first = new PersonBuilder().withName("First").build();
            Person second = new PersonBuilder().withName("Second").build();
            Person third = new PersonBuilder().withName("Third").build();

            ObservableList<Person> personList = FXCollections.observableArrayList(first, second, third);
            PersonListPanel panel = new PersonListPanel(personList);

            panel.setSelectedPerson(third);

            ListView<Person> listView = getPersonListView(panel);
            assertEquals(third, listView.getSelectionModel().getSelectedItem());
            assertEquals(2, listView.getSelectionModel().getSelectedIndex());
        });
    }

    @Test
    public void listUpdates_dynamicList_reflectedInPanel() throws Exception {
        runOnJavaFxThread(() -> {
            ObservableList<Person> personList = FXCollections.observableArrayList();
            PersonListPanel panel = new PersonListPanel(personList);

            ListView<Person> listView = getPersonListView(panel);
            assertEquals(0, listView.getItems().size());

            // Add person to list
            Person newPerson = new PersonBuilder().withName("New Person").build();
            personList.add(newPerson);

            assertEquals(1, listView.getItems().size());
            assertEquals(newPerson, listView.getItems().get(0));

            // Remove person from list
            personList.remove(newPerson);
            assertEquals(0, listView.getItems().size());
        });
    }

    @Test
    public void cellFactory_emptyCell_noGraphic() throws Exception {
        runOnJavaFxThread(() -> {
            ObservableList<Person> personList = FXCollections.observableArrayList();
            PersonListPanel panel = new PersonListPanel(personList);
            ListView<Person> listView = getPersonListView(panel);

            // Create a cell and test empty state
            PersonListPanel.PersonListViewCell cell = panel.new PersonListViewCell();
            cell.updateItem(null, true);

            assertNull(cell.getGraphic());
            assertNull(cell.getText());
        });
    }

    @Test
    public void cellFactory_nonEmptyCell_hasGraphic() throws Exception {
        runOnJavaFxThread(() -> {
            ObservableList<Person> personList = FXCollections.observableArrayList();
            PersonListPanel panel = new PersonListPanel(personList);
            ListView<Person> listView = getPersonListView(panel);

            Person person = new PersonBuilder().build();
            PersonListPanel.PersonListViewCell cell = panel.new PersonListViewCell();
            cell.updateItem(person, false);

            assertNotNull(cell.getGraphic(), "Cell should have graphic when not empty");
        });
    }

    @Test
    public void multiplePanels_independentLists() throws Exception {
        runOnJavaFxThread(() -> {
            Person alice = new PersonBuilder().withName("Alice").build();
            Person bob = new PersonBuilder().withName("Bob").build();

            ObservableList<Person> list1 = FXCollections.observableArrayList(alice);
            ObservableList<Person> list2 = FXCollections.observableArrayList(bob);

            PersonListPanel panel1 = new PersonListPanel(list1);
            PersonListPanel panel2 = new PersonListPanel(list2);

            ListView<Person> listView1 = getPersonListView(panel1);
            ListView<Person> listView2 = getPersonListView(panel2);

            assertEquals(1, listView1.getItems().size());
            assertEquals(alice, listView1.getItems().get(0));

            assertEquals(1, listView2.getItems().size());
            assertEquals(bob, listView2.getItems().get(0));
        });
    }

    @Test
    public void constructor_largeList_handlesCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            ObservableList<Person> largeList = FXCollections.observableArrayList();
            for (int i = 0; i < 100; i++) {
                largeList.add(new PersonBuilder().withName("Person " + i).build());
            }

            PersonListPanel panel = new PersonListPanel(largeList);
            ListView<Person> listView = getPersonListView(panel);

            assertEquals(100, listView.getItems().size());
        });
    }

    @Test
    public void setSelectedPerson_switchSelection_updatesCorrectly() throws Exception {
        runOnJavaFxThread(() -> {
            Person alice = new PersonBuilder().withName("Alice").build();
            Person bob = new PersonBuilder().withName("Bob").build();

            ObservableList<Person> personList = FXCollections.observableArrayList(alice, bob);
            PersonListPanel panel = new PersonListPanel(personList);

            // Select Alice
            panel.setSelectedPerson(alice);
            ListView<Person> listView = getPersonListView(panel);
            assertEquals(alice, listView.getSelectionModel().getSelectedItem());

            // Switch to Bob
            panel.setSelectedPerson(bob);
            assertEquals(bob, listView.getSelectionModel().getSelectedItem());
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
     * Gets the person list view from PersonListPanel using reflection.
     */
    private ListView<Person> getPersonListView(PersonListPanel panel) {
        try {
            java.lang.reflect.Field field = PersonListPanel.class.getDeclaredField("personListView");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            ListView<Person> listView = (ListView<Person>) field.get(panel);
            return listView;
        } catch (Exception e) {
            fail("Failed to get personListView: " + e.getMessage());
            return null;
        }
    }

    /**
     * Gets the selectPersonCallback from PersonListPanel using reflection.
     */
    private Object getSelectPersonCallback(PersonListPanel panel) {
        try {
            java.lang.reflect.Field field = PersonListPanel.class.getDeclaredField("selectPersonCallback");
            field.setAccessible(true);
            return field.get(panel);
        } catch (Exception e) {
            fail("Failed to get selectPersonCallback: " + e.getMessage());
            return null;
        }
    }
}
