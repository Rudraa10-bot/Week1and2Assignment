import java.util.*;

public class PlagiarismDetector {

    private Map<String, Set<String>> ngramIndex = new HashMap<>();
    private Map<String, List<String>> documentNgrams = new HashMap<>();
    private int N = 3;

    public void addDocument(String docId, String text) {

        List<String> ngrams = generateNgrams(text);
        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {
            ngramIndex.computeIfAbsent(gram, k -> new HashSet<>()).add(docId);
        }
    }

    public void analyzeDocument(String docId) {

        List<String> grams = documentNgrams.get(docId);
        Map<String, Integer> matchCount = new HashMap<>();

        for (String gram : grams) {
            Set<String> docs = ngramIndex.getOrDefault(gram, new HashSet<>());

            for (String otherDoc : docs) {
                if (!otherDoc.equals(docId)) {
                    matchCount.put(otherDoc,
                            matchCount.getOrDefault(otherDoc, 0) + 1);
                }
            }
        }

        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {

            String otherDoc = entry.getKey();
            int matches = entry.getValue();

            int total = grams.size();
            double similarity = (matches * 100.0) / total;

            System.out.println("Matched with " + otherDoc +
                    " -> " + matches +
                    " n-grams, Similarity: " +
                    String.format("%.2f", similarity) + "%");
        }
    }

    private List<String> generateNgrams(String text) {

        String[] words = text.toLowerCase().split("\\s+");
        List<String> ngrams = new ArrayList<>();

        for (int i = 0; i <= words.length - N; i++) {

            String gram = "";
            for (int j = 0; j < N; j++) {
                gram += words[i + j] + " ";
            }

            ngrams.add(gram.trim());
        }

        return ngrams;
    }

    public static void main(String[] args) {

        PlagiarismDetector detector = new PlagiarismDetector();

        String essay1 = "data structures and algorithms are important for programming students";
        String essay2 = "data structures and algorithms are important for computer science students";
        String essay3 = "machine learning and artificial intelligence are transforming technology";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);
        detector.addDocument("essay_123.txt", essay3);

        detector.analyzeDocument("essay_092.txt");
    }
}