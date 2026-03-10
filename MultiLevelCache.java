import java.util.*;

class VideoData {
    String videoId;
    String content;

    VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private int capacity;

    LRUCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}

public class MultiLevelCache {

    private LRUCache<String, VideoData> L1 = new LRUCache<>(10000);
    private LRUCache<String, VideoData> L2 = new LRUCache<>(100000);
    private Map<String, VideoData> database = new HashMap<>();

    private int l1Hits = 0;
    private int l2Hits = 0;
    private int l3Hits = 0;

    public MultiLevelCache() {

        database.put("video_123", new VideoData("video_123", "Video Content A"));
        database.put("video_456", new VideoData("video_456", "Video Content B"));
        database.put("video_999", new VideoData("video_999", "Video Content C"));
    }

    public VideoData getVideo(String videoId) {

        if (L1.containsKey(videoId)) {
            l1Hits++;
            System.out.println("L1 Cache HIT");
            return L1.get(videoId);
        }

        if (L2.containsKey(videoId)) {
            l2Hits++;
            System.out.println("L2 Cache HIT - Promoted to L1");

            VideoData data = L2.get(videoId);
            L1.put(videoId, data);

            return data;
        }

        if (database.containsKey(videoId)) {
            l3Hits++;
            System.out.println("L3 Database HIT - Added to L2");

            VideoData data = database.get(videoId);
            L2.put(videoId, data);

            return data;
        }

        System.out.println("Video not found");
        return null;
    }

    public void updateVideo(String videoId, String newContent) {

        VideoData data = new VideoData(videoId, newContent);

        database.put(videoId, data);
        L1.remove(videoId);
        L2.remove(videoId);

        System.out.println("Cache invalidated for " + videoId);
    }

    public void getStatistics() {

        int total = l1Hits + l2Hits + l3Hits;

        if (total == 0) return;

        System.out.println("L1 Hit Rate: " + (l1Hits * 100.0 / total) + "%");
        System.out.println("L2 Hit Rate: " + (l2Hits * 100.0 / total) + "%");
        System.out.println("L3 Hit Rate: " + (l3Hits * 100.0 / total) + "%");
    }

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        cache.getVideo("video_123");
        cache.getVideo("video_123");
        cache.getVideo("video_999");
        cache.getVideo("video_999");

        cache.updateVideo("video_123", "Updated Video Content");

        cache.getVideo("video_123");

        cache.getStatistics();
    }
}