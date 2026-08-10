package net.swofty.type.skyblockgeneric.experimentation;

import net.swofty.type.skyblockgeneric.rngmeter.RNGMeterDefinition;
import net.swofty.type.skyblockgeneric.rngmeter.RNGMeterReward;
import net.swofty.type.skyblockgeneric.rngmeter.RNGMeterType;

import java.util.Arrays;
import java.util.List;

public final class ExperimentationRNGMeter implements RNGMeterDefinition {
    public static final ExperimentationRNGMeter INSTANCE = new ExperimentationRNGMeter();

    private final List<ExperimentReward> rewards = Arrays.stream(ExperimentReward.values())
            .filter(reward -> reward.requiredXp() > 0)
            .toList();

    private ExperimentationRNGMeter() {
    }

    @Override
    public RNGMeterType type() {
        return RNGMeterType.EXPERIMENTATION;
    }

    @Override
    public String displayName() {
        return "Experimentation Table";
    }

    @Override
    public List<? extends RNGMeterReward> rewards() {
        return rewards;
    }

    @Override
    public RNGMeterReward defaultReward() {
        return ExperimentReward.TITANIC_EXPERIENCE_BOTTLE;
    }
}
