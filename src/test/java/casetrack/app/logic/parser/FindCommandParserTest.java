package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static casetrack.app.logic.parser.CommandParserTestUtil.assertParseFailure;
import static casetrack.app.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import casetrack.app.logic.commands.FindCommand;
import casetrack.app.model.person.NameContainsKeywordsPredicate;
import casetrack.app.model.person.PhoneContainsKeywordsPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidSearchType_throwsParseException() {
        // invalid search type
        assertParseFailure(parser, "email alice@example.com",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "tags diabetes",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "invalid alice bob",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingKeywords_throwsParseException() {
        // only search type without keywords
        assertParseFailure(parser, "name", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "name   ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validNameArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList("Alice", "Bob")));
        assertParseSuccess(parser, "name Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n name \n Alice \n \t Bob  \t", expectedFindCommand);

        // single keyword
        FindCommand expectedSingleKeywordCommand =
                new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList("Alice")));
        assertParseSuccess(parser, "name Alice", expectedSingleKeywordCommand);

        // case insensitive search type
        assertParseSuccess(parser, "NAME Alice Bob", expectedFindCommand);
        assertParseSuccess(parser, "Name Alice Bob", expectedFindCommand);
    }

    @Test
    public void parse_validArgsWithDifferentCasing_returnsFindCommand() {
        // test case insensitive search type
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(Arrays.asList("alice", "BOB")));
        assertParseSuccess(parser, "name alice BOB", expectedFindCommand);
    }

    @Test
    public void parse_validNumberArgs_returnsFindCommand() {
        FindCommand expectedFindCommand =
                new FindCommand(new PhoneContainsKeywordsPredicate(Arrays.asList("123", "456")));
        assertParseSuccess(parser, "number 123 456", expectedFindCommand);

        assertParseSuccess(parser, " \n number \n 123 \n \t 456  \t", expectedFindCommand);

        FindCommand expectedSingleKeywordCommand =
                new FindCommand(new PhoneContainsKeywordsPredicate(Arrays.asList("123")));
        assertParseSuccess(parser, "number 123", expectedSingleKeywordCommand);

        assertParseSuccess(parser, "NUMBER 123 456", expectedFindCommand);
        assertParseSuccess(parser, "Number 123 456", expectedFindCommand);
    }

}
