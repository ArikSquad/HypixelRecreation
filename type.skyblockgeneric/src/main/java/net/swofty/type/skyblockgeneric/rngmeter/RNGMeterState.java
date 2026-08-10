package net.swofty.type.skyblockgeneric.rngmeter;

public record RNGMeterState(String selectedReward, double storedXp) {
    public RNGMeterState {
        if (selectedReward == null || selectedReward.isBlank()) {
            throw new IllegalArgumentException("Selected reward cannot be blank");
        }
        if (storedXp < 0) throw new IllegalArgumentException("Stored XP cannot be negative");
    }
}
