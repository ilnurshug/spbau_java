import org.junit.*;
import static org.junit.Assert.*;

public class TestTrie {

    private static TrieImpl trie;
    private static String[] str;
    private static int size;
    
    @Before
    public void before() {
        trie = new TrieImpl();
        
        size = 6;
        str = new String[size];
        str[0] = "abC";
        str[1] = "abCd";
        str[2] = "abcdef";
        str[3] = "bcd";
        str[4] = "cdef";
        str[5] = "cdeg";
        
        for (int i = 0; i < str.length; i++) {
            trie.add(str[i]);
        }
    }

    @After
    public void after() {
        trie = null;
        str = null;
    }

    @Test
    public void testContains() {
        System.out.println("testContains");
        
        Boolean f = true;
        for (int i = 0; i < str.length; i++) {
            f &= trie.contains(str[i]);
        }
        
        assertEquals(f, true);
    }
    
    @Test
    public void testAdd() {
        System.out.println("testAdd");
        
        assertEquals(trie.add("CDEG"), true);
        assertEquals(trie.add("CDEG"), false);
        
        assertEquals(trie.add("cdeg"), false);
        
        trie.remove("cdeg");
        
        assertEquals(trie.add("cdeg"), true);
        
        assertEquals(trie.add(""), true);
        assertEquals(trie.add(""), false);
        
        trie.remove("");
        assertEquals(trie.add(""), true);
    }
    
    @Test
    public void testRemove() {
        System.out.println("testRemove");
        
        for (int i = 0; i < size; i++) {
            assertEquals(trie.remove(str[i]), true);
            assertEquals(trie.size(), size - i - 1);
        }
        
        for (int i = 0; i < str.length; i++) {
            assertEquals(trie.add(str[i]), true);
            assertEquals(trie.size(), i + 1);
        }
        
        assertEquals(trie.remove("cde"), false);
        assertEquals(trie.remove("cdeg"), true);
        assertEquals(trie.remove("cdeg"), false);
    }
    
    @Test
    public void testHowManyStartsWithPrefix() {
        System.out.println("testHowManyStartsWithPrefix");
        
        assertEquals(trie.howManyStartsWithPrefix("ab"), 3);
        assertEquals(trie.howManyStartsWithPrefix("abc"), 1);
        assertEquals(trie.howManyStartsWithPrefix("abC"), 2);
        assertEquals(trie.howManyStartsWithPrefix("cd"), 2);
        assertEquals(trie.howManyStartsWithPrefix("b"), 1);
        assertEquals(trie.howManyStartsWithPrefix("e"), 0);
    }
    
    @Test
    public void testSize() {
        System.out.println("testSize");
        
        assertEquals(trie.size(), size);
        trie.add("cdEg");
        assertEquals(trie.size(), size + 1);
        trie.add("cdEg");
        assertEquals(trie.size(), size + 1);
        
        trie.add("");
        assertEquals(trie.size(), size + 2);
        trie.add("");
        assertEquals(trie.size(), size + 2);
        
        trie.remove("");
        assertEquals(trie.size(), size + 1);
        
        trie.remove("cdEg");
        assertEquals(trie.size(), size);
        
        trie.remove("cdEg");
        assertEquals(trie.size(), size);
    }
}