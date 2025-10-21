package casetrack.app.model.patient;

import static casetrack.app.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import casetrack.app.commons.util.ToStringBuilder;
import casetrack.app.model.tag.Tag;

/**
 * Represents a Patient in the address book.
 * Guarantees: details are present and not null, field values are validated,
 * immutable.
 */
public class Patient {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Income income;
    private final Set<Tag> tags = new HashSet<>();
    private final List<Note> notes = new ArrayList<>();

    /**
     * Every field must be present and not null.
     */
    public Patient(Name name, Phone phone, Email email, Address address, Income income, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, income, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.income = income;
        this.tags.addAll(tags);
    }

    /**
     * Constructor with notes.
     */
    public Patient(Name name, Phone phone, Email email, Address address,
                    Income income, Set<Tag> tags, List<Note> notes) {
        requireAllNonNull(name, phone, email, address, income, tags, notes);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.income = income;
        this.tags.addAll(tags);
        this.notes.addAll(notes);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public Income getIncome() {
        return income;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns an immutable note list, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public List<Note> getNotes() {
        return Collections.unmodifiableList(notes);
    }

    /**
     * Returns a new Patient with the given note added to the notes list.
     */
    public Patient addNote(Note note) {
        List<Note> updatedNotes = new ArrayList<>(notes);
        updatedNotes.add(note);
        return new Patient(name, phone, email, address, income, new HashSet<>(tags), updatedNotes);
    }

    /**
     * Returns a new Patient with the note at the specified index removed from the
     * notes list.
     */
    public Patient removeNote(int noteIndex) {
        List<Note> updatedNotes = new ArrayList<>(notes);
        updatedNotes.remove(noteIndex);
        return new Patient(name, phone, email, address, income, new HashSet<>(tags), updatedNotes);
    }

    /**
     * Returns true if both patients have the same name.
     * This defines a weaker notion of equality between two patients.
     */
    public boolean isSamePatient(Patient otherPatient) {
        if (otherPatient == this) {
            return true;
        }

        return otherPatient != null
                && otherPatient.getName().equals(getName());
    }

    /**
     * Returns true if both patients have the same identity and data fields.
     * This defines a stronger notion of equality between two patients.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Patient)) {
            return false;
        }

        Patient otherPatient = (Patient) other;
        return name.equals(otherPatient.name)
                && phone.equals(otherPatient.phone)
                && email.equals(otherPatient.email)
                && address.equals(otherPatient.address)
                && tags.equals(otherPatient.tags)
                && income.equals(otherPatient.income)
                && notes.equals(otherPatient.notes);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, income, tags, notes);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("income", income)
                .add("tags", tags)
                .add("notes", notes)
                .toString();
    }

}
