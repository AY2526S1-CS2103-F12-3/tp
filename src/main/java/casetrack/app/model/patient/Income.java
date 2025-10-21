package casetrack.app.model.patient;

import static casetrack.app.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

import java.math.BigDecimal;

/**
 * Represents a Patient's income in the address book.
 * Guarantees: immutable; numeric and non-negative (>= 0); decimals allowed.
 */
public class Income {

    public static final String MESSAGE_CONSTRAINTS =
            "Income must be a numeric value greater than or equal to 0.";

    private final BigDecimal value;

    /**
     * Constructs an {@code Income} from a numeric string.
     * Decimals are allowed. No commas or currency symbols.
     */
    public Income(String income) {
        requireNonNull(income);
        checkArgument(isValidIncome(income), MESSAGE_CONSTRAINTS);
        this.value = new BigDecimal(income.trim()).stripTrailingZeros();
    }

    /**
     * Returns true if a given string is a valid income value: numeric and >= 0.
     * Decimals are allowed. Commas or currency symbols are not allowed.
     */
    public static boolean isValidIncome(String test) {
        requireNonNull(test);
        String trimmed = test.trim();
        if (trimmed.isEmpty()) {
            return false;
        }
        try {
            BigDecimal bd = new BigDecimal(trimmed);
            return bd.compareTo(BigDecimal.ZERO) >= 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public BigDecimal getValue() {
        return value;
    }

    @Override
    public String toString() {
        // Use a canonical plain string without scientific notation
        BigDecimal normalized = value.stripTrailingZeros();
        return normalized.toPlainString();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Income)) {
            return false;
        }
        Income otherIncome = (Income) other;
        return this.value.compareTo(otherIncome.value) == 0;
    }

    @Override
    public int hashCode() {
        // Ensure values that are numerically equal have the same hash (e.g., 1 and 1.0)
        return this.value.stripTrailingZeros().hashCode();
    }
}
