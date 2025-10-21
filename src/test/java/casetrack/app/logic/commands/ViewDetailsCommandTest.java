package casetrack.app.logic.commands;

import static casetrack.app.logic.commands.CommandTestUtil.assertCommandFailure;
import static casetrack.app.logic.commands.CommandTestUtil.assertCommandSuccess;
import static casetrack.app.logic.commands.CommandTestUtil.showPatientAtIndex;
import static casetrack.app.testutil.TypicalIndexes.INDEX_FIRST_PATIENT;
import static casetrack.app.testutil.TypicalIndexes.INDEX_SECOND_PATIENT;
import static casetrack.app.testutil.TypicalPatients.getTypicalAddressBook;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.Messages;
import casetrack.app.model.Model;
import casetrack.app.model.ModelManager;
import casetrack.app.model.UserPrefs;
import casetrack.app.model.patient.Patient;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code ViewDetailsCommand}.
 */
public class ViewDetailsCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Patient patientToView = model.getFilteredPatientList().get(INDEX_FIRST_PATIENT.getZeroBased());
        ViewDetailsCommand viewDetailCommand = new ViewDetailsCommand(INDEX_FIRST_PATIENT);

        String expectedMessage = String.format(ViewDetailsCommand.MESSAGE_VIEW_DETAILS_SUCCESS,
                Messages.format(patientToView));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        CommandResult expectedResult = new CommandResult(expectedMessage, patientToView, false, false);
        assertCommandSuccess(viewDetailCommand, model, expectedResult, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPatientList().size() + 1);
        ViewDetailsCommand viewDetailCommand = new ViewDetailsCommand(outOfBoundIndex);

        assertCommandFailure(viewDetailCommand, model, Messages.MESSAGE_INVALID_PATIENT_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPatientAtIndex(model, INDEX_FIRST_PATIENT);

        Patient patientToView = model.getFilteredPatientList().get(INDEX_FIRST_PATIENT.getZeroBased());
        ViewDetailsCommand viewDetailCommand = new ViewDetailsCommand(INDEX_FIRST_PATIENT);

        String expectedMessage = String.format(ViewDetailsCommand.MESSAGE_VIEW_DETAILS_SUCCESS,
                Messages.format(patientToView));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        showPatientAtIndex(expectedModel, INDEX_FIRST_PATIENT);

        CommandResult expectedResult = new CommandResult(expectedMessage, patientToView, false, false);
        assertCommandSuccess(viewDetailCommand, model, expectedResult, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPatientAtIndex(model, INDEX_FIRST_PATIENT);

        Index outOfBoundIndex = INDEX_SECOND_PATIENT;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPatientList().size());

        ViewDetailsCommand viewDetailCommand = new ViewDetailsCommand(outOfBoundIndex);

        assertCommandFailure(viewDetailCommand, model, Messages.MESSAGE_INVALID_PATIENT_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ViewDetailsCommand viewFirstCommand = new ViewDetailsCommand(INDEX_FIRST_PATIENT);
        ViewDetailsCommand viewSecondCommand = new ViewDetailsCommand(INDEX_SECOND_PATIENT);

        // same object -> returns true
        assertTrue(viewFirstCommand.equals(viewFirstCommand));

        // same values -> returns true
        ViewDetailsCommand viewFirstCommandCopy = new ViewDetailsCommand(INDEX_FIRST_PATIENT);
        assertTrue(viewFirstCommand.equals(viewFirstCommandCopy));

        // different types -> returns false
        assertFalse(viewFirstCommand.equals(1));

        // null -> returns false
        assertFalse(viewFirstCommand.equals(null));

        // different patient -> returns false
        assertFalse(viewFirstCommand.equals(viewSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        ViewDetailsCommand viewDetailCommand = new ViewDetailsCommand(targetIndex);
        String expected = ViewDetailsCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, viewDetailCommand.toString());
    }
}
