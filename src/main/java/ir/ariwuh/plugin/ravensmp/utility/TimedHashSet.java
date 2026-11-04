package ir.ariwuh.plugin.ravensmp.utility;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class TimedHashSet<K> {

    private final @NotNull Map<Instant, K> map = new TreeMap<>(); // Optimized: TreeMap for O(log n) cleanup
    private final @NotNull ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void add(@NotNull K key, long duration, @NotNull TimeUnit timeUnit) {
        val expirationTime = Instant.now().plusMillis(timeUnit.toMillis(duration));
        this.map.put(expirationTime, key); // Store with expiration time as key for efficient cleanup

        scheduleCleanup(duration, timeUnit);
    }

    public void remove(@NotNull K key) {
        val instantToRemove = findExpirationTime(key);
        if (instantToRemove != null) {
            this.map.remove(instantToRemove);
        }
    }

    public boolean contains(@NotNull K key) {
        return findExpirationTime(key) != null;
    }

    private Instant findExpirationTime(@NotNull K key) {
        // Find the first expiration time >= now that matches this key
        val instant = map.ceilingKey(Instant.now());
        if (instant != null && map.get(instant).equals(key)) {
            return instant;
        }
        // Linear scan for exact match (rare case)
        for (var entry : map.entrySet()) {
            if (entry.getValue().equals(key)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void cleanupExpiredEntries() {
        val instant = Instant.now();
        // Optimized: TreeMap allows efficient removal of expired entries
        this.map.headSet(instant, true).clear();
    }

    private void scheduleCleanup(long duration, @NotNull TimeUnit timeUnit) {
        this.scheduler.schedule(this::cleanupExpiredEntries, duration, timeUnit);
    }

}