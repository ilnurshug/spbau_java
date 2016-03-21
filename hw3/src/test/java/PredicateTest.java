/**
 * Created by ilnur on 20.03.16.
 */

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PredicateTest {
    private Predicate<Integer> greaterThan5 = arg -> arg > 5;

    private Predicate<Integer> isEven = arg -> arg % 2 == 0;

    @Test
    public void truePredicateReturnsTrue() throws Exception {
        assertTrue(Predicate.ALWAYS_TRUE.apply("false"));
        assertTrue(Predicate.ALWAYS_TRUE.apply(false));
        assertTrue(Predicate.ALWAYS_TRUE.apply(1));
    }

    @Test
    public void falsePredicateReturnsFalse() throws Exception {
        assertFalse(Predicate.ALWAYS_FALSE.apply("true"));
        assertFalse(Predicate.ALWAYS_FALSE.apply(true));
        assertFalse(Predicate.ALWAYS_FALSE.apply(0));
    }

    @Test
    public void testApply() throws Exception {
        assertTrue(greaterThan5.apply(7));
        assertFalse(greaterThan5.apply(4));
        assertTrue(isEven.apply(4));
        assertFalse(greaterThan5.apply(5));
    }

    @Test
    public void testOr() throws Exception {
        Predicate<Integer> moreThanFiveOrIsEven = greaterThan5.or(isEven);
        assertTrue(moreThanFiveOrIsEven.apply(6)); // True True
        assertTrue(moreThanFiveOrIsEven.apply(7)); // True False
        assertTrue(moreThanFiveOrIsEven.apply(4)); // False True
        assertFalse(moreThanFiveOrIsEven.apply(3)); // False False
    }

    @Test
    public void testAnd() throws Exception {
        Predicate<Integer> moreThanFiveAndIsEven = greaterThan5.and(isEven);
        assertTrue(moreThanFiveAndIsEven.apply(6)); // True True
        assertFalse(moreThanFiveAndIsEven.apply(7)); // True False
        assertFalse(moreThanFiveAndIsEven.apply(4)); // False True
        assertFalse(moreThanFiveAndIsEven.apply(3)); // False False
    }

    @Test
    public void testNot() throws Exception {
        assertFalse(greaterThan5.not().apply(7));
        assertTrue(greaterThan5.not().apply(4));
        assertFalse(isEven.not().apply(4));
        assertTrue(greaterThan5.not().apply(5));
    }
}