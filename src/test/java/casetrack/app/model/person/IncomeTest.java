package casetrack.app.model.person;

import static casetrack.app.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

public class IncomeTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Income(null));
    }

    @Test
    public void constructor_invalidIncome_throwsIllegalArgumentException() {
        String invalidIncome = "";
        assertThrows(IllegalArgumentException.class, () -> new Income(invalidIncome));
        assertThrows(IllegalArgumentException.class, () -> new Income("-1"));
        assertThrows(IllegalArgumentException.class, () -> new Income("$100"));
        assertThrows(IllegalArgumentException.class, () -> new Income("1,000"));
    }

    @Test
    public void isValidIncome() {
        // null income
        assertThrows(NullPointerException.class, () -> Income.isValidIncome(null));

        // invalid income
        assertFalse(Income.isValidIncome("")); // empty string
        assertFalse(Income.isValidIncome(" ")); // spaces only
        assertFalse(Income.isValidIncome("-1")); // negative number not allowed
        assertFalse(Income.isValidIncome("1,000")); // commas not allowed
        assertFalse(Income.isValidIncome("$1000")); // currency symbol not allowed
        assertFalse(Income.isValidIncome("abc")); // non-numeric

        // valid income
        assertTrue(Income.isValidIncome("0")); // zero allowed
        assertTrue(Income.isValidIncome("5")); // small integer
        assertTrue(Income.isValidIncome("1234567890")); // long integer
        assertTrue(Income.isValidIncome("1.0")); // decimals allowed
        assertTrue(Income.isValidIncome("3.1415")); // decimals allowed
        assertTrue(Income.isValidIncome("+2.5")); // plus sign allowed
    }

    @Test
    public void equals() {
        Income income = new Income("1000");

        // same numeric values -> returns true
        assertTrue(income.equals(new Income("1000.0")));

        // same object -> returns true
        assertTrue(income.equals(income));

        // null -> returns false
        assertFalse(income.equals(null));

        // different types -> returns false
        assertFalse(income.equals(5.0f));

        // different values -> returns false
        assertFalse(income.equals(new Income("999")));
    }

    @Test
    public void toString_returnsCanonicalIncomeString() {
        Income income = new Income("1000.5000");
        assertEquals("1000.5", income.toString());
    }

    @Test
    public void hashCode_equalValuesSameHash_differentValuesDifferentHash() {
        Income income1 = new Income("1000");
        Income income2 = new Income("1000.0");
        Income different = new Income("999");

        assertEquals(income1.hashCode(), income2.hashCode());

        assertNotEquals(income1.hashCode(), different.hashCode());

        int initialHash = income1.hashCode();
        assertEquals(initialHash, income1.hashCode());
        assertEquals(initialHash, income1.hashCode());
    }

    @Test
    public void leadingZeroes_inputsAreValid_andCanonicalized() {
        // validation accepts leading zeros
        assertTrue(Income.isValidIncome("000"));
        assertTrue(Income.isValidIncome("000.00"));
        assertTrue(Income.isValidIncome("00123"));
        assertTrue(Income.isValidIncome("000.50"));
        assertTrue(Income.isValidIncome("+0002.5"));

        // constructor and toString canonicalization
        assertEquals("0", new Income("000").toString());
        assertEquals("0", new Income("000.00").toString());
        assertEquals("123", new Income("00123").toString());
        assertEquals("0.5", new Income("000.50").toString());
        assertEquals("2.5", new Income("+0002.5").toString());

        // equality and hashCode
        assertEquals(new Income("000"), new Income("0"));
        assertEquals(new Income("00123"), new Income("123"));
        assertEquals(new Income("000.50"), new Income("0.5"));
        assertEquals(new Income("+0002.5"), new Income("2.5"));

        assertEquals(new Income("000").hashCode(), new Income("0").hashCode());
        assertEquals(new Income("00123").hashCode(), new Income("123").hashCode());
    }

    @Test
    public void getValue_returnsExpectedBigDecimal() {
        // exact integer
        assertEquals(new BigDecimal("0"), new Income("0").getValue());
        assertEquals(new BigDecimal("123"), new Income("123").getValue());

        // leading zeros are ignored in numeric value
        assertEquals(new BigDecimal("123"), new Income("00123").getValue());

        // decimals: trailing zeros removed in stored value
        assertEquals(new BigDecimal("0.5"), new Income("000.5000").getValue());

        // plus sign allowed, value parsed correctly
        assertEquals(new BigDecimal("2.5"), new Income("+2.5").getValue());

        // numeric equality: 1.0 is stored as 1
        assertEquals(new BigDecimal("1"), new Income("1.0").getValue());
    }
}
