import org.junit.*;
import static org.junit.Assert.*;

public class TrieImplTest {

    private static TrieImpl trie;
    private static final String[] strs = new String[]{"abC", "abCd", "abcdef", "bcd", "cdef", "cdeg"};
    
    @Before
    public void before() {
        trie = new TrieImpl();
        
        for (String s : strs) {
            trie.add(s);
        }
    }
    
    @Test
    public void testContains() {
        for (String s : strs) {
            assertTrue(trie.contains(s));
        }
    }
    
    @Test
    public void testAdd() {
        assertTrue(trie.add("CDEG"));
        assertFalse(trie.add("CDEG"));
        
        assertFalse(trie.add("cdeg"));
        
        trie.remove("cdeg");
        
        assertTrue(trie.add("cdeg"));
        
        assertTrue(trie.add(""));
        assertFalse(trie.add(""));
        
        trie.remove("");
        assertTrue(trie.add(""));
    }
    
    @Test
    public void testRemove() {
        for (int i = 0; i < strs.length; i++) {
            assertTrue(trie.remove(strs[i]));
            assertEquals(trie.size(), strs.length - i - 1);
        }
        
        for (int i = 0; i < strs.length; i++) {
            assertTrue(trie.add(strs[i]));
            assertEquals(trie.size(), i + 1);
        }
        
        assertFalse(trie.remove("cde"));
        assertTrue(trie.remove("cdeg"));
        assertFalse(trie.remove("cdeg"));
    }
    
    @Test
    public void testHowManyStartsWithPrefix() {
        assertEquals(trie.howManyStartsWithPrefix("ab"), 3);
        assertEquals(trie.howManyStartsWithPrefix("abc"), 1);
        assertEquals(trie.howManyStartsWithPrefix("abC"), 2);
        assertEquals(trie.howManyStartsWithPrefix("cd"), 2);
        assertEquals(trie.howManyStartsWithPrefix("b"), 1);
        assertEquals(trie.howManyStartsWithPrefix("e"), 0);
    }
    
    @Test
    public void testSize() {
        assertEquals(trie.size(), strs.length);
        trie.add("cdEg");
        assertEquals(trie.size(), strs.length + 1);
        trie.add("cdEg");
        assertEquals(trie.size(), strs.length + 1);
        
        trie.add("");
        assertEquals(trie.size(), strs.length + 2);
        trie.add("");
        assertEquals(trie.size(), strs.length + 2);
        
        trie.remove("");
        assertEquals(trie.size(), strs.length + 1);
        
        trie.remove("cdEg");
        assertEquals(trie.size(), strs.length);
        
        trie.remove("cdEg");
        assertEquals(trie.size(), strs.length);
    }
}