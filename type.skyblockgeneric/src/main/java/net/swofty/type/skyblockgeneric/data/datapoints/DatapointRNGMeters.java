package net.swofty.type.skyblockgeneric.data.datapoints;

import net.swofty.commons.protocol.Serializer;
import net.swofty.type.skyblockgeneric.data.SkyBlockDatapoint;
import net.swofty.type.skyblockgeneric.rngmeter.RNGMeterState;
import net.swofty.type.skyblockgeneric.rngmeter.RNGMeterType;
import org.json.JSONObject;

import java.util.EnumMap;
import java.util.Map;

public final class DatapointRNGMeters extends SkyBlockDatapoint<Map<RNGMeterType, RNGMeterState>> {
    private static final Serializer<Map<RNGMeterType, RNGMeterState>> SERIALIZER = new Serializer<>() {
        @Override
        public String serialize(Map<RNGMeterType, RNGMeterState> value) {
            JSONObject meters = new JSONObject();
            value.forEach((type, state) -> meters.put(type.name(), new JSONObject()
                    .put("selected_reward", state.selectedReward())
                    .put("stored_xp", state.storedXp())));
            return meters.toString();
        }

        @Override
        public Map<RNGMeterType, RNGMeterState> deserialize(String json) {
            Map<RNGMeterType, RNGMeterState> meters = new EnumMap<>(RNGMeterType.class);
            if (json == null || json.isBlank()) return meters;

            JSONObject value = new JSONObject(json);
            for (String key : value.keySet()) {
                try {
                    JSONObject meter = value.getJSONObject(key);
                    meters.put(RNGMeterType.valueOf(key.toUpperCase()), new RNGMeterState(
                            meter.getString("selected_reward"),
                            meter.optDouble("stored_xp", 0)
                    ));
                } catch (RuntimeException ignored) {
                }
            }
            return meters;
        }

        @Override
        public Map<RNGMeterType, RNGMeterState> clone(Map<RNGMeterType, RNGMeterState> value) {
            return new EnumMap<>(value);
        }
    };

    public DatapointRNGMeters(String key) {
        super(key, new EnumMap<>(RNGMeterType.class), SERIALIZER);
    }
}
