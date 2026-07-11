package ir.ariwuh.plugin.ravensmp.utility;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class TimedHashSet<K> {

    private final @NotNull Map<K, Instant> map = new HashMap<>();
    private final @NotNull ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void add(@NotNull K key, long duration, @NotNull TimeUnit timeUnit) {
        val expirationTime = Instant.now().plusMillis(timeUnit.toMillis(duration));
        this.map.put(key, expirationTime);

        scheduleCleanup(duration, timeUnit);
    }

    public void remove(@NotNull K key) {
        this.map.remove(key);
    }

    public boolean contains(@NotNull K key) {
        return this.map.containsKey(key);
    }

    private void cleanupExpiredEntries() {
        val instant = Instant.now();
        this.map.entrySet()
                .removeIf(entry -> entry.getValue().isBefore(instant));
    }

    private void scheduleCleanup(long duration, @NotNull TimeUnit timeUnit) {
        this.scheduler.schedule(this::cleanupExpiredEntries, duration, timeUnit);
    }

}