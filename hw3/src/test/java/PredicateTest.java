/**
 * Created by ilnur on 20.03.16.
 */

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PredicateTest {
    private static final Predicate<Integer> GREATER_THAN_5 = arg -> arg > 5;

    private static final Predicate<Integer> IS_EVEN = arg -> arg % 2 == 0;

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
        assertTrue(GREATER_THAN_5.apply(7));
        assertFalse(GREATER_THAN_5.apply(4));
        assertTrue(IS_EVEN.apply(4));
        assertFalse(GREATER_THAN_5.apply(5));
    }

    @Test
    public void testOr() throws Exception {
        Predicate<Integer> moreThanFiveOrIsEven = GREATER_THAN_5.or(IS_EVEN);
        assertTrue(moreThanFiveOrIsEven.apply(6)); // True True
        assertTrue(moreThanFiveOrIsEven.apply(7)); // True False
        assertTrue(moreThanFiveOrIsEven.apply(4)); // False True
        assertFalse(moreThanFiveOrIsEven.apply(3)); // False False

        // lazy

        Predicate<Integer> second = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer arg) {
                throw new RuntimeException();
            }
        };

        Predicate<Integer> moreThanFiveOrException = GREATER_THAN_5.or(second);

        assertTrue(moreThanFiveOrException.apply(6)); // True 

        boolean f = false;
        try {
            moreThanFiveOrException.apply(2);
        }
        catch (RuntimeException e) {
            f = true;
        }
        assertTrue(f);
    }

    @Test
    public void testAnd() throws Exception {
        Predicate<Integer> moreThanFiveAndIsEven = GREATER_THAN_5.and(IS_EVEN);
        assertTrue(moreThanFiveAndIsEven.apply(6)); // True True
        assertFalse(moreThanFiveAndIsEven.apply(7)); // True False
        assertFalse(moreThanFiveAndIsEven.apply(4)); // False True
        assertFalse(moreThanFiveAndIsEven.apply(3)); // False False

        // lazy
        Predicate<Integer> second = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer arg) {
                throw new RuntimeException();
            }
        };

        Predicate<Integer> moreThanFiveAndException = GREATER_THAN_5.and(second);

        assertFalse(moreThanFiveAndException.apply(4)); // False

        boolean f = false;
        try {
            moreThanFiveAndException.apply(6);
        }
        catch (RuntimeException e) {
            f = true;
        }
        assertTrue(f);
    }

    @Test
    public void testNot() throws Exception {
        assertFalse(GREATER_THAN_5.not().apply(7));
        assertTrue(GREATER_THAN_5.not().apply(4));
        assertFalse(IS_EVEN.not().apply(4));
        assertTrue(GREATER_THAN_5.not().apply(5));
    }
}