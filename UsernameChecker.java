import java.util.*;

public class UsernameChecker {

    private HashMap<String, Integer> users = new HashMap<>();
    private HashMap<String, Integer> attemptFrequency = new HashMap<>();
    private int userIdCounter = 1;

    public void registerUser(String username) {
        users.put(username, userIdCounter++);
    }

    public boolean checkAvailability(String username) {
        attemptFrequency.put(username,
                attemptFrequency.getOrDefault(username, 0) + 1);
        return !users.containsKey(username);
    }

    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!users.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        String dotSuggestion = username.replace("_", ".");
        if (!users.containsKey(dotSuggestion)) {
            suggestions.add(dotSuggestion);
        }

        return suggestions;
    }

    public String getMostAttempted() {

        String mostAttempted = null;
        int max = 0;

        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                mostAttempted = entry.getKey();
            }
        }

        return mostAttempted + " (" + max + " attempts)";
    }

    public static void main(String[] args) {

        UsernameChecker checker = new UsernameChecker();

        checker.registerUser("john_doe");
        checker.registerUser("admin");
        checker.registerUser("alex");

        System.out.println(checker.checkAvailability("john_doe"));
        System.out.println(checker.checkAvailability("jane_smith"));

        System.out.println(checker.suggestAlternatives("john_doe"));

        checker.checkAvailability("admin");
        checker.checkAvailability("admin");
        checker.checkAvailability("admin");
        checker.checkAvailability("admin");

        System.out.println(checker.getMostAttempted());
    }
}