package casetrack.app.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import casetrack.app.commons.util.ToStringBuilder;
import casetrack.app.logic.Messages;
import casetrack.app.model.Model;
import casetrack.app.model.patient.Patient;

/**
 * Finds and lists all patients in address book whose specified field contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "search";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Finds all patients whose specified field contains any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: FIELD KEYWORD [MORE_KEYWORDS]...\n"
            + "Supported fields: name, number, email, tag\n"
            + "Examples: " + COMMAND_WORD + " name alice charlie\n"
            + "          " + COMMAND_WORD + " number 91234567\n"
            + "          " + COMMAND_WORD + " email alice@example.com\n"
            + "          " + COMMAND_WORD + " tag friend colleague";

    private final Predicate<Patient> predicate;

    public FindCommand(Predicate<Patient> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPatientList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PATIENTS_LISTED_OVERVIEW, model.getFilteredPatientList().size()));
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
