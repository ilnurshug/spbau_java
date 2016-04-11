/**
 * Created by ilnur on 16.03.16.
 */

import org.junit.Test;

import static org.junit.Assert.*;

public class Function1Test {
    private static final Function1<String, Integer> GET_STRING_LENGTH = s -> s.length();

    private static final Function1<Integer, Integer> MUL_2 = i -> i * 2;

    private static final Function1<Object, String> TO_STRING = obj -> obj.toString();

    @Test
    public void testApply() throws Exception {
        assertEquals(3, (int) GET_STRING_LENGTH.apply("abc"));
        assertEquals(1, (int) GET_STRING_LENGTH.apply("a"));
        assertEquals(0, (int) GET_STRING_LENGTH.apply(""));
        assertEquals(0, (int) MUL_2.apply(0));
        assertEquals(32, (int) MUL_2.apply(16));
    }

    @Test
    public void testCompose() throws Exception {
        Function1<String, Integer> getDoubleLength = GET_STRING_LENGTH.compose(MUL_2);
        assertEquals(6, (int) getDoubleLength.apply("abc"));
        assertEquals(2, (int) getDoubleLength.apply("a"));
        assertEquals(0, (int) getDoubleLength.apply(""));
        Function1<Integer, String> getSquareSting = MUL_2.compose(TO_STRING);
        assertEquals("8", getSquareSting.apply(4));
        assertEquals("2", getSquareSting.apply(1));
        assertEquals("0", getSquareSting.apply(0));

        Function1<Integer, Derived> foo = i -> new Derived(i);
        Function1<Base, Integer> bar = b -> b.f();
        Function1<Integer, Integer> c = foo.compose(bar);
        assertEquals(20, (int)c.apply(10));
    }
}

class Base {
    protected int a;

    Base(int a) {
        this.a = a;
    }

    public int f() {
        return a;
    }
}

class Derived extends Base {

    Derived(int a) {
        super(a + 10);
    }

    @Override
    public int f() {
        return a;
    }
}
