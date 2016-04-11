package sp;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.UncheckedIOException;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static sp.SecondPartTasks.*;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() {
        List<String> paths = Arrays.asList(
                "src/test/resources/file1",
                "src/test/resources/file2",
                "src/test/resources/file3"
        );
        List<String> result = Arrays.asList(
                "We've known each other for so long",
                "We've known each other for so long",
                "knownfff blow snow"
        );

        assertEquals(result, findQuotes(paths, "known"));
        assertEquals(Collections.emptyList(), findQuotes(paths, "blah blah"));

        boolean ex = false;
        try {
            findQuotes(Collections.singletonList("blah"), "blah");
        }
        catch (UncheckedIOException e) {
            ex = true;
        }
        assertTrue(ex);
    }

    @Test
    public void testPiDividedBy4() {
        double x = piDividedBy4();
        assertTrue(x >= 0.7);
        assertTrue(x <= 0.8);
    }

    @Test
    public void testFindPrinter() {
        Map<String, List<String>> w = new HashMap<String, List<String>>() {{
            put("a", Arrays.asList("a", "b", "c"));
            put("b", Arrays.asList("a", "b"));
            put("c", Arrays.asList("abcdef"));
        }};
        assertEquals("c", findPrinter(w));

        assertEquals(null, findPrinter(Collections.emptyMap()));
    }

    @Test
    public void testCalculateGlobalOrder() {
        Map<String, Integer> o1 = new HashMap<String, Integer>() {{
            put("a", 1);
            put("b", 2);
            put("c", 3);
            put("d", 4);
        }};

        Map<String, Integer> o2 = new HashMap<String, Integer>() {{
            put("a", 1);
            put("b", 2);
            put("c", 3);
            put("d", 4);
            put("e", 1);
        }};

        Map<String, Integer> o3 = new HashMap<String, Integer>() {{
            put("d", 4);
            put("e", 1);
        }};

        Map<String, Integer> o4 = new HashMap<String, Integer>() {{
            put("f", 1);
        }};

        Map<String, Integer> result = new HashMap<String, Integer>() {{
            put("a", 2);
            put("b", 4);
            put("c", 6);
            put("d", 12);
            put("e", 2);
            put("f", 1);
        }};

        assertEquals(result,
                calculateGlobalOrder(Arrays.asList(o1, o2, o3, o4))
        );

        assertEquals(Collections.emptyMap(),
                Collections.emptyMap());
    }
}