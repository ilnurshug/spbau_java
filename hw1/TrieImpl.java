 
class CharacterHash implements HashFunction<Character> {
    public int call(Character i) {
        return i;
    }
}
 
class Node {
    
    public Node() {
        ch = new CharacterHash();
    
        children = new HashMap<Character, Node>(ch);
        isTerminal = false;
        termCount = 0;
    }
    
    public boolean isTerminal;
    public HashMap<Character, Node> children;
    public int termCount;
    
    private static CharacterHash ch;
}
 
public class TrieImpl implements Trie {
    
    public TrieImpl() {
        root = new Node();
    }
 
    public boolean add(String element) {
        if (contains(element)) return false;
        
        element += "$"; // hack for empty-string support
        
        Node cur = root;
        for (int i = 0; i < element.length(); i++) {
            cur.termCount++;
        
            char c = element.charAt(i);
            if (cur.children.contains(c) == false) {
                cur.children.insert(c, new Node());
            }
            cur = cur.children.get(c);
        }
        
        cur.isTerminal = true;
        return true;
    }
 
    public boolean contains(String element) {
        element += "$";
        
        Node cur = go(element);
        return cur == null ? false : cur.isTerminal;
    }
 
    public boolean remove(String element) {
        if (!contains(element)) return false;
        
        element += "$";
        
        Node cur = root;
        for (int i = 0; i < element.length(); i++) {
            Character c = element.charAt(i);
            cur.termCount--;
            
            Node nxt = cur.children.get(c);
            if (nxt.termCount <= 1) {
                cur.children.remove(c);
                break;
            }
            
            cur = nxt;
        }
        
        return true;
    }
 
    public int size() {
        return root != null ? root.termCount : 0;
    }
 
    public int howManyStartsWithPrefix(String prefix) {
        Node cur = go(prefix);
        
        return cur == null ? 0 : cur.termCount;
    }
    
    private Node go(String s)
    {
        Node cur = root;
        for (int i = 0; cur != null && i < s.length(); i++) {
            cur = cur.children.get(s.charAt(i));
        }
        
        return cur;
    }
    
    private Node root;
}

