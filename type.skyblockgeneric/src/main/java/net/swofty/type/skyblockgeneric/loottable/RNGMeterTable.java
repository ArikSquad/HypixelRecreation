package net.swofty.type.skyblockgeneric.loottable;

import lombok.NonNull;
import net.swofty.type.skyblockgeneric.rngmeter.RNGMeterType;

public abstract class RNGMeterTable extends SkyBlockLootTable {
    public abstract @NonNull RNGMeterType getRNGMeterType();
}
