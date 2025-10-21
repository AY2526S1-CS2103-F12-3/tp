package casetrack.app.model.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import casetrack.app.testutil.PatientBuilder;

public class TagContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = Collections.singletonList("first");
        List<String> secondPredicateKeywordList = Arrays.asList("first", "second");

        TagContainsKeywordsPredicate firstPredicate = new TagContainsKeywordsPredicate(firstPredicateKeywordList);
        TagContainsKeywordsPredicate secondPredicate = new TagContainsKeywordsPredicate(secondPredicateKeywordList);

        assertTrue(firstPredicate.equals(firstPredicate));

        TagContainsKeywordsPredicate firstPredicateCopy = new TagContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        assertFalse(firstPredicate.equals(1));

        assertFalse(firstPredicate.equals(null));

        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_tagContainsKeywords_returnsTrue() {
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(Collections.singletonList("friend"));
        assertTrue(predicate.test(new PatientBuilder().withTags("friend", "colleague").build()));

        predicate = new TagContainsKeywordsPredicate(Arrays.asList("friend", "colleague"));
        assertTrue(predicate.test(new PatientBuilder().withTags("friend", "colleague").build()));

        predicate = new TagContainsKeywordsPredicate(Arrays.asList("friend", "family"));
        assertTrue(predicate.test(new PatientBuilder().withTags("friend", "colleague").build()));

        predicate = new TagContainsKeywordsPredicate(Arrays.asList("FRIEND", "Colleague"));
        assertTrue(predicate.test(new PatientBuilder().withTags("friend", "colleague").build()));
    }

    @Test
    public void test_tagDoesNotContainKeywords_returnsFalse() {
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(Collections.emptyList());
        assertFalse(predicate.test(new PatientBuilder().withTags("friend").build()));

        predicate = new TagContainsKeywordsPredicate(Arrays.asList("family"));
        assertFalse(predicate.test(new PatientBuilder().withTags("friend", "colleague").build()));

        predicate = new TagContainsKeywordsPredicate(Arrays.asList("12345"));
        assertFalse(predicate.test(new PatientBuilder().withTags("friend").build()));
    }

    @Test
    public void test_tagContainsPartialKeywords_returnsTrue() {
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(Collections.singletonList("fri"));
        assertTrue(predicate.test(new PatientBuilder().withTags("friend").build()));

        predicate = new TagContainsKeywordsPredicate(Collections.singletonList("end"));
        assertTrue(predicate.test(new PatientBuilder().withTags("friend").build()));
    }

    @Test
    public void test_patientWithNoTags_returnsFalse() {
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(Collections.singletonList("friend"));
        assertFalse(predicate.test(new PatientBuilder().build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = Arrays.asList("friend", "colleague");
        TagContainsKeywordsPredicate predicate = new TagContainsKeywordsPredicate(keywords);

        String expected = TagContainsKeywordsPredicate.class.getCanonicalName() + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
