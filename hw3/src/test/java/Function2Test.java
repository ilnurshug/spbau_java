/**
 * Created by ilnur on 20.03.16.
 */
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Function2Test {

    private Function2<Integer, Integer, Integer> mul = (lhs, rhs) -> lhs * rhs;

    private Function2<Integer, Integer, Integer> div = (lhs, rhs) -> lhs / rhs;

    private Function1<Object, String> toString = obj -> obj.toString();

    @Test
    public void testApply() throws Exception {
        assertEquals(6, (int) mul.apply(2, 3));
        assertEquals(2, (int) div.apply(10, 5));
    }

    @Test
    public void testCompose() throws Exception {
        assertEquals("30", mul.compose(toString).apply(6, 5));
        assertEquals("0", mul.compose(toString).apply(0, 0));
    }

    @Test
    public void testBind1() throws Exception {
        Function1<Integer, Integer> mul16 = mul.bind1(16);
        assertEquals(32, (int) mul16.apply(2));
        assertEquals(-16, (int) mul16.apply(-1));
    }

    @Test
    public void testBind2() throws Exception {
        Function1<Integer, Integer> mul16 = mul.bind2(16);
        assertEquals(32, (int) mul16.apply(2));
        assertEquals(-16, (int) mul16.apply(-1));

        Function1<Integer, Integer> div2 = div.bind2(2);
        assertEquals(5, (int) div2.apply(10));
    }

    @Test
    public void testCurry() throws Exception {
        Function1<Integer, Function1<Integer, Integer>> curryMul = mul.curry();
        assertEquals(30, (int) curryMul.apply(15).apply(2));
        assertEquals(-20, (int) curryMul.apply(4).apply(-5));

        Function1<Integer, Function1<Integer, Integer>> curryDiv = div.curry();
        assertEquals(10, (int) curryDiv.apply(100).apply(10));
    }
}
