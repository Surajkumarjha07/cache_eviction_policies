import java.util.HashMap;
import java.util.Map;

public class LFUCache {
    private final int capacity;
    private final Map<String, DoublyLinkedList.Node> keyNodeMap = new HashMap<>();
    private final Map<Integer, DoublyLinkedList> freqDLLMap = new HashMap<>();
    private int minFreq;

    LFUCache(int capacity) {
        this.capacity = capacity;
    }

    static class DoublyLinkedList {
        Node head;
        Node tail;

        static class Node {
            String key;
            String value;
            int freq = 1;
            Node prev;
            Node next;

            Node(String key, String value) {
                this.key = key;
                this.value = value;
            }

            @Override
            public String toString() {
                return "(v=" + value + ", f=" + freq + ")";
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

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            for (Node n = head; n != null; n = n.next) {
                sb.append(n.key).append(n.next != null ? " -> " : "");
            }
            return sb.append("]").toString();
        }
    }

    private void touch(DoublyLinkedList.Node node) {
        DoublyLinkedList old = freqDLLMap.get(node.freq);
        old.remove(node);
        if (old.head == null) {
            freqDLLMap.remove(node.freq);
            if (minFreq == node.freq) minFreq++;
        }
        node.freq++;
        freqDLLMap.computeIfAbsent(node.freq, f -> new DoublyLinkedList())
                .addToHead(node);
    }

    String get(String key) {
        DoublyLinkedList.Node node = keyNodeMap.get(key);

        if (node == null) {
            return "no node found for this key" + key;
        }
        touch(node);

        return node.value;
    }

    void put(String key, String value) {
        if (capacity <= 0) return;

        DoublyLinkedList.Node existing = keyNodeMap.get(key);
        if (existing != null) {
            existing.value = value;
            touch(existing);
            return;
        }

        if (keyNodeMap.size() >= capacity) {
            DoublyLinkedList linkedList = freqDLLMap.get(minFreq);

            DoublyLinkedList.Node evicted = linkedList.tail;
            linkedList.remove(evicted);
            keyNodeMap.remove(evicted.key);

            if (linkedList.head == null) {
                freqDLLMap.remove(minFreq);
            }
        }

        DoublyLinkedList.Node node = new DoublyLinkedList.Node(key, value);
        keyNodeMap.put(key, node);
        freqDLLMap.computeIfAbsent(1, f -> new DoublyLinkedList())
                .addToHead(node);
        minFreq = 1;
    }

    public static void main(String[] args) {
        LFUCache cache = new LFUCache(8);

        cache.put("101", "Orchid LFU 101");
        cache.put("102", "Orchid LFU 102");
        cache.put("103", "Orchid LFU 103");
        cache.put("104", "Orchid LFU 104");
        cache.put("105", "Orchid LFU 105");
        cache.put("106", "Orchid LFU 106");

        System.out.println("VALUE ============ " + cache.get("105"));
        System.out.println("VALUE ============ " + cache.get("101"));
        System.out.println("VALUE ============ " + cache.get("101"));
        System.out.println("VALUE ============ " + cache.get("101"));
        System.out.println("VALUE ============ " + cache.get("105"));

        System.out.println("FREQ MAP ========= " + cache.freqDLLMap);
        System.out.println("KEY MAP ======== " + cache.keyNodeMap);
    }
}
