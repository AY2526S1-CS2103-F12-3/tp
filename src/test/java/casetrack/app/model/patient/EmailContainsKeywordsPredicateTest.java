package casetrack.app.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import casetrack.app.testutil.PatientBuilder;

public class EmailContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("alice");
        List<String> secondPredicateKeywordList = Arrays.asList("alice", "bob");

        EmailContainsKeywordsPredicate firstPredicate =
                new EmailContainsKeywordsPredicate(firstPredicateKeywordList);
        EmailContainsKeywordsPredicate secondPredicate =
                new EmailContainsKeywordsPredicate(firstPredicateKeywordList);
        EmailContainsKeywordsPredicate thirdPredicate =
                new EmailContainsKeywordsPredicate(secondPredicateKeywordList);

        assertTrue(firstPredicate.equals(firstPredicate));

        assertTrue(firstPredicate.equals(secondPredicate));

        assertFalse(firstPredicate.equals(1));

        assertFalse(firstPredicate.equals(null));

        assertFalse(firstPredicate.equals(thirdPredicate));
    }

    @Test
    public void test_emailContainsKeywords_returnsTrue() {
        EmailContainsKeywordsPredicate predicate =
                new EmailContainsKeywordsPredicate(Collections.singletonList("alice"));
        assertTrue(predicate.test(new PatientBuilder().withEmail("alice@example.com").build()));

        predicate = new EmailContainsKeywordsPredicate(Arrays.asList("alice", "bob"));
        assertTrue(predicate.test(new PatientBuilder().withEmail("alice@example.com").build()));

        predicate = new EmailContainsKeywordsPredicate(Arrays.asList("example", "com"));
        assertTrue(predicate.test(new PatientBuilder().withEmail("alice@example.com").build()));

        predicate = new EmailContainsKeywordsPredicate(Arrays.asList("alice", "example"));
        assertTrue(predicate.test(new PatientBuilder().withEmail("alice@example.com").build()));

        predicate = new EmailContainsKeywordsPredicate(Arrays.asList("lice"));
        assertTrue(predicate.test(new PatientBuilder().withEmail("alice@example.com").build()));
    }

    @Test
    public void test_emailDoesNotContainKeywords_returnsFalse() {
        EmailContainsKeywordsPredicate predicate =
                new EmailContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new PatientBuilder().withEmail("alice@example.com").build()));

        predicate = new EmailContainsKeywordsPredicate(Arrays.asList("bob"));
        assertFalse(predicate.test(new PatientBuilder().withEmail("alice@example.com").build()));

        predicate = new EmailContainsKeywordsPredicate(Arrays.asList("12345678", "Main", "Street"));
        assertFalse(predicate.test(new PatientBuilder().withName("Alice").withPhone("12345678")
                .withEmail("alice@example.com").withAddress("Main Street").build()));
    }

    @Test
    public void test_caseInsensitiveEmailSearch_returnsTrue() {
        EmailContainsKeywordsPredicate predicate =
                new EmailContainsKeywordsPredicate(Collections.singletonList("ALICE"));
        assertTrue(predicate.test(new PatientBuilder().withEmail("alice@example.com").build()));

        predicate = new EmailContainsKeywordsPredicate(Arrays.asList("EXAMPLE"));
        assertTrue(predicate.test(new PatientBuilder().withEmail("alice@example.com").build()));

        predicate = new EmailContainsKeywordsPredicate(Arrays.asList("COM"));
        assertTrue(predicate.test(new PatientBuilder().withEmail("alice@example.com").build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = Arrays.asList("alice", "example");
        EmailContainsKeywordsPredicate predicate = new EmailContainsKeywordsPredicate(keywords);

        String expected = EmailContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
