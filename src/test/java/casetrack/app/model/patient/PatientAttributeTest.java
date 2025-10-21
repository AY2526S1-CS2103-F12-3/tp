package casetrack.app.model.patient;

import static casetrack.app.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class PatientAttributeTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PatientAttribute(null, null));
        assertThrows(NullPointerException.class, () -> new PatientAttribute("test", null));
        assertThrows(NullPointerException.class, () -> new PatientAttribute(null, "test"));
    }

    @Test
    public void constructor_success() {
        PatientAttribute attribute = new PatientAttribute("name", "value");
        assertEquals(attribute.name, "name");
        assertEquals(attribute.value, "value");
    }

}
