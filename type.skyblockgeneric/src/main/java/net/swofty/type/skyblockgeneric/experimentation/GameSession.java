package net.swofty.type.skyblockgeneric.experimentation;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Accessors(fluent = true)
public final class GameSession {
    private final UUID playerId;
    private final ExperimentType type;
    private final ExperimentTier tier;
    private final long startTime;
    private GameState state;
    private int bestScore;

    public GameSession(UUID playerId, ExperimentType type, ExperimentTier tier) {
        this.playerId = playerId;
        this.type = type;
        this.tier = tier;
        this.startTime = System.currentTimeMillis();
        this.state = switch (type) {
            case SUPERPAIRS -> new SuperPairsState(tier.baseClicks());
            case CHRONOMATRON -> new ChronomatronState();
            case ULTRASEQUENCER -> new UltraSequencerState();
        };
    }

    public void state(GameState state) {
        this.state = state;
    }

    public void bestScore(int score) {
        bestScore = Math.max(bestScore, score);
    }

    public abstract static class GameState {
    }

    public enum GamePhase {
        READY,
        WATCHING,
        PLAYING,
        COMPLETE
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static final class ChronomatronState extends GameState {
        private final List<Integer> sequence = new ArrayList<>();
        private GamePhase phase = GamePhase.READY;
        private int inputIndex;
        private long lastInput;
        private long deadline;

    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static final class UltraSequencerState extends GameState {
        private final List<Integer> sequence = new ArrayList<>();
        private GamePhase phase = GamePhase.READY;
        private int inputIndex;
        private long deadline;

    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static final class SuperPairsState extends GameState {
        private final List<SuperPairItem> board = new ArrayList<>();
        private final Set<Integer> matchedTiles = new java.util.HashSet<>();
        private final int totalClicks;
        private int clicksRemaining;
        private int firstFlip = -1;
        private int mismatchFirst = -1;
        private int mismatchSecond = -1;
        private long mismatchUntil;

        public SuperPairsState(int totalClicks) {
            this.totalClicks = totalClicks;
            this.clicksRemaining = totalClicks;
        }

        public int pairsFound() {
            return matchedTiles.size() / 2;
        }
    }
}
