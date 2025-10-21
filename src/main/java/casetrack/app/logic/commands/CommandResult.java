package casetrack.app.logic.commands;

import java.util.Objects;

import casetrack.app.commons.util.ToStringBuilder;
import casetrack.app.model.patient.Patient;

/**
 * Represents the result of a command execution.
 */
public class CommandResult {

    private final String feedbackToUser;

    /** Help information should be shown to the user. */
    private final boolean showHelp;

    /** The application should exit. */
    private final boolean exit;

    /** The application should display details for a patient. */
    private final Patient detailTarget;

    /**
     * Constructs a {@code CommandResult} with the specified fields.
     */
    public CommandResult(String feedbackToUser, Patient detailTarget, boolean showHelp, boolean exit) {
        this.feedbackToUser = feedbackToUser;
        this.detailTarget = detailTarget;
        this.showHelp = showHelp;
        this.exit = exit;
    }
    /**
     * Constructs a {@code CommandResult} with the specified {@code feedbackToUser},
     * and other fields set to their default value.
     */
    public CommandResult(String feedbackToUser) {
        this(feedbackToUser, null, false, false);
    }

    public Patient getDetailTarget() {
        return detailTarget;
    }

    public String getFeedbackToUser() {
        return feedbackToUser;
    }

    public boolean isShowHelp() {
        return showHelp;
    }

    public boolean isExit() {
        return exit;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof CommandResult)) {
            return false;
        }

        CommandResult otherCommandResult = (CommandResult) other;
        boolean isDetailTargetEqual = (detailTarget == null && otherCommandResult.detailTarget == null
                || detailTarget != null && detailTarget.equals(otherCommandResult.detailTarget));
        return feedbackToUser.equals(otherCommandResult.feedbackToUser)
                && isDetailTargetEqual
                && showHelp == otherCommandResult.showHelp
                && exit == otherCommandResult.exit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedbackToUser, detailTarget, showHelp, exit);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("feedbackToUser", feedbackToUser)
                .add("detailTarget", detailTarget)
                .add("showHelp", showHelp)
                .add("exit", exit)
                .toString();
    }

}
