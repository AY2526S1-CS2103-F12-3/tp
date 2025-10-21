package casetrack.app.model.patient;

import static casetrack.app.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_INCOME_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static casetrack.app.testutil.Assert.assertThrows;
import static casetrack.app.testutil.TypicalPatients.ALICE;
import static casetrack.app.testutil.TypicalPatients.BOB;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import casetrack.app.model.tag.Tag;
import casetrack.app.testutil.PatientBuilder;

public class PatientTest {

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Patient patient = new PatientBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> patient.getTags().remove(new Tag("dummy")));
    }

    @Test
    public void isSamePatient() {
        // same object -> returns true
        assertTrue(ALICE.isSamePatient(ALICE));

        // null -> returns false
        assertFalse(ALICE.isSamePatient(null));

        // same name, all other attributes different -> returns true
        Patient editedAlice = new PatientBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSamePatient(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new PatientBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSamePatient(editedAlice));

        // name differs in case, all other attributes same -> returns false
        Patient editedBob = new PatientBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertFalse(BOB.isSamePatient(editedBob));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PatientBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertFalse(BOB.isSamePatient(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Patient aliceCopy = new PatientBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different patient -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Patient editedAlice = new PatientBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PatientBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PatientBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new PatientBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different income -> returns false (ensures income.equals is evaluated)
        editedAlice = new PatientBuilder(ALICE).withIncome(VALID_INCOME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PatientBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));
    }

    @Test
    public void toStringMethod() {
        String expected = Patient.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", address=" + ALICE.getAddress() + ", income=" + ALICE.getIncome()
                + ", tags=" + ALICE.getTags()
                + ", notes=" + ALICE.getNotes() + "}";
        assertEquals(expected, ALICE.toString());
    }

    @Test
    public void getNotes_modifyList_throwsUnsupportedOperationException() {
        Patient patient = new PatientBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> patient.getNotes().remove(0));
    }

    @Test
    public void addNote() {
        Patient patient = new PatientBuilder().build();
        Note note = new Note("Test note");
        Patient patientWithNote = patient.addNote(note);

        assertTrue(patient.getNotes().isEmpty());

        // new patient have the note
        assertFalse(patientWithNote.getNotes().isEmpty());
        assertEquals(1, patientWithNote.getNotes().size());
        assertEquals(note, patientWithNote.getNotes().get(0));
    }

    @Test
    public void removeNote() {
        Note note1 = new Note("First note");
        Note note2 = new Note("Second note");
        Patient patient = new PatientBuilder()
                .withNotes(note1, note2)
                .build();

        Patient patientRemoved = patient.removeNote(0);

        // original patient should not be modified
        assertEquals(2, patient.getNotes().size());
        assertEquals(note1, patient.getNotes().get(0));
        assertEquals(note2, patient.getNotes().get(1));

        // new patient should have one note removed
        assertEquals(1, patientRemoved.getNotes().size());
        assertEquals(note2, patientRemoved.getNotes().get(0));
    }

    @Test
    public void removeNote_middleNote() {
        Note note1 = new Note("First note");
        Note note2 = new Note("Second note");
        Note note3 = new Note("Third note");
        Patient patient = new PatientBuilder()
                .withNotes(note1, note2, note3)
                .build();

        // remove middle note
        Patient patientRemoved = patient.removeNote(1);

        // original patient should not be modified
        assertEquals(3, patient.getNotes().size());

        assertEquals(2, patientRemoved.getNotes().size());
        assertEquals(note1, patientRemoved.getNotes().get(0));
        assertEquals(note3, patientRemoved.getNotes().get(1));
    }

    @Test
    public void equals_differentNotes_returnsFalse() {
        Note noteA = new Note("Alpha");
        Note noteB = new Note("Beta");

        Patient patientWithNoteA = new PatientBuilder(ALICE).withNotes(noteA).build();
        Patient patientWithNoteB = new PatientBuilder(ALICE).withNotes(noteB).build();

        // all other fields same, only notes differ -> equals should be false
        assertFalse(patientWithNoteA.equals(patientWithNoteB));
        assertFalse(patientWithNoteB.equals(patientWithNoteA));
    }

    @Test
    public void hashCode_includesNotes() {
        Note note = new Note("Hash note");

        Patient withoutNotes = new PatientBuilder(ALICE).build();
        Patient withNotes = new PatientBuilder(ALICE).withNotes(note).build();

        // hash codes should differ when only notes differ
        assertNotEquals(withoutNotes.hashCode(), withNotes.hashCode());
    }
}
