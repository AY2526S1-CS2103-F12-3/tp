package casetrack.app.model.note;

import static casetrack.app.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import casetrack.app.model.patient.Note;

public class NoteTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Note(null));
    }

    @Test
    public void constructor_invalidNote_throwsIllegalArgumentException() {
        String invalidNote = "";
        assertThrows(IllegalArgumentException.class, () -> new Note(invalidNote));
    }

    @Test
    public void constructor_validNote_success() {
        // Valid note with content
        String validNote = "This is a test note";
        Note note = new Note(validNote);
        assertEquals(validNote, note.value);
    }

    @Test
    public void constructor_noteWithWhitespace_trimmed() {
        // Note with leading and trailing whitespace should be trimmed
        String noteWithWhitespace = "  This is a test note  ";
        String expectedTrimmed = "This is a test note";
        Note note = new Note(noteWithWhitespace);
        assertEquals(expectedTrimmed, note.value);
    }

    @Test
    public void isValidNote() {
        // null note
        assertThrows(NullPointerException.class, () -> Note.isValidNote(null));

        // invalid notes
        assertFalse(Note.isValidNote("")); // empty string
        assertFalse(Note.isValidNote(" ")); // spaces only
        assertFalse(Note.isValidNote("  ")); // multiple spaces only
        assertFalse(Note.isValidNote("\t")); // tab only
        assertFalse(Note.isValidNote("\n")); // newline only
        assertFalse(Note.isValidNote("   \t  \n  ")); // whitespace only

        // valid notes
        assertTrue(Note.isValidNote("This is a test note"));
        assertTrue(Note.isValidNote("a")); // single character
        assertTrue(Note.isValidNote("Follow-up in 2 weeks"));
        assertTrue(Note.isValidNote("Meeting notes: discussed project timeline"));
        assertTrue(Note.isValidNote("123")); // numbers
        assertTrue(Note.isValidNote("Note with special chars: @#$%^&*()"));
        assertTrue(Note.isValidNote("  Valid note with spaces  ")); // spaces around valid content
    }

    @Test
    public void equals() {
        Note note = new Note("This is a test note");

        // same values -> returns true
        Note noteCopy = new Note("This is a test note");
        assertTrue(note.equals(noteCopy));

        // same object -> returns true
        assertTrue(note.equals(note));

        // null -> returns false
        assertFalse(note.equals(null));

        // different type -> returns false
        assertFalse(note.equals(5));

        // different note content -> returns false
        Note differentNote = new Note("This is a different note");
        assertFalse(note.equals(differentNote));

        // same content with different whitespace -> returns true (after trimming)
        Note noteWithWhitespace = new Note("  This is a test note  ");
        assertTrue(note.equals(noteWithWhitespace));
    }

    @Test
    public void hashCode_sameNote_sameHashCode() {
        Note note1 = new Note("This is a test note");
        Note note2 = new Note("This is a test note");
        assertEquals(note1.hashCode(), note2.hashCode());

        // Notes with same content after trimming should have same hash code
        Note noteWithWhitespace = new Note("  This is a test note  ");
        assertEquals(note1.hashCode(), noteWithWhitespace.hashCode());
    }

    @Test
    public void hashCode_differentNote_differentHashCode() {
        Note note1 = new Note("This is a test note");
        Note note2 = new Note("This is a different note");
        // While not guaranteed, different notes should typically have different hash codes
        // This is a probabilistic test
        assertFalse(note1.hashCode() == note2.hashCode());
    }

    @Test
    public void toStringMethod() {
        String noteContent = "This is a test note";
        Note note = new Note(noteContent);
        assertEquals(noteContent, note.toString());

        // Test with trimmed content
        String noteWithWhitespace = "  This is a test note  ";
        String expectedTrimmed = "This is a test note";
        Note noteWithWhitespace2 = new Note(noteWithWhitespace);
        assertEquals(expectedTrimmed, noteWithWhitespace2.toString());
    }

    @Test
    public void testNoteFunctionality_fromTestFiles() {
        // Test cases inspired by TestNoteFunctionality.java

        // Test basic note creation
        Note note = new Note("This is a test note");
        assertEquals("This is a test note", note.toString());

        // Test follow-up note
        Note followUpNote = new Note("Follow-up in 2 weeks");
        assertEquals("Follow-up in 2 weeks", followUpNote.toString());

        // Test notes are not equal when content differs
        assertFalse(note.equals(followUpNote));

        // Test notes are equal when content is same
        Note duplicateNote = new Note("This is a test note");
        assertTrue(note.equals(duplicateNote));
    }
}
