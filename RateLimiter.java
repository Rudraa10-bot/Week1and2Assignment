import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class TokenBucket {
    int tokens;
    int maxTokens;
    double refillRate;
    long lastRefillTime;

    TokenBucket(int maxTokens, double refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    synchronized boolean allowRequest() {
        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        double tokensToAdd = (now - lastRefillTime) / 1000.0 * refillRate;

        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + (int) tokensToAdd);
            lastRefillTime = now;
        }
    }

    int getRemainingTokens() {
        refill();
        return tokens;
    }
}

public class RateLimiter {

    private Map<String, TokenBucket> clients = new ConcurrentHashMap<>();
    private int LIMIT = 1000;
    private double REFILL_RATE = 1000.0 / 3600.0;

    public String checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId, new TokenBucket(LIMIT, REFILL_RATE));

        TokenBucket bucket = clients.get(clientId);

        if (bucket.allowRequest()) {
            return "Allowed (" + bucket.getRemainingTokens() + " requests remaining)";
        } else {
            return "Denied (0 requests remaining)";
        }
    }

    public String getRateLimitStatus(String clientId) {

        TokenBucket bucket = clients.get(clientId);

        if (bucket == null) {
            return "Client not found";
        }

        int remaining = bucket.getRemainingTokens();
        int used = LIMIT - remaining;

        return "{used: " + used + ", limit: " + LIMIT + "}";
    }

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        String client = "abc123";

        for (int i = 0; i < 5; i++) {
            System.out.println(limiter.checkRateLimit(client));
        }

        System.out.println(limiter.getRateLimitStatus(client));
    }
}