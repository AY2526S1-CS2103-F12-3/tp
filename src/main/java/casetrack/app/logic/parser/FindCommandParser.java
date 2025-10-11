package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import casetrack.app.logic.commands.FindCommand;
import casetrack.app.logic.parser.exceptions.ParseException;
import casetrack.app.model.person.NameContainsKeywordsPredicate;
import casetrack.app.model.person.Person;
import casetrack.app.model.person.PhoneContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        String[] argParts = trimmedArgs.split("\\s+");

        if (argParts.length < 2) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        String searchType = argParts[0].toLowerCase();
        List<String> keywords = Arrays.asList(Arrays.copyOfRange(argParts, 1, argParts.length));

        Predicate<Person> predicate = subcommand(searchType, keywords);
        return new FindCommand(predicate);
    }

    /**
     * Creates the appropriate predicate based on the search subcommand type.
     * @param searchType the type of search (name, email, phone, etc.)
     * @param keywords the keywords to search for
     * @return the appropriate predicate for the search type
     * @throws ParseException if the search type is invalid
     */
    private Predicate<Person> subcommand(String searchType, List<String> keywords) throws ParseException {
        switch (searchType) {
        case "name":
            return new NameContainsKeywordsPredicate(keywords);
        case "number":
            return new PhoneContainsKeywordsPredicate(keywords);
        default:
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
    }

}
