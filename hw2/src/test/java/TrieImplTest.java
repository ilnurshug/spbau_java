
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.*;

public class TrieImplTest {

    @Test
    public void testSimple() {
        Trie trie = instance();

        assertTrue(trie.add("abc"));
        assertTrue(trie.contains("abc"));
        assertEquals(1, trie.size());
        assertEquals(1, trie.howManyStartsWithPrefix("abc"));
    }

    @Test
    public void testSimpleSerialization() throws IOException {
        Trie trie = instance();

        assertTrue(trie.add("abc"));
        assertTrue(trie.add("abcd"));
        assertTrue(trie.add("cde"));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ((StreamSerializable) trie).serialize(outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        Trie newTrie = instance();
        ((StreamSerializable) newTrie).deserialize(inputStream);

        assertTrue(newTrie.contains("abc"));
        assertTrue(newTrie.contains("abcd"));
        assertTrue(newTrie.contains("cde"));
    }

    @Test
    public void testSerialization() throws IOException {
        Trie t = instance();

        assertTrue(t.add("AbcD"));
        assertTrue(t.add("abcD"));
        assertTrue(t.add("abcDefg"));
        assertTrue(t.add("bcdef"));
        assertTrue(t.add(""));
        assertFalse(t.add(""));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ((StreamSerializable) t).serialize(outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ((StreamSerializable) t).deserialize(inputStream);

        assertTrue(t.contains(""));
        assertTrue(t.contains("AbcD"));
        assertTrue(t.contains("abcDefg"));
        assertTrue(t.contains("abcD"));
        assertTrue(t.contains("bcdef"));

        assertEquals(t.howManyStartsWithPrefix("A"), 1);
        assertEquals(t.howManyStartsWithPrefix("a"), 2);
        assertEquals(t.howManyStartsWithPrefix("bcdef"), 1);
    }

    @Test
    public void testDeserialize() throws IOException {
        Trie t = instance();

        assertTrue(t.add("AbcD"));
        assertTrue(t.add("abcD"));
        assertTrue(t.add("bcdef"));
        assertTrue(t.add(""));
        assertFalse(t.add(""));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ((StreamSerializable) t).serialize(outputStream);

        t.add("str");
        t.add("strs");
        t.remove("AbcD");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ((StreamSerializable) t).deserialize(inputStream);

        assertTrue(t.contains(""));
        assertTrue(t.contains("AbcD"));
        assertTrue(t.contains("abcD"));
        assertTrue(t.contains("bcdef"));
        assertFalse(t.contains("str"));
        assertFalse(t.contains("strs"));
    }

    @Test
    public void testEmptyTrie() throws IOException {
        Trie trie = instance();
        assertEquals(trie.size(), 0);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ((StreamSerializable) trie).serialize(outputStream);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        ((StreamSerializable) trie).deserialize(inputStream);

        assertEquals(trie.size(), 0);
    }

    @Test(expected=IOException.class)
    public void testSimpleSerializationFails() throws IOException {
        Trie trie = instance();

        assertTrue(trie.add("abc"));
        assertTrue(trie.add("cde"));

        OutputStream outputStream = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException("Fail");
            }
        };

        ((StreamSerializable) trie).serialize(outputStream);
    }

    public static Trie instance() {
        try {
            return (Trie) Class.forName("TrieImpl").newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
