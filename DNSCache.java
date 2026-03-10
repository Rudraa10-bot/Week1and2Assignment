import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    DNSEntry(String domain, String ipAddress, long ttl) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + ttl * 1000;
    }

    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class DNSCache {

    private int capacity;
    private Map<String, DNSEntry> cache;
    private LinkedHashMap<String, DNSEntry> lru;
    private int hits = 0;
    private int misses = 0;

    public DNSCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.lru = new LinkedHashMap<>(capacity, 0.75f, true);
    }

    public String resolve(String domain) {

        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;
                lru.get(domain);
                return "Cache HIT → " + entry.ipAddress;
            } else {
                cache.remove(domain);
                lru.remove(domain);
            }
        }

        misses++;
        String ip = queryUpstreamDNS(domain);
        DNSEntry newEntry = new DNSEntry(domain, ip, 300);

        if (cache.size() >= capacity) {
            String oldest = lru.keySet().iterator().next();
            cache.remove(oldest);
            lru.remove(oldest);
        }

        cache.put(domain, newEntry);
        lru.put(domain, newEntry);

        return "Cache MISS → " + ip;
    }

    private String queryUpstreamDNS(String domain) {
        Random r = new Random();
        return "172.217.14." + r.nextInt(255);
    }

    public String getCacheStats() {
        int total = hits + misses;
        double hitRate = total == 0 ? 0 : (hits * 100.0) / total;
        return "Hit Rate: " + String.format("%.2f", hitRate) + "%";
    }

    public static void main(String[] args) throws Exception {

        DNSCache dns = new DNSCache(5);

        System.out.println(dns.resolve("google.com"));
        System.out.println(dns.resolve("google.com"));

        Thread.sleep(2000);

        System.out.println(dns.resolve("openai.com"));
        System.out.println(dns.resolve("google.com"));

        System.out.println(dns.getCacheStats());
    }
}