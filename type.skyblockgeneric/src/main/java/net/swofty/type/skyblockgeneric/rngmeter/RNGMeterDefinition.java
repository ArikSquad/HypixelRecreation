package net.swofty.type.skyblockgeneric.rngmeter;

import java.util.List;

public interface RNGMeterDefinition {
    RNGMeterType type();

    String displayName();

    List<? extends RNGMeterReward> rewards();

    RNGMeterReward defaultReward();

    default RNGMeterReward reward(String id) {
        return rewards().stream()
                .filter(reward -> reward.id().equalsIgnoreCase(id))
                .map(reward -> (RNGMeterReward) reward)
                .findFirst()
                .orElse(defaultReward());
    }
}
