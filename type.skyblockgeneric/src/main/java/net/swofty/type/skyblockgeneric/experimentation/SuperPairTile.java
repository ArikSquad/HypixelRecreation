package net.swofty.type.skyblockgeneric.experimentation;

public record SuperPairTile(String pairId, ExperimentReward reward, SuperPairItem item, int amount) {
    public SuperPairTile {
        if (pairId == null || pairId.isBlank()) throw new IllegalArgumentException("Pair id cannot be blank");
        if (reward == null && (item == null || !item.isPowerUp())) {
            throw new IllegalArgumentException("A tile must contain a reward or a power-up");
        }
        if (reward != null && item != null && item.isPowerUp()) {
            throw new IllegalArgumentException("Reward tiles must contain a reward item");
        }
        if (amount < 1) throw new IllegalArgumentException("Pair amount must be positive");
        if (isPowerUp(item) && amount != 1) throw new IllegalArgumentException("Power-ups cannot have quantities");
    }

    public boolean isPowerUp() {
        return isPowerUp(item);
    }

    private static boolean isPowerUp(SuperPairItem item) {
        return item != null && item.isPowerUp();
    }
}
