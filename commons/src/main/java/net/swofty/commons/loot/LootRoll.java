package net.swofty.commons.loot;

public record LootRoll<T>(
        String poolId,
        String entryId,
        T value,
        double baseChance,
        double effectiveChance
) {
}
