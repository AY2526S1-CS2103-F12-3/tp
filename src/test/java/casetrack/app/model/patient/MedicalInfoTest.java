package casetrack.app.model.patient;

import static casetrack.app.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class MedicalInfoTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new MedicalInfo(null));
    }

    @Test
    public void constructor_invalidMedicalInfo_throwsIllegalArgumentException() {
        String invalidInfo = "";
        assertThrows(IllegalArgumentException.class, () -> new MedicalInfo(invalidInfo));
    }

    @Test
    public void isValidMedicalInfo() {
        // null medical info
        assertThrows(NullPointerException.class, () -> MedicalInfo.isValidMedicalInfo(null));

        // invalid medical info
        assertFalse(MedicalInfo.isValidMedicalInfo("")); // empty string
        assertFalse(MedicalInfo.isValidMedicalInfo(" ")); // spaces only

        // valid medical info
        assertTrue(MedicalInfo.isValidMedicalInfo("Asthma"));
        assertTrue(MedicalInfo.isValidMedicalInfo("diabetes type 2"));
        assertTrue(MedicalInfo.isValidMedicalInfo("N/A"));
        assertTrue(MedicalInfo.isValidMedicalInfo("some info"));
    }

    @Test
    public void equals() {
        MedicalInfo info = new MedicalInfo("Asthma");

        // same values -> returns true
        assertTrue(info.equals(new MedicalInfo("Asthma")));

        // same object -> returns true
        assertTrue(info.equals(info));

        // null -> returns false
        assertFalse(info.equals(null));

        // different types -> returns false
        assertFalse(info.equals(5.0f));

        // different values -> returns false
        assertFalse(info.equals(new MedicalInfo("Diabetes")));
    }

    @Test
    public void toString_returnsMedicalInfoString() {
        MedicalInfo info = new MedicalInfo("Asthma");
        assertEquals("Asthma", info.toString());
    }

    @Test
    public void hashCode_equalValuesSameHash_differentValuesDifferentHash() {
        MedicalInfo info1 = new MedicalInfo("Asthma");
        MedicalInfo info2 = new MedicalInfo("Asthma");
        MedicalInfo different = new MedicalInfo("Diabetes");

        assertEquals(info1.hashCode(), info2.hashCode());

        assertNotEquals(info1.hashCode(), different.hashCode());

        int initialHash = info1.hashCode();
        assertEquals(initialHash, info1.hashCode());
        assertEquals(initialHash, info1.hashCode());
    }
}
