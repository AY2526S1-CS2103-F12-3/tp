package casetrack.app.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import casetrack.app.commons.util.ToStringBuilder;
import casetrack.app.logic.Messages;
import casetrack.app.model.Model;
import casetrack.app.model.person.Person;

/**
 * Finds and lists all persons in address book whose specified field contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "search";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds all persons whose specified field contains any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: FIELD KEYWORD [MORE_KEYWORDS]...\n"
            + "Supported fields: name, email, phone\n"
            + "Examples: " + COMMAND_WORD + " name alice charlie\n"
            + "          " + COMMAND_WORD + " email gmail.com\n"
            + "          " + COMMAND_WORD + " phone 91234567";

    private final Predicate<Person> predicate;

    public FindCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand)) {
            return false;
        }

        FindCommand otherFindCommand = (FindCommand) other;
        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
