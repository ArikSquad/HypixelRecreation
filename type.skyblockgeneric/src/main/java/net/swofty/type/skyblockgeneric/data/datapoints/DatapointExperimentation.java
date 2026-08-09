package net.swofty.type.skyblockgeneric.data.datapoints;

import net.swofty.commons.protocol.Serializer;
import net.swofty.type.skyblockgeneric.data.SkyBlockDatapoint;
import org.json.JSONObject;

public final class DatapointExperimentation extends SkyBlockDatapoint<DatapointExperimentation.PlayerExperimentation> {
    private static final Serializer<PlayerExperimentation> SERIALIZER = new Serializer<>() {
        @Override
        public String serialize(PlayerExperimentation value) {
            return new JSONObject().put("superpairs_bonus_clicks", value.superpairsBonusClicks())
                    .put("rng_meter_reward", value.rngMeterReward()).put("rng_meter_xp", value.rngMeterXp()).toString();
        }

        @Override
        public PlayerExperimentation deserialize(String json) {
            if (json == null || json.isBlank()) return new PlayerExperimentation(0, "TITANIC_EXPERIENCE_BOTTLE", 0);
            JSONObject value = new JSONObject(json);
            return new PlayerExperimentation(value.optInt("superpairs_bonus_clicks", 0),
                    value.optString("rng_meter_reward", "TITANIC_EXPERIENCE_BOTTLE"), value.optInt("rng_meter_xp", 0));
        }

        @Override
        public PlayerExperimentation clone(PlayerExperimentation value) {
            return new PlayerExperimentation(value.superpairsBonusClicks(), value.rngMeterReward(), value.rngMeterXp());
        }
    };

    public DatapointExperimentation(String key) {
        super(key, new PlayerExperimentation(0, "TITANIC_EXPERIENCE_BOTTLE", 0), SERIALIZER);
    }

    public record PlayerExperimentation(int superpairsBonusClicks, String rngMeterReward, int rngMeterXp) {
        public PlayerExperimentation {
            if (superpairsBonusClicks < 0) throw new IllegalArgumentException("Bonus clicks cannot be negative");
            if (rngMeterXp < 0) throw new IllegalArgumentException("RNG meter XP cannot be negative");
        }
    }
}
