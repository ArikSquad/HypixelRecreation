package net.swofty.type.skyblockgeneric.loottable;

import lombok.NonNull;
import net.swofty.type.skyblockgeneric.rngmeter.RNGMeterDefinition;
import net.swofty.type.skyblockgeneric.rngmeter.RNGMeterReward;
import net.swofty.type.skyblockgeneric.rngmeter.RNGMeterService;
import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;
import org.jetbrains.annotations.Nullable;

public abstract class RNGMeterTable extends SkyBlockLootTable {
    public abstract @NonNull RNGMeterDefinition getRNGMeterDefinition();

    public abstract @Nullable RNGMeterReward getRNGMeterReward(LootRecord record);

    @Override
    protected double adjustChance(SkyBlockPlayer player, LootRecord record, double chance) {
        RNGMeterReward reward = getRNGMeterReward(record);
        return reward == null ? chance
                : RNGMeterService.applyDropRate(player, getRNGMeterDefinition(), reward, chance);
    }

    @Override
    protected void onRolled(SkyBlockPlayer player, LootRecord record) {
        RNGMeterReward reward = getRNGMeterReward(record);
        if (reward != null) RNGMeterService.selectedDropObtained(player, getRNGMeterDefinition(), reward);
    }
}
