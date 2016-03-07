 
import java.util.*;
 
public class TrieImpl implements Trie {
    
    private static class Node {
        
        /*private static class CharacterHash implements HashFunction<Character> {
            public int call(Character i) {
                return i;
            }
        }*/
        
        public Node() {
            //ch = new CharacterHash();
        
            children = new HashMap<Character, Node>(/*ch*/);
            isTerminal = false;
            termCount = 0;
        }
        
        
        //private static CharacterHash ch;
        
        public boolean isTerminal;
        final public HashMap<Character, Node> children;
        public int termCount;
    }
    
    
    public TrieImpl() {
        root = new Node();
    }
 
    public boolean add(String element) {
        if (contains(element)) return false;
        
        Node cur = root;
        for (int i = 0; i < element.length(); i++) {
            cur.termCount++;
        
            char c = element.charAt(i);
            if (!cur.children.containsKey(c)) {
                cur.children.put(c, new Node());
            }
            cur = cur.children.get(c);
        }
        
        cur.isTerminal = true;
        cur.termCount++;
        
        return true;
    }
 
    public boolean contains(String element) {
        Node cur = get(element);
        return cur != null && cur.isTerminal;
    }
 
    public boolean remove(String element) {
        if (!contains(element)) return false;
        
        boolean f = false;
        Node cur = root;
        for (int i = 0; i < element.length(); i++) {
            char c = element.charAt(i);
            cur.termCount--;
            
            Node next = cur.children.get(c);
            if (next.termCount <= 1) {
                cur.children.remove(c);
                f = true;
                break;
            }
            
            cur = next;
        }
        
        if (!f) {
            cur.termCount--;
            cur.isTerminal = false;
        }
        
        return true;
    }
 
    public int size() {
        return root.termCount;
    }
 
    public int howManyStartsWithPrefix(String prefix) {
        Node cur = get(prefix);
        
        return cur == null ? 0 : cur.termCount;
    }
    
    private Node get(String s) {
        Node cur = root;
        for (int i = 0; cur != null && i < s.length(); i++) {
            cur = cur.children.get(s.charAt(i));
        }
        
        return cur;
    }
    
    final private Node root;
}

