package casetrack.app.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import casetrack.app.commons.core.index.Index;
import casetrack.app.commons.util.StringUtil;
import casetrack.app.logic.parser.exceptions.ParseException;
import casetrack.app.model.person.Address;
import casetrack.app.model.person.Email;
import casetrack.app.model.person.Income;
import casetrack.app.model.person.MedicalInfo;
import casetrack.app.model.person.Name;
import casetrack.app.model.person.Note;
import casetrack.app.model.person.Phone;
import casetrack.app.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "The index provided is invalid.";
    public static final String PATIENT_STRING = "patient";
    public static final String NOTE_STRING = "note";

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        try {
            return Index.fromOneBased(Integer.parseInt(trimmedIndex));
        } catch (NumberFormatException nfe) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String income} into an {@code Income}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code income} is invalid.
     */
    public static Income parseIncome(String income) throws ParseException {
        requireNonNull(income);
        String trimmedIncome = income.trim();
        if (!Income.isValidIncome(trimmedIncome)) {
            throw new ParseException(Income.MESSAGE_CONSTRAINTS);
        }
        return new Income(trimmedIncome);
    }

    /**
     * Parses a {@code String medicalInfo} into a {@code MedicalInfo}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code medicalInfo} is invalid.
     */
    public static MedicalInfo parseMedicalInfo(String medicalInfo) throws ParseException {
        requireNonNull(medicalInfo);
        String trimmedMedicalInfo = medicalInfo.trim();
        if (!MedicalInfo.isValidMedicalInfo(trimmedMedicalInfo)) {
            throw new ParseException(MedicalInfo.MESSAGE_CONSTRAINTS);
        }
        return new MedicalInfo(trimmedMedicalInfo);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }

    /**
     * Parses a {@code String note} into a {@code Note}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code note} is invalid.
     */
    public static Note parseNote(String note) throws ParseException {
        requireNonNull(note);
        String trimmedNote = note.trim();
        if (!Note.isValidNote(trimmedNote)) {
            throw new ParseException(Note.MESSAGE_CONSTRAINTS);
        }
        return new Note(trimmedNote);
    }
}
