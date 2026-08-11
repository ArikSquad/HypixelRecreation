package net.swofty.type.skyblockgeneric.rngmeter;

import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;

public interface RNGMeterReward {
    String id();

    String displayName();

    double requiredXp();

    void give(SkyBlockPlayer player);

    // I guess this is fine for now but it's quite stupid
    default String dropRarity() {
        if (requiredXp() >= 500_000) return "RNGesus Incarnate";
        if (requiredXp() >= 150_000) return "Rare";
        return "Extraordinary";
    }

    default double baseDropRate() {
        if (requiredXp() >= 2_500_000) return 0.004;
        if (requiredXp() >= 500_000) return 0.0263;
        if (requiredXp() >= 150_000) return 5.3191;
        return 1.0638;
    }
}
