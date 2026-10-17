import java.util.HashMap;
import java.util.Map;

public class LRUCache {
    int capacity;
    Node head;
    Node tail;

    LRUCache(int capacity) {
        this.capacity = capacity;
    }

    Map<String, Node> map = new HashMap<>();

    static class Node {
        String key;
        String value;
        Node prev;
        Node next;

        Node(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    void addToHead(Node node) {
        if (head == null) {
            head = node;
            tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
    }

    void remove(Node node) {
        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }

        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }

        node.next = null;
        node.prev = null;
    }

    void traverse() {
        Node tempNode = head;

        while (tempNode != null) {
            System.out.println("node value ======== " + tempNode.value);
            tempNode = tempNode.next;
        }
    }

    void put(String key, String value) {
        if (capacity <= 0) return;

        Node existing = map.get(key);

        if (existing != null) {
            existing.value = value;
            remove(existing);
            addToHead(existing);
            return;
        }

        if (map.size() >= capacity) {
            Node evicted = tail;
            remove(evicted);
            map.remove(evicted.key);
        }

        Node node = new Node(key, value);
        addToHead(node);
        map.put(key, node);
    }

    String get(String key) {
        Node node = map.get(key);

        if (node == null) return "node not found for this key!";

        remove(node);
        addToHead(node);

        return node.value;
    }

    public static void main(String[] args) {
        LRUCache cache = new LRUCache(3);
        cache.put("101", "Orchid 101");
        cache.put("102", "Orchid 102");
        cache.put("103", "Orchid 103");

        cache.get("101");
        cache.put("104", "Orchid 104");

        cache.traverse();
    }
}
