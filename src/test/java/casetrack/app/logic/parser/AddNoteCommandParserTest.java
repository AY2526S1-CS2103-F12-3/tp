package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_NAME;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_NOTE_TEXT;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_PHONE;
import static casetrack.app.logic.parser.CommandParserTestUtil.assertParseFailure;
import static casetrack.app.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static casetrack.app.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.commands.NoteCommand;
import casetrack.app.model.person.Name;
import casetrack.app.model.person.Note;
import casetrack.app.model.person.Phone;

public class AddNoteCommandParserTest {

    private NoteCommandParser parser = new NoteCommandParser();

    @Test
    public void parse_validArgsWithIndex_returnsNoteCommand() {
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + " " + PREFIX_NOTE_TEXT + "Follow-up in 2 weeks";
        Note expectedNote = new Note("Follow-up in 2 weeks");
        NoteCommand expectedCommand = new NoteCommand(targetIndex, expectedNote);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_validArgsWithNameAndPhone_returnsNoteCommand() {
        String userInput = " " + PREFIX_NAME + "John Doe " + PREFIX_PHONE + "91234567 "
                + PREFIX_NOTE_TEXT + "Follow-up in 2 weeks";
        Name expectedName = new Name("John Doe");
        Phone expectedPhone = new Phone("91234567");
        Note expectedNote = new Note("Follow-up in 2 weeks");
        NoteCommand expectedCommand = new NoteCommand(expectedName, expectedPhone, expectedNote);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingNoteText_throwsParseException() {
        String userInput = "1";
        assertParseFailure(parser, userInput, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyNoteText_throwsParseException() {
        String userInput = "1 " + PREFIX_NOTE_TEXT;
        assertParseFailure(parser, userInput, Note.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidNoteText_throwsParseException() {
        String userInput = "1 " + PREFIX_NOTE_TEXT + "   "; // whitespace only
        assertParseFailure(parser, userInput, Note.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidIndex_throwsParseException() {
        String userInput = "a " + PREFIX_NOTE_TEXT + "Valid note";
        assertParseFailure(parser, userInput, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_negativeIndex_throwsParseException() {
        String userInput = "-1 " + PREFIX_NOTE_TEXT + "Valid note";
        assertParseFailure(parser, userInput, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_zeroIndex_throwsParseException() {
        String userInput = "0 " + PREFIX_NOTE_TEXT + "Valid note";
        assertParseFailure(parser, userInput, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingName_throwsParseException() {
        String userInput = " " + PREFIX_PHONE + "91234567 " + PREFIX_NOTE_TEXT + "Valid note";
        assertParseFailure(parser, userInput, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingPhone_throwsParseException() {
        String userInput = " " + PREFIX_NAME + "John Doe " + PREFIX_NOTE_TEXT + "Valid note";
        assertParseFailure(parser, userInput, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidName_throwsParseException() {
        String userInput = " " + PREFIX_NAME + "John@Doe " + PREFIX_PHONE + "91234567 "
                + PREFIX_NOTE_TEXT + "Valid note";
        assertParseFailure(parser, userInput, Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidPhone_throwsParseException() {
        String userInput = " " + PREFIX_NAME + "John Doe " + PREFIX_PHONE + "invalid "
                + PREFIX_NOTE_TEXT + "Valid note";
        assertParseFailure(parser, userInput, Phone.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_bothIndexAndNamePhone_throwsParseException() {
        String userInput = "1 " + PREFIX_NAME + "John Doe " + PREFIX_PHONE + "91234567 "
                + PREFIX_NOTE_TEXT + "Valid note";
        assertParseFailure(parser, userInput, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_neitherIndexNorNamePhone_throwsParseException() {
        String userInput = " " + PREFIX_NOTE_TEXT + "Valid note";
        assertParseFailure(parser, userInput, String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyArgs_throwsParseException() {
        assertParseFailure(parser, "", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                NoteCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_duplicatePrefixes_throwsParseException() {
        String userInput = " " + PREFIX_NAME + "John Doe " + PREFIX_NAME + "Jane Doe "
                + PREFIX_PHONE + "91234567 " + PREFIX_NOTE_TEXT + "Valid note";
        assertParseFailure(parser, userInput, "Multiple values specified for the following single-valued field(s): n/");
    }

    @Test
    public void parse_validArgsWithExtraSpaces_returnsNoteCommand() {
        String userInput = "  1   " + PREFIX_NOTE_TEXT + "  Follow-up in 2 weeks  ";
        Note expectedNote = new Note("Follow-up in 2 weeks");
        NoteCommand expectedCommand = new NoteCommand(INDEX_FIRST_PERSON, expectedNote);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_validArgsWithSpecialCharacters_returnsNoteCommand() {
        String userInput = "1 " + PREFIX_NOTE_TEXT + "Patient needs follow-up @clinic #urgent!";
        Note expectedNote = new Note("Patient needs follow-up @clinic #urgent!");
        NoteCommand expectedCommand = new NoteCommand(INDEX_FIRST_PERSON, expectedNote);
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_multipleNoteTextPrefixes_takesFirstValue() {
        // Test with index-based identification
        String userInput = "2 " + PREFIX_NOTE_TEXT + "test 1 " + PREFIX_NOTE_TEXT + "test 2";
        Note expectedNote = new Note("test 1");
        NoteCommand expectedCommand = new NoteCommand(Index.fromOneBased(2), expectedNote);
        assertParseSuccess(parser, userInput, expectedCommand);
    }
}
