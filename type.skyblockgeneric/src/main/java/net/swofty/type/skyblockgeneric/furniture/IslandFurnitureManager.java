package net.swofty.type.skyblockgeneric.furniture;

import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;
import net.swofty.type.skyblockgeneric.user.island.SkyBlockIsland;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class IslandFurnitureManager {
    private final SkyBlockIsland island;
    private final Map<FurnitureLimitPool, Integer> counts = new EnumMap<>(FurnitureLimitPool.class);

    public IslandFurnitureManager(SkyBlockIsland island) {
        this.island = island;
        Object stored = island.getDatabase().get("furniture_counts", Map.of());
        if (stored instanceof Map<?, ?> values) {
            values.forEach((key, value) -> {
                try {
                    counts.put(FurnitureLimitPool.valueOf(String.valueOf(key)), ((Number) value).intValue());
                } catch (IllegalArgumentException | ClassCastException ignored) {
                }
            });
        }
    }

    public synchronized boolean place(SkyBlockPlayer player, FurnitureLimitPool pool, String displayName) {
        int current = count(pool);
        if (current >= pool.limit()) {
            player.sendMessage("<c>You have the maximum number of this furniture allowed on your island. (<e>"
                    + current + "<c>/<e>" + pool.limit() + "<c>)");
            return false;
        }
        counts.put(pool, current + 1);
        save();
        player.sendMessage("<7>You placed an <e>" + displayName + "<7>. (<e>" + (current + 1) + "<7>/<e>" + pool.limit() + "<7>)");
        return true;
    }

    public synchronized void remove(SkyBlockPlayer player, FurnitureLimitPool pool, String displayName) {
        int remaining = Math.max(0, count(pool) - 1);
        counts.put(pool, remaining);
        save();
        player.sendMessage("<7>You removed an <e>" + displayName + "<7>. (<e>" + remaining + "<7>/<e>" + pool.limit() + "<7>)");
    }

    public int count(FurnitureLimitPool pool) {
        return counts.getOrDefault(pool, 0);
    }

    private void save() {
        Map<String, Integer> serialized = new ConcurrentHashMap<>();
        counts.forEach((pool, count) -> serialized.put(pool.name(), count));
        island.getDatabase().set("furniture_counts", serialized);
    }
}
