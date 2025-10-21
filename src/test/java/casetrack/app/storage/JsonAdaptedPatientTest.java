package casetrack.app.storage;

import static casetrack.app.storage.JsonAdaptedPatient.MISSING_FIELD_MESSAGE_FORMAT;
import static casetrack.app.testutil.Assert.assertThrows;
import static casetrack.app.testutil.TypicalPatients.BENSON;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import casetrack.app.commons.exceptions.IllegalValueException;
import casetrack.app.model.patient.Address;
import casetrack.app.model.patient.Email;
import casetrack.app.model.patient.Name;
import casetrack.app.model.patient.Note;
import casetrack.app.model.patient.Patient;
import casetrack.app.model.patient.Phone;
import casetrack.app.testutil.PatientBuilder;

public class JsonAdaptedPatientTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";
    private static final String INVALID_INCOME = "-5";

    private static final String VALID_NAME = BENSON.getName().toString();
    private static final String VALID_PHONE = BENSON.getPhone().toString();
    private static final String VALID_EMAIL = BENSON.getEmail().toString();
    private static final String VALID_ADDRESS = BENSON.getAddress().toString();
    private static final String VALID_INCOME = BENSON.getIncome().toString();
    private static final List<JsonAdaptedTag> VALID_TAGS = BENSON.getTags().stream()
            .map(JsonAdaptedTag::new)
            .collect(Collectors.toList());
    private static final List<String> VALID_NOTES = new ArrayList<>();

    @Test
    public void toModelType_validPatientDetails_returnsPatient() throws Exception {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(BENSON);
        assertEquals(BENSON, patient.toModelType());
    }

    @Test
    public void toModelType_invalidName_throwsIllegalValueException() {
        JsonAdaptedPatient patient =
                new JsonAdaptedPatient(INVALID_NAME, VALID_PHONE, VALID_EMAIL,
                        VALID_ADDRESS, VALID_INCOME, VALID_TAGS, VALID_NOTES);
        String expectedMessage = Name.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_nullName_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(null, VALID_PHONE,
                VALID_EMAIL, VALID_ADDRESS, VALID_INCOME, VALID_TAGS, VALID_NOTES);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_invalidPhone_throwsIllegalValueException() {
        JsonAdaptedPatient patient =
                new JsonAdaptedPatient(VALID_NAME, INVALID_PHONE, VALID_EMAIL,
                        VALID_ADDRESS, VALID_INCOME, VALID_TAGS, VALID_NOTES);
        String expectedMessage = Phone.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_nullPhone_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_NAME, null,
                VALID_EMAIL, VALID_ADDRESS, VALID_INCOME, VALID_TAGS, VALID_NOTES);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_invalidEmail_throwsIllegalValueException() {
        JsonAdaptedPatient patient =
                new JsonAdaptedPatient(VALID_NAME, VALID_PHONE, INVALID_EMAIL,
                        VALID_ADDRESS, VALID_INCOME, VALID_TAGS, VALID_NOTES);
        String expectedMessage = Email.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_nullEmail_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_NAME, VALID_PHONE,
                null, VALID_ADDRESS, VALID_INCOME, VALID_TAGS, VALID_NOTES);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_invalidAddress_throwsIllegalValueException() {
        JsonAdaptedPatient patient =
                new JsonAdaptedPatient(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                        INVALID_ADDRESS, VALID_INCOME, VALID_TAGS, VALID_NOTES);
        String expectedMessage = Address.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_nullAddress_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_NAME, VALID_PHONE,
                VALID_EMAIL, null, VALID_INCOME, VALID_TAGS, VALID_NOTES);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_invalidIncome_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, INVALID_INCOME, VALID_TAGS, VALID_NOTES);
        String expectedMessage = casetrack.app.model.patient.Income.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_nullIncome_throwsIllegalValueException() {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, null, VALID_TAGS, VALID_NOTES);
        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT,
                casetrack.app.model.patient.Income.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_invalidTags_throwsIllegalValueException() {
        List<JsonAdaptedTag> invalidTags = new ArrayList<>(VALID_TAGS);
        invalidTags.add(new JsonAdaptedTag(INVALID_TAG));
        JsonAdaptedPatient patient =
                new JsonAdaptedPatient(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                        VALID_ADDRESS, VALID_INCOME, invalidTags, VALID_NOTES);
        assertThrows(IllegalValueException.class, patient::toModelType);
    }

    @Test
    public void toModelType_validNotes_returnsPatient() throws Exception {
        List<String> validNotes = new ArrayList<>();
        validNotes.add("Follow-up in 2 weeks");
        validNotes.add("Patient mentioned financial difficulties");

        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_INCOME, VALID_TAGS, validNotes);

        Patient modelPatient = patient.toModelType();
        assertEquals(2, modelPatient.getNotes().size());
        assertTrue(modelPatient.getNotes().stream().anyMatch(note -> note.value.equals("Follow-up in 2 weeks")));
        assertTrue(modelPatient.getNotes().stream().anyMatch(note ->
                note.value.equals("Patient mentioned financial difficulties")));
    }

    @Test
    public void toModelType_emptyNotes_returnsPatient() throws Exception {
        List<String> emptyNotes = new ArrayList<>();

        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_INCOME, VALID_TAGS, emptyNotes);

        Patient modelPatient = patient.toModelType();
        assertTrue(modelPatient.getNotes().isEmpty());
    }

    @Test
    public void toModelType_nullNotes_returnsPatient() throws Exception {
        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_INCOME, VALID_TAGS, null);

        Patient modelPatient = patient.toModelType();
        assertTrue(modelPatient.getNotes().isEmpty());
    }

    @Test
    public void toModelType_invalidNotes_throwsIllegalValueException() {
        List<String> notesWithInvalid = new ArrayList<>();
        notesWithInvalid.add("Valid note");
        notesWithInvalid.add("   "); // Invalid note (whitespace only)
        notesWithInvalid.add("Another valid note");
        notesWithInvalid.add(""); // Invalid note (empty)

        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_INCOME, VALID_TAGS, notesWithInvalid);

        String expectedMessage = Note.MESSAGE_CONSTRAINTS;
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void toModelType_nullNoteInList_throwsIllegalValueException() {
        List<String> notesWithNull = new ArrayList<>();
        notesWithNull.add("Valid note");
        notesWithNull.add(null); // Null note entry

        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_INCOME, VALID_TAGS, notesWithNull);

        String expectedMessage = String.format(MISSING_FIELD_MESSAGE_FORMAT, Note.class.getSimpleName());
        assertThrows(IllegalValueException.class, expectedMessage, patient::toModelType);
    }

    @Test
    public void constructor_patientWithNotes_success() throws Exception {
        // Create a patient with notes using PatientBuilder (need to extend it first)
        Patient patientWithNotes = new PatientBuilder(BENSON).build();
        patientWithNotes = patientWithNotes.addNote(new casetrack.app.model.patient.Note("Test note 1"));
        patientWithNotes = patientWithNotes.addNote(new casetrack.app.model.patient.Note("Test note 2"));

        JsonAdaptedPatient jsonPatient = new JsonAdaptedPatient(patientWithNotes);

        // Verify notes are properly stored
        assertEquals(2, jsonPatient.toModelType().getNotes().size());
    }

    @Test
    public void toModelType_notesWithSpecialCharacters_success() throws Exception {
        List<String> specialNotes = new ArrayList<>();
        specialNotes.add("Patient needs follow-up @clinic #urgent!");
        specialNotes.add("Medication: 50mg/day (morning & evening)");
        specialNotes.add("Contact: john.doe@email.com or +65-1234-5678");

        JsonAdaptedPatient patient = new JsonAdaptedPatient(VALID_NAME, VALID_PHONE, VALID_EMAIL,
                VALID_ADDRESS, VALID_INCOME, VALID_TAGS, specialNotes);

        Patient modelPatient = patient.toModelType();
        assertEquals(3, modelPatient.getNotes().size());
        assertTrue(modelPatient.getNotes().stream().anyMatch(note ->
            note.value.equals("Patient needs follow-up @clinic #urgent!")));
        assertTrue(modelPatient.getNotes().stream().anyMatch(note ->
            note.value.equals("Medication: 50mg/day (morning & evening)")));
        assertTrue(modelPatient.getNotes().stream().anyMatch(note ->
            note.value.equals("Contact: john.doe@email.com or +65-1234-5678")));
    }

}
