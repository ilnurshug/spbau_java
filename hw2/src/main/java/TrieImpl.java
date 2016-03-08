 
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
 
public class TrieImpl implements Trie, StreamSerializable {
    
    public TrieImpl() {
        root = new Node(' ', null);
    }

    public boolean add(String element) {
        if (contains(element)) return false;
        
        Node cur = root;
        for (int i = 0; i < element.length(); i++) {
            cur.termCount++;
        
            char c = element.charAt(i);
            if (!cur.children.containsKey(c)) {
                cur.children.put(c, new Node(c, cur));
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

        Node cur = root;
        for (int i = 0; i < element.length(); i++) {
            char c = element.charAt(i);
            cur.termCount--;
            
            Node next = cur.children.get(c);
            if (next.termCount <= 1) {
                cur.children.remove(c);
                return true;
            }
            
            cur = next;
        }

        cur.termCount--;
        cur.isTerminal = false;
        
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

    @Override
    public void serialize(OutputStream out) throws IOException {
        LinkedList<Node> t = new LinkedList<Node>();
        getAllTermNodes(root, t);

        for (Node r : t) {
            String s = getString(r) + "$";
            out.write(s.getBytes());
        }
    }

    @Override
    public void deserialize(InputStream in) throws IOException {
        int i;
        char c;
        String s = "";

        while ((i = in.read()) != -1) {
            c = (char)i;
            if (c == '$') {
                add(s);
                s = "";
            }
            else {
                s += c;
            }
        }
    }

    private static void getAllTermNodes(Node r, LinkedList<Node> t) {
        if (r.isTerminal) t.addLast(r);

        for (Node c : r.children.values()) {
            getAllTermNodes(c, t);
        }
    }

    private static String getString(Node r) {
        if (r.p == null) {
            return "";
        }
        else {
            return getString(r.p) + r.p_sym;
        }
    }

    private static class Node {

        public Node(char p_symbol, Node parent) {
            children = new HashMap<Character, Node>();
            isTerminal = false;
            termCount = 0;

            p_sym = p_symbol;
            p = parent;
        }

        public boolean isTerminal;
        public final HashMap<Character, Node> children;
        public int termCount;

        public char p_sym;
        public Node p;
    }

    private final Node root;
}

