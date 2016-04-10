/**
 * Created by ilnur on 16.03.16.
 */

import org.junit.Test;

import static org.junit.Assert.*;

public class Function1Test {
    private static final Function1<String, Integer> getStringLength = s -> s.length();

    private static final Function1<Integer, Integer> mul2 = i -> i * 2;

    private static final Function1<Object, String> toString = obj -> obj.toString();

    @Test
    public void testApply() throws Exception {
        assertEquals(3, (int) getStringLength.apply("abc"));
        assertEquals(1, (int) getStringLength.apply("a"));
        assertEquals(0, (int) getStringLength.apply(""));
        assertEquals(0, (int) mul2.apply(0));
        assertEquals(32, (int) mul2.apply(16));
    }

    @Test
    public void testCompose() throws Exception {
        Function1<String, Integer> getDoubleLength = getStringLength.compose(mul2);
        assertEquals(6, (int) getDoubleLength.apply("abc"));
        assertEquals(2, (int) getDoubleLength.apply("a"));
        assertEquals(0, (int) getDoubleLength.apply(""));
        Function1<Integer, String> getSquareSting = mul2.compose(toString);
        assertEquals("8", getSquareSting.apply(4));
        assertEquals("2", getSquareSting.apply(1));
        assertEquals("0", getSquareSting.apply(0));

        Function1<Integer, Derived> foo = i -> new Derived(i);
        Function1<Base, Integer> bar = b -> b.f();
        Function1<Integer, Integer> c = foo.compose(bar);
        assertEquals(20, (int)c.apply(10));
    }

    private class Base {
        protected int a;

        Base(int a) {
            this.a = a;
        }

        public int f() {
            return a;
        }
    }

    private class Derived extends Base {

        Derived(int a) {
            super(a);
        }

        @Override
        public int f() {
            return a + 10;
        }
    }
}
