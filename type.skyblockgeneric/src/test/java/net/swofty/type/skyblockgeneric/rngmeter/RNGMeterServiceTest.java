package net.swofty.type.skyblockgeneric.rngmeter;

import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RNGMeterServiceTest {
    private static final RNGMeterReward REWARD = new RNGMeterReward() {
        @Override
        public String id() {
            return "reward";
        }

        @Override
        public String displayName() {
            return "Reward";
        }

        @Override
        public double requiredXp() {
            return 1_000;
        }

        @Override
        public void give(SkyBlockPlayer player) {
        }
    };

    @Test
    void scalesDropRateUpToThreeTimesBaseChance() {
        assertEquals(1, RNGMeterService.applyDropRate(new RNGMeterState("reward", 0), REWARD, 1));
        assertEquals(2, RNGMeterService.applyDropRate(new RNGMeterState("reward", 500), REWARD, 1));
        assertEquals(3, RNGMeterService.applyDropRate(new RNGMeterState("reward", 1_000), REWARD, 1));
        assertEquals(3, RNGMeterService.applyDropRate(new RNGMeterState("reward", 2_000), REWARD, 1));
    }
}
