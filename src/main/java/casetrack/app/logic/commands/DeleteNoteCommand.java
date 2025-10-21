package casetrack.app.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import casetrack.app.commons.core.index.Index;
import casetrack.app.commons.util.ToStringBuilder;
import casetrack.app.logic.Messages;
import casetrack.app.logic.commands.exceptions.CommandException;
import casetrack.app.model.Model;
import casetrack.app.model.patient.Note;
import casetrack.app.model.patient.Patient;

/**
 * Deletes a note from a patient identified using its displayed index from the address book.
 */
public class DeleteNoteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a note from a patient identified by the index number used in the displayed patient list.\n"
            + "Parameters: note <PATIENT_INDEX> <NOTE_INDEX>\n"
            + "Example: " + COMMAND_WORD + " note 1 2";

    public static final String MESSAGE_DELETE_NOTE_SUCCESS = "Deleted Note from %1$s: %2$s";
    public static final String MESSAGE_INVALID_NOTE_INDEX = "The note index provided is invalid.";
    public static final String MESSAGE_NO_NOTES = "This patient has no notes to delete.";

    private final Index patientIndex;
    private final Index noteIndex;

    /**
     * Creates a DeleteNoteCommand to delete a note from a patient.
     *
     * @param patientIndex The index of the patient in the filtered patient list
     * @param noteIndex    The index of the note to delete from the patient's notes
     */
    public DeleteNoteCommand(Index patientIndex, Index noteIndex) {
        this.patientIndex = patientIndex;
        this.noteIndex = noteIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Patient> lastShownList = model.getFilteredPatientList();

        if (patientIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PATIENT_DISPLAYED_INDEX);
        }

        Patient patientToUpdate = lastShownList.get(patientIndex.getZeroBased());
        List<Note> notes = patientToUpdate.getNotes();

        if (notes.isEmpty()) {
            throw new CommandException(MESSAGE_NO_NOTES);
        }

        if (noteIndex.getZeroBased() >= notes.size()) {
            throw new CommandException(MESSAGE_INVALID_NOTE_INDEX);
        }

        Note noteToDelete = notes.get(noteIndex.getZeroBased());
        Patient updatedPatient = patientToUpdate.removeNote(noteIndex.getZeroBased());
        model.setPatient(patientToUpdate, updatedPatient);
        return new CommandResult(String.format(MESSAGE_DELETE_NOTE_SUCCESS,
                patientToUpdate.getName().fullName, noteToDelete.value), updatedPatient, false, false);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteNoteCommand)) {
            return false;
        }

        DeleteNoteCommand otherDeleteNoteCommand = (DeleteNoteCommand) other;
        return patientIndex.equals(otherDeleteNoteCommand.patientIndex)
                && noteIndex.equals(otherDeleteNoteCommand.noteIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("patientIndex", patientIndex)
                .add("noteIndex", noteIndex)
                .toString();
    }
}
