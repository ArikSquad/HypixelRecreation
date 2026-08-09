package net.swofty.type.skyblockgeneric.experimentation;

import net.minestom.server.item.Material;

public enum SuperPairItem {
    EXPERIENCE(ExperimentReward.EXPERIENCE),
    GRAND_EXPERIENCE_BOTTLE(ExperimentReward.GRAND_EXPERIENCE_BOTTLE),
    TITANIC_EXPERIENCE_BOTTLE(ExperimentReward.TITANIC_EXPERIENCE_BOTTLE),
    SCAVENGER_V(ExperimentReward.SCAVENGER_V),
    SHARPNESS_VI(ExperimentReward.SHARPNESS_VI),
    POWER_VI(ExperimentReward.POWER_VI),
    GIANT_KILLER_VI(ExperimentReward.GIANT_KILLER_VI),
    METAPHYSICAL_SERUM(ExperimentReward.METAPHYSICAL_SERUM);

    private final ExperimentReward reward;

    SuperPairItem(ExperimentReward reward) {
        this.reward = reward;
    }

    public Material material() {
        return reward.material();
    }

    public String displayName() { return reward.displayName(); }
    public ExperimentReward reward() { return reward; }
}
