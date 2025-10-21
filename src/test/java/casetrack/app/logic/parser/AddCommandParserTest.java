package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static casetrack.app.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static casetrack.app.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static casetrack.app.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.INCOME_DESC_AMY;
import static casetrack.app.logic.commands.CommandTestUtil.INCOME_DESC_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.INVALID_ADDRESS_DESC;
import static casetrack.app.logic.commands.CommandTestUtil.INVALID_EMAIL_DESC;
import static casetrack.app.logic.commands.CommandTestUtil.INVALID_INCOME_DESC;
import static casetrack.app.logic.commands.CommandTestUtil.INVALID_NAME_DESC;
import static casetrack.app.logic.commands.CommandTestUtil.INVALID_PHONE_DESC;
import static casetrack.app.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static casetrack.app.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static casetrack.app.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static casetrack.app.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.PREAMBLE_NON_EMPTY;
import static casetrack.app.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static casetrack.app.logic.commands.CommandTestUtil.TAG_DESC_FRIEND;
import static casetrack.app.logic.commands.CommandTestUtil.TAG_DESC_HUSBAND;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_INCOME_AMY;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_INCOME_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static casetrack.app.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_EMAIL;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_INCOME;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_NAME;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_PHONE;
import static casetrack.app.logic.parser.CommandParserTestUtil.assertParseFailure;
import static casetrack.app.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static casetrack.app.testutil.TypicalPatients.AMY;
import static casetrack.app.testutil.TypicalPatients.BOB;

import org.junit.jupiter.api.Test;

import casetrack.app.logic.Messages;
import casetrack.app.logic.commands.AddCommand;
import casetrack.app.model.patient.Address;
import casetrack.app.model.patient.Email;
import casetrack.app.model.patient.Name;
import casetrack.app.model.patient.Patient;
import casetrack.app.model.patient.Phone;
import casetrack.app.model.tag.Tag;
import casetrack.app.testutil.PatientBuilder;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Patient expectedPatient = new PatientBuilder(BOB).withIncome(VALID_INCOME_BOB).withTags(VALID_TAG_FRIEND)
            .build();

        // whitespace only preamble
        assertParseSuccess(parser, PREAMBLE_WHITESPACE + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + ADDRESS_DESC_BOB + INCOME_DESC_BOB + TAG_DESC_FRIEND, new AddCommand(expectedPatient));


        // multiple tags - all accepted
        Patient expectedPatientMultipleTags = new PatientBuilder(BOB).withIncome(VALID_INCOME_BOB)
                .withTags(VALID_TAG_FRIEND, VALID_TAG_HUSBAND).build();
        assertParseSuccess(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB
                        + INCOME_DESC_BOB + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                        new AddCommand(expectedPatientMultipleTags));
    }

    @Test
    public void parse_repeatedNonTagValue_failure() {
        String validExpectedPatientString = NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + INCOME_DESC_BOB + TAG_DESC_FRIEND;

        // multiple names
        assertParseFailure(parser, NAME_DESC_AMY + validExpectedPatientString,
                                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // multiple phones
        assertParseFailure(parser, PHONE_DESC_AMY + validExpectedPatientString,
                                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // multiple emails
        assertParseFailure(parser, EMAIL_DESC_AMY + validExpectedPatientString,
                                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // multiple addresses
        assertParseFailure(parser, ADDRESS_DESC_AMY + validExpectedPatientString,
                                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // multiple incomes
        assertParseFailure(parser, INCOME_DESC_AMY + validExpectedPatientString,
                                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_INCOME));

        // multiple fields repeated
        assertParseFailure(parser,
                        validExpectedPatientString + PHONE_DESC_AMY + EMAIL_DESC_AMY + NAME_DESC_AMY
                        + ADDRESS_DESC_AMY
                                        + INCOME_DESC_AMY + validExpectedPatientString,
                                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME, PREFIX_ADDRESS,
                        PREFIX_EMAIL, PREFIX_PHONE, PREFIX_INCOME));

        // invalid value followed by valid value

        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + validExpectedPatientString,
                                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, INVALID_EMAIL_DESC + validExpectedPatientString,
                                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, INVALID_PHONE_DESC + validExpectedPatientString,
                                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, INVALID_ADDRESS_DESC + validExpectedPatientString,
                                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // invalid income
        assertParseFailure(parser, INVALID_INCOME_DESC + validExpectedPatientString,
                                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_INCOME));

        // valid value followed by invalid value

        // invalid name
        assertParseFailure(parser, validExpectedPatientString + INVALID_NAME_DESC,
                                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));

        // invalid email
        assertParseFailure(parser, validExpectedPatientString + INVALID_EMAIL_DESC,
                                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));

        // invalid phone
        assertParseFailure(parser, validExpectedPatientString + INVALID_PHONE_DESC,
                                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));

        // invalid address
        assertParseFailure(parser, validExpectedPatientString + INVALID_ADDRESS_DESC,
                                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));

        // invalid income
        assertParseFailure(parser, validExpectedPatientString + INVALID_INCOME_DESC,
                                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_INCOME));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero tags
        Patient expectedPatient = new PatientBuilder(AMY).withIncome(VALID_INCOME_AMY).withTags().build();
        assertParseSuccess(parser, NAME_DESC_AMY + PHONE_DESC_AMY + EMAIL_DESC_AMY
                        + ADDRESS_DESC_AMY + INCOME_DESC_AMY,
                        new AddCommand(expectedPatient));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing name prefix
        assertParseFailure(parser, VALID_NAME_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + ADDRESS_DESC_BOB + INCOME_DESC_BOB,
                expectedMessage);

        // missing phone prefix
        assertParseFailure(parser, NAME_DESC_BOB + VALID_PHONE_BOB + EMAIL_DESC_BOB
                        + ADDRESS_DESC_BOB + INCOME_DESC_BOB,
                expectedMessage);

        // missing email prefix
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + VALID_EMAIL_BOB
                        + ADDRESS_DESC_BOB + INCOME_DESC_BOB,
                expectedMessage);

        // missing address prefix
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + VALID_ADDRESS_BOB + INCOME_DESC_BOB,
                expectedMessage);

        // missing income prefix
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + ADDRESS_DESC_BOB + VALID_INCOME_BOB,
                expectedMessage);

        // all prefixes missing
        assertParseFailure(parser, VALID_NAME_BOB + VALID_PHONE_BOB + VALID_EMAIL_BOB
                        + VALID_ADDRESS_BOB + VALID_INCOME_BOB,
                expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid name
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + INCOME_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, Name.MESSAGE_CONSTRAINTS);

        // invalid phone
        assertParseFailure(parser, NAME_DESC_BOB + INVALID_PHONE_DESC + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + INCOME_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, Phone.MESSAGE_CONSTRAINTS);

        // invalid email
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + INVALID_EMAIL_DESC
                + ADDRESS_DESC_BOB + INCOME_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, Email.MESSAGE_CONSTRAINTS);

        // invalid address
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + INVALID_ADDRESS_DESC + INCOME_DESC_BOB
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, Address.MESSAGE_CONSTRAINTS);

        // invalid income
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + INVALID_INCOME_DESC
                + TAG_DESC_HUSBAND + TAG_DESC_FRIEND, casetrack.app.model.patient.Income.MESSAGE_CONSTRAINTS);

        // invalid tag
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + INCOME_DESC_BOB
                + INVALID_TAG_DESC + VALID_TAG_FRIEND, Tag.MESSAGE_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, INVALID_NAME_DESC + PHONE_DESC_BOB + EMAIL_DESC_BOB
                        + INVALID_ADDRESS_DESC + INCOME_DESC_BOB,
                Name.MESSAGE_CONSTRAINTS);

        // non-empty preamble
        assertParseFailure(parser, PREAMBLE_NON_EMPTY + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB + INCOME_DESC_BOB + TAG_DESC_HUSBAND + TAG_DESC_FRIEND,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }
}
