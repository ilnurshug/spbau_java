 
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
 
public class TrieImpl implements Trie, StreamSerializable {
    
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

    @Override
    public void serialize(OutputStream out) throws IOException {
        dfsSerialize(root, true, ' ', new StringBuilder(), out);
    }

    @Override
    public void deserialize(InputStream in) throws IOException {
        root = new Node();

        int i;
        char c;
        StringBuilder str = new StringBuilder();

        while ((i = in.read()) != -1) {
            c = (char)i;
            if (c == '$') {
                add(str.toString());
                str = new StringBuilder();
            }
            else {
                str.append(c);
            }
        }
    }


    private Node get(String s) {
        Node cur = root;
        for (int i = 0; cur != null && i < s.length(); i++) {
            cur = cur.children.get(s.charAt(i));
        }

        return cur;
    }

    private void dfsSerialize(Node node, boolean isRoot, char symbol,
                              StringBuilder str, OutputStream out) throws IOException {
        if (!isRoot) {
            str.append(symbol);
        }

        if (node.isTerminal) {
            out.write((str.toString() + "$").getBytes());
        }

        for (char sym : node.children.keySet()) {
            Node child = node.children.get(sym);
            dfsSerialize(child, false, sym, str, out);
        }

        if (!isRoot) {
            str.deleteCharAt(str.length() - 1);
        }
    }


    private static class Node {

        public Node() {
            children = new HashMap<Character, Node>();
            isTerminal = false;
            termCount = 0;
        }

        public boolean isTerminal;
        public final HashMap<Character, Node> children;
        public int termCount;
    }

    private Node root;
}

