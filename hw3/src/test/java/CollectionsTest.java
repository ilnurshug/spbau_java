/**
 * Created by ilnur on 20.03.16.
 */

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.*;

import static org.junit.Assert.*;

public class CollectionsTest {
    private static final List<Integer> numbers = Arrays.asList(1, 3, 6, 12);
    private static final int countOfEvens =  2;

    private static final List<String> strings = Arrays.asList(" 1 ", " 2 ", " 3 ", " 4 ");

    private static final Predicate<Integer> isEven = arg -> arg % 2 == 0;

    private static final Function2<String, String, String> concat = (lhs, rhs) -> lhs + rhs;

    private static final Function2<String, Integer, Integer> stringToIntAdd = (lhs, rhs) -> Integer.parseInt(lhs.trim()) + rhs;
    private static final Function2<Integer, String, Integer> stringToIntAdd2 = (lhs, rhs) -> lhs + Integer.parseInt(rhs.trim());

    private static final Function2<Integer, Integer, Integer> sub = (lhs, rhs) -> lhs - rhs;

    private static final Function2<Base, Integer, Integer> wildTest = (lhs, rhs) -> lhs.f() + rhs;
    private static final Function2<Integer, Base, Integer> wildTest2 = (lhs, rhs) -> rhs.f() + lhs;
    private final List<Derived> derived = Arrays.asList(new Derived(), new Derived());

    @Test
    public void testMap() throws Exception {
        Collection<Integer> map = Collections.map(numbers, arg -> arg * arg);

        Integer[] result = map.toArray(new Integer[map.size()]);
        int i = 0;
        for (int val : numbers) {
            assertEquals(val * val, (int) result[i++]);
        }
    }

    @Test
    public void testFilter() throws Exception {
        Collection<Integer> map = Collections.filter(numbers, isEven);
        Integer[] result = map.toArray(new Integer[map.size()]);

        assertEquals(result.length, countOfEvens);
        for (int val : result) {
            assertTrue(val % 2 == 0);
        }
    }

    @Test
    public void testTakeWhile() throws Exception {
        Collection<Integer> map = Collections.takeWhile(numbers, isEven.not());
        Integer[] result = map.toArray(new Integer[map.size()]);

        assertEquals(result.length, numbers.size() - countOfEvens);
        for (int val : result) {
            assertTrue(val % 2 != 0);
        }
    }

    @Test
    public void testTakeUnless() throws Exception {
        Collection<Integer> map = Collections.takeUnless(numbers, isEven);
        Integer[] result = map.toArray(new Integer[map.size()]);

        assertEquals(result.length, numbers.size() - countOfEvens);
        for (int val : result) {
            assertTrue(val % 2 != 0);
        }
    }

    @Test
    public void testFoldr() throws Exception {
        assertEquals(" 1  2  3  4 0 ",
                Collections.foldr(concat, "0 ", strings));

        assertEquals(12, (int) Collections.foldr(sub, 20, numbers));

        assertEquals(10, (int) Collections.foldr(stringToIntAdd, 0, strings));

        assertEquals(40, (int) Collections.foldr(wildTest, 0, derived));
    }

    @Test
    public void testFoldl() throws Exception {
        assertEquals("0  1  2  3  4 ",
                Collections.foldl(concat, "0 ", strings));

        assertEquals(-2, (int) Collections.foldl(sub, 20, numbers));

        assertEquals(10, (int) Collections.foldl(stringToIntAdd2, 0, strings));

        assertEquals(40, (int) Collections.foldl(wildTest2, 0, derived));
    }

    private class Base {
        public int f() {
            return 10;
        }
    }

    private class Derived extends Base {
        @Override
        public int f() {
            return 20;
        }
    }
}