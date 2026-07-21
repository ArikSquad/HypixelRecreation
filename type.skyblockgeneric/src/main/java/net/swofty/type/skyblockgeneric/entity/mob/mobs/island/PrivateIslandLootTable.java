package net.swofty.type.skyblockgeneric.entity.mob.mobs.island;

import lombok.NonNull;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;

import java.util.List;

final class PrivateIslandLootTable extends SkyBlockLootTable {
    private final List<LootRecord> records;

    PrivateIslandLootTable(LootRecord... records) {
        this.records = List.of(records);
    }

    @Override
    public @NonNull List<LootRecord> getLootTable() {
        return records;
    }

    @Override
    public @NonNull CalculationMode getCalculationMode() {
        return CalculationMode.CALCULATE_INDIVIDUAL;
    }
}
