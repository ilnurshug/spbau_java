/**
 * Created by ilnur on 20.03.16.
 */

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class CollectionsTest {
    private List<Integer> numbers = Arrays.asList(1, 3, 6, 12);
    private final int countOfEvens =  2;

    private List<String> strings = Arrays.asList(" 1 ", " 2 ", " 3 ", " 4 ");

    private Predicate<Integer> isEven = arg -> arg % 2 == 0;

    private Function2<String, String, String> concat = (lhs, rhs) -> lhs + rhs;

    private Function2<Integer, Integer, Integer> sub = (lhs, rhs) -> lhs - rhs;

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
    }

    @Test
    public void testFoldl() throws Exception {
        assertEquals("0  1  2  3  4 ",
                Collections.foldl(concat, "0 ", strings));

        assertEquals(-2, (int) Collections.foldl(sub, 20, numbers));
    }
}