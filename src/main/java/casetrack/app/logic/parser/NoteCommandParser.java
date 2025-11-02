package casetrack.app.logic.parser;

import static casetrack.app.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_NAME;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_NOTE_TEXT;
import static casetrack.app.logic.parser.CliSyntax.PREFIX_PHONE;

import casetrack.app.commons.core.index.Index;
import casetrack.app.logic.commands.NoteCommand;
import casetrack.app.logic.parser.exceptions.ParseException;
import casetrack.app.model.person.Name;
import casetrack.app.model.person.Note;
import casetrack.app.model.person.Phone;

/**
 * Parses input arguments and creates a new NoteCommand object
 */
public class NoteCommandParser implements Parser<NoteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the NoteCommand
     * and returns a NoteCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public NoteCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_NOTE_TEXT);

        // Check if note text is present
        if (argMultimap.getAllValues(PREFIX_NOTE_TEXT).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE));
        }

        // Take the first note text if multiple are specified
        Note note = ParserUtil.parseNote(argMultimap.getAllValues(PREFIX_NOTE_TEXT).get(0));

        // Check for name and phone-based identification
        if (argMultimap.getValue(PREFIX_NAME).isPresent() && argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            if (!argMultimap.getPreamble().isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE));
            }
            argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_NOTE_TEXT);
            Name name = ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get());
            Phone phone = ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get());
            return new NoteCommand(name, phone, note);
        }

        // Check for index-based identification (preamble should contain the index)
        if (!argMultimap.getPreamble().isEmpty()) {
            try {
                Index index = ParserUtil.parseIndex(argMultimap.getPreamble());
                return new NoteCommand(index, note);
            } catch (ParseException pe) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE));
            }
        }

        // Neither identification method provided
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, NoteCommand.MESSAGE_USAGE));
    }
}

