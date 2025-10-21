package casetrack.app.logic.commands;

import static casetrack.app.logic.commands.CommandTestUtil.assertCommandFailure;
import static casetrack.app.logic.commands.CommandTestUtil.assertCommandSuccess;
import static casetrack.app.logic.commands.CommandTestUtil.showPatientAtIndex;
import static casetrack.app.testutil.Assert.assertThrows;
import static casetrack.app.testutil.TypicalIndexes.INDEX_FIRST_PATIENT;
import static casetrack.app.testutil.TypicalIndexes.INDEX_SECOND_PATIENT;
import static casetrack.app.testutil.TypicalPatients.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.junit.jupiter.api.Test;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.Messages;
import casetrack.app.model.Model;
import casetrack.app.model.ModelManager;
import casetrack.app.model.UserPrefs;
import casetrack.app.model.patient.Name;
import casetrack.app.model.patient.Note;
import casetrack.app.model.patient.Patient;
import casetrack.app.model.patient.Phone;

/**
 * Contains integration tests (interaction with the Model) and unit tests for NoteCommand.
 */
public class AddNoteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        Note validNote = new Note("Valid note");
        assertThrows(NullPointerException.class, () -> new NoteCommand((Index) null, validNote));
    }

    @Test
    public void constructor_nullNote_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new NoteCommand(INDEX_FIRST_PATIENT, null));
    }

    @Test
    public void constructor_nullName_throwsNullPointerException() {
        Phone validPhone = new Phone("91234567");
        Note validNote = new Note("Valid note");
        assertThrows(NullPointerException.class, () -> new NoteCommand(null, validPhone, validNote));
    }

    @Test
    public void constructor_nullPhone_throwsNullPointerException() {
        Name validName = new Name("John Doe");
        Note validNote = new Note("Valid note");
        assertThrows(NullPointerException.class, () -> new NoteCommand(validName, null, validNote));
    }

    @Test
    public void constructor_nullNoteWithNamePhone_throwsNullPointerException() {
        Name validName = new Name("John Doe");
        Phone validPhone = new Phone("91234567");
        assertThrows(NullPointerException.class, () -> new NoteCommand(validName, validPhone, null));
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Patient patientToAddNoteTo = model.getFilteredPatientList().get(INDEX_FIRST_PATIENT.getZeroBased());
        Note noteToAdd = new Note("Follow-up in 2 weeks");
        NoteCommand noteCommand = new NoteCommand(INDEX_FIRST_PATIENT, noteToAdd);

        Patient expectedPatient = patientToAddNoteTo.addNote(noteToAdd);
        String expectedMessage = String.format(NoteCommand.MESSAGE_SUCCESS,
                expectedPatient.getName().fullName,
                        expectedPatient.getPhone().value,
                noteToAdd.value);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPatient(patientToAddNoteTo, expectedPatient);

        CommandResult expectedResult = new CommandResult(expectedMessage, expectedPatient, false, false);
        assertCommandSuccess(noteCommand, model, expectedResult, expectedModel);
    }

    @Test
    public void execute_validNameAndPhoneUnfilteredList_success() {
        Patient patientToAddNoteTo = model.getFilteredPatientList().get(INDEX_FIRST_PATIENT.getZeroBased());
        Note noteToAdd = new Note("Follow-up in 2 weeks");
        NoteCommand noteCommand = new NoteCommand(patientToAddNoteTo.getName(),
                patientToAddNoteTo.getPhone(), noteToAdd);

        Patient expectedPatient = patientToAddNoteTo.addNote(noteToAdd);
        String expectedMessage = String.format(NoteCommand.MESSAGE_SUCCESS,
                expectedPatient.getName().fullName,
                        expectedPatient.getPhone().value,
                noteToAdd.value);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPatient(patientToAddNoteTo, expectedPatient);

        CommandResult expectedResult = new CommandResult(expectedMessage, expectedPatient, false, false);
        assertCommandSuccess(noteCommand, model, expectedResult, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPatientList().size() + 1);
        Note noteToAdd = new Note("Follow-up in 2 weeks");
        NoteCommand noteCommand = new NoteCommand(outOfBoundIndex, noteToAdd);

        assertCommandFailure(noteCommand, model, Messages.MESSAGE_INVALID_PATIENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidNameAndPhone_throwsCommandException() {
        Name nonExistentName = new Name("Non Existent Patient");
        Phone nonExistentPhone = new Phone("99999999");
        Note noteToAdd = new Note("Follow-up in 2 weeks");
        NoteCommand noteCommand = new NoteCommand(nonExistentName, nonExistentPhone, noteToAdd);

        assertCommandFailure(noteCommand, model, NoteCommand.MESSAGE_PATIENT_NOT_FOUND);
    }

    @Test
    public void execute_missingPatientReference_throwsCommandException() throws Exception {
        // Start with a valid name/phone NoteCommand, then remove phone via reflection
        Patient somePatient = model.getFilteredPatientList().get(INDEX_FIRST_PATIENT.getZeroBased());
        Name existingName = somePatient.getName();
        Phone existingPhone = somePatient.getPhone();
        Note noteToAdd = new Note("Any note");
        NoteCommand noteCommand = new NoteCommand(existingName, existingPhone, noteToAdd);

        // Simulate parser creating a command without sufficient reference: clear phone
        Field phoneField = NoteCommand.class.getDeclaredField("phone");
        phoneField.setAccessible(true);
        phoneField.set(noteCommand, java.util.Optional.empty());

        assertCommandFailure(noteCommand, model, NoteCommand.MESSAGE_MISSING_PATIENT_REFERENCE);
    }

    @Test
    public void execute_validNameButInvalidPhone_throwsCommandException() {
        Patient existingPatient = model.getFilteredPatientList().get(INDEX_FIRST_PATIENT.getZeroBased());
        Name existingName = existingPatient.getName();
        Phone nonExistentPhone = new Phone("99999999");
        Note noteToAdd = new Note("Follow-up in 2 weeks");
        NoteCommand noteCommand = new NoteCommand(existingName, nonExistentPhone, noteToAdd);

        assertCommandFailure(noteCommand, model, NoteCommand.MESSAGE_PATIENT_NOT_FOUND);
    }

    @Test
    public void execute_nameAndPhoneNotInFilteredList_throwsCommandException() {
        // Take details from the first patient, then filter to show only the second patient
        Patient target = model.getFilteredPatientList().get(INDEX_FIRST_PATIENT.getZeroBased());
        Name targetName = target.getName();
        Phone targetPhone = target.getPhone();
        Note noteToAdd = new Note("Filter test note");

        showPatientAtIndex(model, INDEX_SECOND_PATIENT);

        NoteCommand noteCommand = new NoteCommand(targetName, targetPhone, noteToAdd);
        assertCommandFailure(noteCommand, model, NoteCommand.MESSAGE_PATIENT_NOT_FOUND);
    }

    @Test
    public void equals() {
        Note note1 = new Note("First note");
        Note note2 = new Note("Second note");
        Name name1 = new Name("Alice");
        Name name2 = new Name("Bob");
        Phone phone1 = new Phone("91234567");
        Phone phone2 = new Phone("98765432");

        NoteCommand addNoteFirstCommand = new NoteCommand(INDEX_FIRST_PATIENT, note1);
        NoteCommand addNoteSecondCommand = new NoteCommand(INDEX_SECOND_PATIENT, note1);
        NoteCommand addNoteNamePhoneCommand = new NoteCommand(name1, phone1, note1);

        // same object -> returns true
        assertTrue(addNoteFirstCommand.equals(addNoteFirstCommand));

        // same values -> returns true
        NoteCommand addNoteFirstCommandCopy = new NoteCommand(INDEX_FIRST_PATIENT, note1);
        assertTrue(addNoteFirstCommand.equals(addNoteFirstCommandCopy));

        // different types -> returns false
        assertFalse(addNoteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(addNoteFirstCommand.equals(null));

        // different patient index -> returns false
        assertFalse(addNoteFirstCommand.equals(addNoteSecondCommand));

        // different note -> returns false
        NoteCommand addNoteDifferentNoteCommand = new NoteCommand(INDEX_FIRST_PATIENT, note2);
        assertFalse(addNoteFirstCommand.equals(addNoteDifferentNoteCommand));

        // different identification method -> returns false
        assertFalse(addNoteFirstCommand.equals(addNoteNamePhoneCommand));

        // same name/phone identification -> returns true
        NoteCommand addNoteNamePhoneCommandCopy = new NoteCommand(name1, phone1, note1);
        assertTrue(addNoteNamePhoneCommand.equals(addNoteNamePhoneCommandCopy));

        // different name in name/phone identification -> returns false
        NoteCommand addNoteDifferentNameCommand = new NoteCommand(name2, phone1, note1);
        assertFalse(addNoteNamePhoneCommand.equals(addNoteDifferentNameCommand));

        // different phone in name/phone identification -> returns false
        NoteCommand addNoteDifferentPhoneCommand = new NoteCommand(name1, phone2, note1);
        assertFalse(addNoteNamePhoneCommand.equals(addNoteDifferentPhoneCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        Note note = new Note("Test note");
        NoteCommand noteCommand = new NoteCommand(targetIndex, note);
        String expected = "casetrack.app.logic.commands.NoteCommand{targetIndex=Optional[" + targetIndex
                + "], name=Optional.empty, phone=Optional.empty, note=" + note + "}";
        assertEquals(expected, noteCommand.toString());

        // Test name/phone identification
        Name name = new Name("John Doe");
        Phone phone = new Phone("91234567");
        NoteCommand noteCommandNamePhone = new NoteCommand(name, phone, note);
        String expectedNamePhone = "casetrack.app.logic.commands.NoteCommand{targetIndex=Optional.empty"
                + ", name=Optional[" + name + "], phone=Optional[" + phone + "], note=" + note + "}";
        assertEquals(expectedNamePhone, noteCommandNamePhone.toString());
    }
}
