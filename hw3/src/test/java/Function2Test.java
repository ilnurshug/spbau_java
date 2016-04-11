/**
 * Created by ilnur on 20.03.16.
 */
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class Function2Test {

    private static final Function2<Integer, Integer, Integer> MUL = (lhs, rhs) -> lhs * rhs;

    private static final Function2<Integer, Integer, Integer> DIV = (lhs, rhs) -> lhs / rhs;

    private static final Function1<Object, String> TO_STRING = obj -> obj.toString();

    private static final Function2<Derived, Derived, Derived> BLAH = (lhs, rhs) -> new Derived(lhs.f() + rhs.f());
    private static final Function1<Base, Integer> BLAH_2 = b -> b.f();

    @Test
    public void testApply() throws Exception {
        assertEquals(6, (int) MUL.apply(2, 3));
        assertEquals(2, (int) DIV.apply(10, 5));
    }

    @Test
    public void testCompose() throws Exception {
        assertEquals("30", MUL.compose(TO_STRING).apply(6, 5));
        assertEquals("0", MUL.compose(TO_STRING).apply(0, 0));

        // wildcard
        assertEquals(30, (int) BLAH.compose(BLAH_2).apply(new Derived(0), new Derived(0)));
    }

    @Test
    public void testBind1() throws Exception {
        Function1<Integer, Integer> mul16 = MUL.bind1(16);
        assertEquals(32, (int) mul16.apply(2));
        assertEquals(-16, (int) mul16.apply(-1));

        Function2<Base, Integer, Integer> foo = (b, i) -> b.f() + i;
        Function1<Integer, Integer> bar = foo.bind1(new Derived(10));
        assertEquals(30, (int) bar.apply(10));
    }

    @Test
    public void testBind2() throws Exception {
        Function1<Integer, Integer> mul16 = MUL.bind2(16);
        assertEquals(32, (int) mul16.apply(2));
        assertEquals(-16, (int) mul16.apply(-1));

        Function1<Integer, Integer> div2 = DIV.bind2(2);
        assertEquals(5, (int) div2.apply(10));

        Function2<Integer, Base, Integer> foo = (i, b) -> b.f() + i;
        Function1<Integer, Integer> bar = foo.bind2(new Derived(10));
        assertEquals(30, (int) bar.apply(10));
    }

    @Test
    public void testCurry() throws Exception {
        Function1<Integer, Function1<Integer, Integer>> curryMul = MUL.curry();
        assertEquals(30, (int) curryMul.apply(15).apply(2));
        assertEquals(-20, (int) curryMul.apply(4).apply(-5));

        Function1<Integer, Function1<Integer, Integer>> curryDiv = DIV.curry();
        assertEquals(10, (int) curryDiv.apply(100).apply(10));
    }
}
