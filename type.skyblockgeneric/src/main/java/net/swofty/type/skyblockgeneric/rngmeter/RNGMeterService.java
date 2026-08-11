package net.swofty.type.skyblockgeneric.rngmeter;

import net.swofty.type.skyblockgeneric.data.SkyBlockDataHandler;
import net.swofty.type.skyblockgeneric.data.datapoints.DatapointRNGMeters;
import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;

import java.util.Map;

public final class RNGMeterService {
    private RNGMeterService() {
    }

    public static RNGMeterState get(SkyBlockPlayer player, RNGMeterDefinition definition) {
        return data(player).getOrDefault(definition.type(), defaultState(definition));
    }

    public static void select(SkyBlockPlayer player, RNGMeterDefinition definition, RNGMeterReward reward) {
        if (!definition.rewards().contains(reward)) {
            throw new IllegalArgumentException("Reward does not belong to " + definition.type());
        }
        RNGMeterState current = get(player, definition);
        set(player, definition.type(), new RNGMeterState(reward.id(), current.storedXp()));
    }

    public static ProgressResult addProgress(SkyBlockPlayer player, RNGMeterDefinition definition, double xp) {
        if (xp < 0) throw new IllegalArgumentException("RNG Meter XP cannot be negative");

        RNGMeterState current = get(player, definition);
        if (current.selectedReward().isBlank()) {
            double progress = current.storedXp() + xp;
            set(player, definition.type(), new RNGMeterState("", progress));
            return new ProgressResult(progress, false, null);
        }
        RNGMeterReward reward = definition.reward(current.selectedReward());
        double progress = current.storedXp() + xp;
        if (progress < reward.requiredXp()) {
            set(player, definition.type(), new RNGMeterState(reward.id(), progress));
            return new ProgressResult(progress, false, reward);
        }

        set(player, definition.type(), new RNGMeterState(reward.id(), reward.requiredXp()));
        player.sendMessage("<d><l>RNG METER! <f>Your " + definition.displayName()
                + " RNG Meter is full and will guarantee your next drop!");
        return new ProgressResult(reward.requiredXp(), true, reward);
    }

    public static void reset(SkyBlockPlayer player, RNGMeterDefinition definition) {
        RNGMeterState current = get(player, definition);
        set(player, definition.type(), new RNGMeterState("", current.storedXp()));
    }

    public static boolean selectedDropObtained(SkyBlockPlayer player, RNGMeterDefinition definition,
                                               RNGMeterReward obtainedReward) {
        RNGMeterState current = get(player, definition);
        if (!current.selectedReward().equalsIgnoreCase(obtainedReward.id())) return false;

        set(player, definition.type(), new RNGMeterState(current.selectedReward(), 0));
        return true;
    }

    public static boolean giveSelectedReward(SkyBlockPlayer player, RNGMeterDefinition definition) {
        RNGMeterState current = get(player, definition);
        if (current.selectedReward().isBlank()) return false;

        RNGMeterReward reward = definition.reward(current.selectedReward());
        if (current.storedXp() < reward.requiredXp()) return false;

        reward.give(player);
        set(player, definition.type(), new RNGMeterState(reward.id(), 0));
        return true;
    }

    public static boolean giveReward(SkyBlockPlayer player, RNGMeterDefinition definition,
                                     RNGMeterReward reward) {
        reward.give(player);
        return selectedDropObtained(player, definition, reward);
    }

    public static double applyDropRate(RNGMeterState state, RNGMeterReward reward, double baseDropRate) {
        if (reward.requiredXp() <= 0) return baseDropRate;
        double completion = Math.min(state.storedXp(), reward.requiredXp()) / reward.requiredXp();
        return baseDropRate * (1 + 2 * completion);
    }

    private static Map<RNGMeterType, RNGMeterState> data(SkyBlockPlayer player) {
        return datapoint(player).getValue();
    }

    private static void set(SkyBlockPlayer player, RNGMeterType type, RNGMeterState state) {
        DatapointRNGMeters datapoint = datapoint(player);
        Map<RNGMeterType, RNGMeterState> meters = data(player);
        meters.put(type, state);
        datapoint.setValue(meters);
    }

    private static DatapointRNGMeters datapoint(SkyBlockPlayer player) {
        return player.getSkyblockDataHandler()
                .get(SkyBlockDataHandler.Data.RNG_METERS, DatapointRNGMeters.class);
    }

    private static RNGMeterState defaultState(RNGMeterDefinition definition) {
        return new RNGMeterState(definition.defaultReward().id(), 0);
    }

    public record ProgressResult(double storedXp, boolean completed, RNGMeterReward reward) {
    }
}
