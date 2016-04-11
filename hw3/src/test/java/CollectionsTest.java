/**
 * Created by ilnur on 20.03.16.
 */

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class CollectionsTest {
    private static final List<Integer> NUMBERS = Arrays.asList(1, 3, 6, 12);
    private static final int COUNT_OF_EVENS =  2;

    private static final List<String> STRINGS = Arrays.asList(" 1 ", " 2 ", " 3 ", " 4 ");

    private static final Predicate<Integer> IS_EVEN = arg -> arg % 2 == 0;

    private static final Function2<String, String, String> CONCAT = (lhs, rhs) -> lhs + rhs;

    private static final Function2<String, Integer, Integer> STRING_TO_INT_ADD = (lhs, rhs) -> Integer.parseInt(lhs.trim()) + rhs;
    private static final Function2<Integer, String, Integer> STRING_TO_INT_ADD_2 = (lhs, rhs) -> lhs + Integer.parseInt(rhs.trim());

    private static final Function2<Integer, Integer, Integer> SUB = (lhs, rhs) -> lhs - rhs;

    private static final Function2<Base, Integer, Integer> WILD_TEST = (lhs, rhs) -> lhs.f() + rhs;
    private static final Function2<Integer, Base, Integer> WILD_TEST_2 = (lhs, rhs) -> rhs.f() + lhs;
    private final List<Derived> DERIVED = Arrays.asList(new Derived(), new Derived());

    private final Predicate<Base> IS_EVEN_2 = b -> b.getB() % 2 == 0;
    private final List<Derived> DERIVED_NUMS = Arrays.asList(new Derived(1), new Derived(3), new Derived(6), new Derived(12));

    @Test
    public void testMap() throws Exception {
        Collection<Integer> map = Collections.map(NUMBERS, arg -> arg * arg);

        Integer[] result = map.toArray(new Integer[map.size()]);

        assertEquals(result.length, NUMBERS.size());

        int i = 0;
        for (int val : NUMBERS) {
            assertEquals(val * val, (int) result[i++]);
        }

        // wildcard
        Function1<Base, Integer> f = Base::f;
        Collection<Integer> map2 = Collections.map(DERIVED, f);
        Integer[] result2 = map2.toArray(new Integer[map2.size()]);

        assertEquals(result2.length, DERIVED.size());
        i = 0;
        for (Derived d : DERIVED) {
            assertEquals(d.f(), (int) result2[i++]);
        }
    }

    @Test
    public void testFilter() throws Exception {
        Collection<Integer> map = Collections.filter(NUMBERS, IS_EVEN);
        Integer[] result = map.toArray(new Integer[map.size()]);

        assertEquals(result.length, COUNT_OF_EVENS);
        for (int val : result) {
            assertTrue(val % 2 == 0);
        }

        // wildcard
        Collection<Derived> map2 = Collections.filter(DERIVED_NUMS, IS_EVEN_2);
        Derived[] res2 = map2.toArray(new Derived[map2.size()]);
        assertEquals(res2.length, COUNT_OF_EVENS);
        for (Derived d : res2) {
            assertTrue(d.getB() % 2 == 0);
        }
    }

    @Test
    public void testTakeWhile() throws Exception {
        Collection<Integer> map = Collections.takeWhile(NUMBERS, IS_EVEN.not());
        Integer[] result = map.toArray(new Integer[map.size()]);

        assertEquals(result.length, NUMBERS.size() - COUNT_OF_EVENS);
        for (int val : result) {
            assertTrue(val % 2 != 0);
        }

        // wildcard
        Collection<Derived> map2 = Collections.takeWhile(DERIVED_NUMS, IS_EVEN_2.not());
        Derived[] result2 = map2.toArray(new Derived[map2.size()]);

        assertEquals(result2.length, DERIVED_NUMS.size() - COUNT_OF_EVENS);
        for (Derived d: result2) {
            assertTrue(d.getB() % 2 != 0);
        }
    }

    @Test
    public void testTakeUnless() throws Exception {
        Collection<Integer> map = Collections.takeUnless(NUMBERS, IS_EVEN);
        Integer[] result = map.toArray(new Integer[map.size()]);

        assertEquals(result.length, NUMBERS.size() - COUNT_OF_EVENS);
        for (int val : result) {
            assertTrue(val % 2 != 0);
        }

        // wildcard
        Collection<Derived> map2 = Collections.takeUnless(DERIVED_NUMS, IS_EVEN_2);
        Derived[] result2 = map2.toArray(new Derived[map2.size()]);

        assertEquals(result2.length, DERIVED_NUMS.size() - COUNT_OF_EVENS);
        for (Derived d: result2) {
            assertTrue(d.getB() % 2 != 0);
        }
    }

    @Test
    public void testFoldr() throws Exception {
        assertEquals(" 1  2  3  4 0 ",
                Collections.foldr(CONCAT, "0 ", STRINGS));

        assertEquals(12, (int) Collections.foldr(SUB, 20, NUMBERS));

        assertEquals(10, (int) Collections.foldr(STRING_TO_INT_ADD, 0, STRINGS));

        assertEquals(40, (int) Collections.foldr(WILD_TEST, 0, DERIVED));
    }

    @Test
    public void testFoldl() throws Exception {
        assertEquals("0  1  2  3  4 ",
                Collections.foldl(CONCAT, "0 ", STRINGS));

        assertEquals(-2, (int) Collections.foldl(SUB, 20, NUMBERS));

        assertEquals(10, (int) Collections.foldl(STRING_TO_INT_ADD_2, 0, STRINGS));

        assertEquals(40, (int) Collections.foldl(WILD_TEST_2, 0, DERIVED));
    }

    private class Base {
        public int f() {
            return 10;
        }

        public int getB() {
            return 0;
        }
    }

    private class Derived extends Base {
        Derived() {}

        Derived(int param) {
            b = param;
        }

        @Override
        public int f() {
            return 20;
        }

        @Override
        public int getB() {
            return b;
        }

        private int b;
    }
}