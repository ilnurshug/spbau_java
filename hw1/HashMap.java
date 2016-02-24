import java.util.LinkedList;
import java.util.ArrayList;

class Pair<K, V> {
    
    public Pair(K f, V s) {
        first = f;
        second = s;
    }
    
    K getFirst() {
        return first;
    }
    
    V getSecond() {
        return second;
    }
    
    void setFirst(K f) {
        first = f;
    }
    
    void setSecond(V s) {
        second = s;
    }
    
    private K first;
    private V second;
}

interface IHashMap<K, V> {

    void insert(K key, V value); 
    
    V get(K key);
    
    boolean contains(K key);
}

public class HashMap<K, V> implements IHashMap<K, V> {
    
    public HashMap(HashFunction<K> hashFunc) {
        hf = hashFunc;
        size = 16;
        count = 0;
        table = new ArrayList<LinkedList<Pair<K, V>>>(size);
        
        for (int i = 0; i < size; i++) {
            table.add(new LinkedList<Pair<K, V>>());
        }
    }
    
    public void insert(K key, V value) {
        int i = hf.call(key) % size;
        
        table.get(i).add(new Pair<K, V>(key, value));
        count++;
        
        if (count >= size) {
            expand();
        }
    }
    
    public V get(K key) {
        int i = hf.call(key) % size;
        
        LinkedList<Pair<K, V>> bucket = table.get(i);
        for (Pair<K, V> p : bucket) {
            if (p.getFirst() == key) return p.getSecond();
        }
        
        return null;
    }
    
    public void set(K key, V value) {
        int i = hf.call(key) % size;
        
        LinkedList<Pair<K, V>> bucket = table.get(i);
        boolean f = false;
        for (Pair<K, V> p : bucket) {
            if (p.getFirst() == key) {
                p.setSecond(value);
                f = true;
                break;
            }
        }
        
        if (!f) insert(key, value);
    }
    
    public void remove(K key) {
        int i = hf.call(key) % size;
        
        LinkedList<Pair<K, V>> bucket = table.get(i);
        int idx = 0;
        for (Pair<K, V> p : bucket) {
            if (p.getFirst() == key) break;
            idx++;
        }
        
        if (idx < bucket.size()) {
            bucket.remove(idx);
            count--;
        }
    }
    
    public boolean contains(K key) {
        return get(key) != null;
    }
    
    public int getCount() {
        return count;
    }
    
    private void expand() {
        size *= 2;
        
        ArrayList<LinkedList<Pair<K, V>>> nt = new ArrayList<LinkedList<Pair<K, V>>>(size);
        for (int i = 0; i < size; i++) {
            nt.add(new LinkedList<Pair<K, V>>());
        }
        
        for (int i = 0; i < table.size(); i++) {
            LinkedList<Pair<K, V>> bucket = table.get(i);
            for (Pair<K, V> p : bucket) {
                nt.get(hf.call(p.getFirst()) % size).add(p);
            }
        }
        
        table = nt;
    }
    
    private ArrayList<LinkedList<Pair<K, V>>> table;
    private HashFunction<K> hf;
    private int size;
    private int count;
}