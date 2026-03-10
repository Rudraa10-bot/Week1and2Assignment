import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEnd = false;
}

public class AutocompleteSystem {

    private TrieNode root = new TrieNode();
    private Map<String, Integer> frequency = new HashMap<>();

    public void addQuery(String query) {

        frequency.put(query, frequency.getOrDefault(query, 0) + 1);

        TrieNode node = root;

        for (char c : query.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }

        node.isEnd = true;
    }

    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return new ArrayList<>();
            }
            node = node.children.get(c);
        }

        List<String> results = new ArrayList<>();
        dfs(node, prefix, results);

        PriorityQueue<String> pq =
                new PriorityQueue<>((a, b) -> frequency.get(a) - frequency.get(b));

        for (String s : results) {
            pq.offer(s);
            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<String> top = new ArrayList<>();

        while (!pq.isEmpty()) {
            top.add(pq.poll());
        }

        Collections.reverse(top);
        return top;
    }

    private void dfs(TrieNode node, String current, List<String> results) {

        if (node.isEnd) {
            results.add(current);
        }

        for (char c : node.children.keySet()) {
            dfs(node.children.get(c), current + c, results);
        }
    }

    public void updateFrequency(String query) {
        addQuery(query);
    }

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.addQuery("java tutorial");
        system.addQuery("javascript");
        system.addQuery("java download");
        system.addQuery("java tutorial");
        system.addQuery("java 21 features");
        system.addQuery("java 21 features");
        system.addQuery("java 21 features");

        List<String> results = system.search("jav");

        int rank = 1;
        for (String r : results) {
            System.out.println(rank++ + ". " + r + " (" + system.frequency.get(r) + ")");
        }

        system.updateFrequency("java 21 features");

        System.out.println("\nUpdated Frequency:");
        System.out.println("java 21 features -> " + system.frequency.get("java 21 features"));
    }
}