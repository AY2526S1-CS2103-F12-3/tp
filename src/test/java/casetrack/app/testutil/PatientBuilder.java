package casetrack.app.testutil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import casetrack.app.model.patient.Address;
import casetrack.app.model.patient.Email;
import casetrack.app.model.patient.Income;
import casetrack.app.model.patient.Name;
import casetrack.app.model.patient.Note;
import casetrack.app.model.patient.Patient;
import casetrack.app.model.patient.Phone;
import casetrack.app.model.tag.Tag;
import casetrack.app.model.util.SampleDataUtil;

/**
 * A utility class to help with building Patient objects.
 */
public class PatientBuilder {

    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";
    public static final String DEFAULT_ADDRESS = "123, Jurong West Ave 6, #08-111";
    public static final String DEFAULT_INCOME = "1000";

    private Name name;
    private Phone phone;
    private Email email;
    private Address address;
    private Income income;
    private Set<Tag> tags;
    private List<Note> notes;

    /**
     * Creates a {@code PatientBuilder} with the default details.
     */
    public PatientBuilder() {
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        address = new Address(DEFAULT_ADDRESS);
        income = new Income(DEFAULT_INCOME);
        tags = new HashSet<>();
        notes = new ArrayList<>();
    }

    /**
     * Initializes the PatientBuilder with the data of {@code patientToCopy}.
     */
    public PatientBuilder(Patient patientToCopy) {
        name = patientToCopy.getName();
        phone = patientToCopy.getPhone();
        email = patientToCopy.getEmail();
        address = patientToCopy.getAddress();
        income = patientToCopy.getIncome();
        tags = new HashSet<>(patientToCopy.getTags());
        notes = new ArrayList<>(patientToCopy.getNotes());
    }

    /**
     * Sets the {@code Name} of the {@code Patient} that we are building.
     */
    public PatientBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Parses the {@code tags} into a {@code Set<Tag>} and set it to the
     * {@code Patient} that we are building.
     */
    public PatientBuilder withTags(String... tags) {
        this.tags = SampleDataUtil.getTagSet(tags);
        return this;
    }

    /**
     * Sets the {@code Address} of the {@code Patient} that we are building.
     */
    public PatientBuilder withAddress(String address) {
        this.address = new Address(address);
        return this;
    }

    /**
     * Sets the {@code Phone} of the {@code Patient} that we are building.
     */
    public PatientBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the {@code Email} of the {@code Patient} that we are building.
     */
    public PatientBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    /**
     * Sets the {@code Income} of the {@code Patient} that we are building.
     */
    public PatientBuilder withIncome(String income) {
        this.income = new Income(income);
        return this;
    }

    /**
     * Sets the {@code Notes} of the {@code Patient} that we are building.
     */
    public PatientBuilder withNotes(Note... notes) {
        this.notes.clear();
        for (Note note : notes) {
            this.notes.add(note);
        }
        return this;
    }

    /**
     * Builds a Patient object with the specified details.
     *
     * @return a Patient object with the configuration
     */
    public Patient build() {
        if (notes.isEmpty()) {
            return new Patient(name, phone, email, address, income, tags);
        } else {
            return new Patient(name, phone, email, address, income, tags, notes);
        }
    }

}
