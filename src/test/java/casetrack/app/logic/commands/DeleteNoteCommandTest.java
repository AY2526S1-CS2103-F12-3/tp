package casetrack.app.logic.commands;

import static casetrack.app.logic.commands.CommandTestUtil.assertCommandFailure;
import static casetrack.app.logic.commands.CommandTestUtil.assertCommandSuccess;
import static casetrack.app.logic.commands.CommandTestUtil.showPatientAtIndex;
import static casetrack.app.testutil.TypicalIndexes.INDEX_FIRST_PATIENT;
import static casetrack.app.testutil.TypicalIndexes.INDEX_SECOND_PATIENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.Messages;
import casetrack.app.model.Model;
import casetrack.app.model.ModelManager;
import casetrack.app.model.UserPrefs;
import casetrack.app.model.patient.Note;
import casetrack.app.model.patient.Patient;
import casetrack.app.testutil.PatientBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for DeleteNoteCommand.
 */
public class DeleteNoteCommandTest {

    private Model model = new ModelManager();

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Patient patientWithNotes = new PatientBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong West Ave 6, #01-111")
                .withTags("friends")
                .build();

        // add notes to the patient
        Patient patientWithTwoNotes = patientWithNotes
                .addNote(new Note("First note"))
                .addNote(new Note("Second note"));

        model.addPatient(patientWithTwoNotes);

        Note noteToDelete = patientWithTwoNotes.getNotes().get(0);
        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(INDEX_FIRST_PATIENT, INDEX_FIRST_PATIENT);

        String expectedMessage = String.format(DeleteNoteCommand.MESSAGE_DELETE_NOTE_SUCCESS,
                patientWithTwoNotes.getName().fullName, noteToDelete.value);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Patient expectedPatient = patientWithTwoNotes.removeNote(0);
        expectedModel.setPatient(patientWithTwoNotes, expectedPatient);

        CommandResult expectedResult = new CommandResult(expectedMessage, expectedPatient, false, false);
        assertCommandSuccess(deleteNoteCommand, model, expectedResult, expectedModel);
    }

    @Test
    public void execute_invalidPatientIndexUnfilteredList_failure() {
        Patient patientWithNotes = new PatientBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong East, #01-111")
                .withTags("friends")
                .build();

        Patient patientWithOneNote = patientWithNotes.addNote(new Note("Test note"));
        model.addPatient(patientWithOneNote);

        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPatientList().size() + 1);
        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(outOfBoundIndex, INDEX_FIRST_PATIENT);

        assertCommandFailure(deleteNoteCommand, model, Messages.MESSAGE_INVALID_PATIENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidNoteIndex_failure() {
        Patient patientWithNotes = new PatientBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong East, #08-111")
                .withTags("friends")
                .build();

        Patient patientWithOneNote = patientWithNotes.addNote(new Note("Test note"));
        model.addPatient(patientWithOneNote);

        Index outOfBoundNoteIndex = Index.fromOneBased(patientWithOneNote.getNotes().size() + 1);
        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(INDEX_FIRST_PATIENT, outOfBoundNoteIndex);

        assertCommandFailure(deleteNoteCommand, model, DeleteNoteCommand.MESSAGE_INVALID_NOTE_INDEX);
    }

    @Test
    public void execute_patientWithNoNotes_failure() {
        Patient patientWithoutNotes = new PatientBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong East, #01-111")
                .withTags("friends")
                .build();

        model.addPatient(patientWithoutNotes);

        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(INDEX_FIRST_PATIENT, INDEX_FIRST_PATIENT);

        assertCommandFailure(deleteNoteCommand, model, DeleteNoteCommand.MESSAGE_NO_NOTES);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        // Create a patient with notes
        Patient patientWithNotes = new PatientBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong East, #01-111")
                .withTags("friends")
                .build();

        Patient patientWithTwoNotes = patientWithNotes
                .addNote(new Note("First note"))
                .addNote(new Note("Second note"));

        model.addPatient(patientWithTwoNotes);

        showPatientAtIndex(model, INDEX_FIRST_PATIENT);

        Note noteToDelete = patientWithTwoNotes.getNotes().get(0);
        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(INDEX_FIRST_PATIENT, INDEX_FIRST_PATIENT);

        String expectedMessage = String.format(DeleteNoteCommand.MESSAGE_DELETE_NOTE_SUCCESS,
                patientWithTwoNotes.getName().fullName, noteToDelete.value);

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        Patient expectedPatient = patientWithTwoNotes.removeNote(0);
        expectedModel.setPatient(patientWithTwoNotes, expectedPatient);
        showPatientAtIndex(expectedModel, INDEX_FIRST_PATIENT);

        CommandResult expectedResult = new CommandResult(expectedMessage, expectedPatient, false, false);
        assertCommandSuccess(deleteNoteCommand, model, expectedResult, expectedModel);
    }

    @Test
    public void execute_invalidPatientIndexFilteredList_failure() {
        Patient patientWithNotes = new PatientBuilder()
                .withName("Alice Pauline")
                .withPhone("94351253")
                .withEmail("alice@example.com")
                .withAddress("123, Jurong East, #01-111")
                .withTags("friends")
                .build();

        Patient patientWithOneNote = patientWithNotes.addNote(new Note("Test note"));
        model.addPatient(patientWithOneNote);

        showPatientAtIndex(model, INDEX_FIRST_PATIENT);
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPatientList().size() + 1);

        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(outOfBoundIndex, INDEX_FIRST_PATIENT);

        assertCommandFailure(deleteNoteCommand, model, Messages.MESSAGE_INVALID_PATIENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteNoteCommand deleteFirstNoteCommand = new DeleteNoteCommand(INDEX_FIRST_PATIENT, INDEX_FIRST_PATIENT);
        DeleteNoteCommand deleteSecondNoteCommand = new DeleteNoteCommand(INDEX_SECOND_PATIENT, INDEX_FIRST_PATIENT);
        DeleteNoteCommand deleteFirstNoteSecondIndexCommand = new DeleteNoteCommand(INDEX_FIRST_PATIENT,
                INDEX_SECOND_PATIENT);

        // same object - returns true
        assertTrue(deleteFirstNoteCommand.equals(deleteFirstNoteCommand));

        // same values - returns true
        DeleteNoteCommand deleteFirstNoteCommandCopy = new DeleteNoteCommand(INDEX_FIRST_PATIENT, INDEX_FIRST_PATIENT);
        assertTrue(deleteFirstNoteCommand.equals(deleteFirstNoteCommandCopy));

        // different types - returns false
        assertFalse(deleteFirstNoteCommand.equals(1));

        // null - returns false
        assertFalse(deleteFirstNoteCommand.equals(null));

        // different patient index - returns false
        assertFalse(deleteFirstNoteCommand.equals(deleteSecondNoteCommand));

        // different note index - returns false
        assertFalse(deleteFirstNoteCommand.equals(deleteFirstNoteSecondIndexCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetPatientIndex = Index.fromOneBased(1);
        Index targetNoteIndex = Index.fromOneBased(1);
        DeleteNoteCommand deleteNoteCommand = new DeleteNoteCommand(targetPatientIndex, targetNoteIndex);
        String expected = DeleteNoteCommand.class.getCanonicalName() + "{patientIndex=" + targetPatientIndex
                + ", noteIndex=" + targetNoteIndex + "}";
        assertEquals(expected, deleteNoteCommand.toString());
    }
}
