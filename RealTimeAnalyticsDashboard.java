import java.util.*;

class Event {
    String url;
    String userId;
    String source;

    Event(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

public class RealTimeAnalyticsDashboard {

    private Map<String, Integer> pageViews = new HashMap<>();
    private Map<String, Set<String>> uniqueVisitors = new HashMap<>();
    private Map<String, Integer> trafficSources = new HashMap<>();

    public void processEvent(Event e) {

        pageViews.put(e.url, pageViews.getOrDefault(e.url, 0) + 1);

        uniqueVisitors
                .computeIfAbsent(e.url, k -> new HashSet<>())
                .add(e.userId);

        trafficSources.put(e.source,
                trafficSources.getOrDefault(e.source, 0) + 1);
    }

    public void getDashboard() {

        List<Map.Entry<String, Integer>> pages =
                new ArrayList<>(pageViews.entrySet());

        pages.sort((a, b) -> b.getValue() - a.getValue());

        System.out.println("Top Pages:");

        int limit = Math.min(10, pages.size());

        for (int i = 0; i < limit; i++) {
            String url = pages.get(i).getKey();
            int views = pages.get(i).getValue();
            int unique = uniqueVisitors.get(url).size();

            System.out.println((i + 1) + ". " + url +
                    " - " + views +
                    " views (" + unique + " unique)");
        }

        System.out.println("\nTraffic Sources:");

        int total = 0;
        for (int c : trafficSources.values()) {
            total += c;
        }

        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {

            double percent = (entry.getValue() * 100.0) / total;

            System.out.println(entry.getKey() +
                    ": " + String.format("%.2f", percent) + "%");
        }
    }

    public static void main(String[] args) throws Exception {

        RealTimeAnalyticsDashboard dashboard =
                new RealTimeAnalyticsDashboard();

        dashboard.processEvent(new Event("/article/breaking-news", "user_123", "google"));
        dashboard.processEvent(new Event("/article/breaking-news", "user_456", "facebook"));
        dashboard.processEvent(new Event("/sports/championship", "user_111", "google"));
        dashboard.processEvent(new Event("/sports/championship", "user_222", "direct"));
        dashboard.processEvent(new Event("/sports/championship", "user_111", "google"));
        dashboard.processEvent(new Event("/article/breaking-news", "user_999", "direct"));

        Thread.sleep(5000);

        dashboard.getDashboard();
    }
}