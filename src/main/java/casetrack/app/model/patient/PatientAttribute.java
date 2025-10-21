package casetrack.app.model.patient;

import static java.util.Objects.requireNonNull;

/**
 * Represents an attribute for a Patient.
 */
public class PatientAttribute {
    public final String name;
    public final String value;

    /**
     * Creates a new attribute object.
     * @param name The name of the attribute
     * @param value The value of the attribute
     */
    public PatientAttribute(String name, String value) {
        requireNonNull(name);
        this.name = name;
        requireNonNull(value);
        this.value = value;
    }
}
