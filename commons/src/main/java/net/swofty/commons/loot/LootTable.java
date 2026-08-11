package net.swofty.commons.loot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

public record LootTable<C, T>(String id, List<LootPool<C, T>> pools) {
    public LootTable {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Loot table id cannot be blank");
        pools = List.copyOf(pools);
    }

    public List<LootRoll<T>> roll(C context) {
        return roll(context, RandomGenerator.getDefault());
    }

    public List<LootRoll<T>> roll(C context, RandomGenerator random) {
        List<LootRoll<T>> results = new ArrayList<>();
        for (LootPool<C, T> pool : pools) results.addAll(pool.roll(context, random));
        return List.copyOf(results);
    }

    public static <T> Optional<LootRoll<T>> rollSingle(String id, T value, double chance) {
        LootTable<Void, T> table = new LootTable<>(id, List.of(new LootPool<>("chance",
                LootPool.Mode.INDEPENDENT, List.of(new LootEntry<>(id, value, chance)))));
        return table.roll(null).stream().findFirst();
    }
}
