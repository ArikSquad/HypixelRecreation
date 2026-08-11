package net.swofty.type.skyblockgeneric.data.datapoints;

import net.swofty.commons.protocol.Serializer;
import net.swofty.type.skyblockgeneric.data.SkyBlockDatapoint;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class DatapointExperimentation extends SkyBlockDatapoint<DatapointExperimentation.PlayerExperimentation> {
    public static final long CHARGE_RENEWAL_PERIOD_MILLIS = 24L * 60L * 60L * 1_000L;
    private static final Serializer<PlayerExperimentation> SERIALIZER = new Serializer<>() {
        @Override
        public String serialize(PlayerExperimentation value) {
            JSONObject json = new JSONObject()
                    .put("superpairs_bonus_clicks", value.superpairsBonusClicks())
                    .put("charges", value.charges())
                    .put("next_charge_renewal_at", value.nextChargeRenewalAt())
                    .put("renewal_count", value.renewalCount())
                    .put("chronomatron_completed", value.chronomatronCompleted())
                    .put("ultrasequencer_completed", value.ultrasequencerCompleted())
                    .put("chronomatron_attempts", value.chronomatronAttempts())
                    .put("ultrasequencer_attempts", value.ultrasequencerAttempts());
            if (value.pendingResult() != null) {
                PendingResult pending = value.pendingResult();
                JSONArray rewards = new JSONArray();
                for (PendingReward reward : pending.rewards()) {
                    rewards.put(new JSONObject().put("reward", reward.rewardId()).put("amount", reward.amount()));
                }
                json.put("pending_result", new JSONObject()
                        .put("experiment", pending.experimentType())
                        .put("tier", pending.tier())
                        .put("completed", pending.completed())
                        .put("score", pending.score())
                        .put("xp", pending.xpAward())
                        .put("bonus_clicks", pending.bonusClicks())
                        .put("rewards", rewards));
            }
            return json.toString();
        }

        @Override
        public PlayerExperimentation deserialize(String json) {
            long now = System.currentTimeMillis();
            if (json == null || json.isBlank()) return new PlayerExperimentation(0, 3, now + CHARGE_RENEWAL_PERIOD_MILLIS,
                    0, false, false, 0, 0, null);
            JSONObject value = new JSONObject(json);
            JSONObject pendingJson = value.optJSONObject("pending_result");
            PendingResult pending = null;
            if (pendingJson != null) {
                List<PendingReward> rewards = new ArrayList<>();
                JSONArray rewardArray = pendingJson.optJSONArray("rewards");
                if (rewardArray != null) {
                    for (int index = 0; index < rewardArray.length(); index++) {
                        JSONObject reward = rewardArray.getJSONObject(index);
                        rewards.add(new PendingReward(reward.getString("reward"), reward.optInt("amount", 1)));
                    }
                }
                pending = new PendingResult(
                        pendingJson.getString("experiment"), pendingJson.getString("tier"),
                        pendingJson.optBoolean("completed"), pendingJson.optInt("score"),
                        pendingJson.optInt("xp"), pendingJson.optInt("bonus_clicks"), rewards);
            }
            long renewalAt = value.optLong("next_charge_renewal_at", now + CHARGE_RENEWAL_PERIOD_MILLIS);
            if (renewalAt <= 0) renewalAt = now + CHARGE_RENEWAL_PERIOD_MILLIS;
            return new PlayerExperimentation(
                    value.optInt("superpairs_bonus_clicks", 0),
                    value.optInt("charges", 3), renewalAt,
                    value.optInt("renewal_count", 0),
                    value.optBoolean("chronomatron_completed"),
                    value.optBoolean("ultrasequencer_completed"),
                    value.optInt("chronomatron_attempts", 0),
                    value.optInt("ultrasequencer_attempts", 0), pending);
        }

        @Override
        public PlayerExperimentation clone(PlayerExperimentation value) {
            return new PlayerExperimentation(value.superpairsBonusClicks(), value.charges(),
                    value.nextChargeRenewalAt(), value.renewalCount(), value.chronomatronCompleted(),
                    value.ultrasequencerCompleted(), value.chronomatronAttempts(), value.ultrasequencerAttempts(),
                    value.pendingResult());
        }
    };

    public DatapointExperimentation(String key) {
        super(key, new PlayerExperimentation(0), SERIALIZER);
    }

    public record PlayerExperimentation(int superpairsBonusClicks, int charges, long nextChargeRenewalAt,
                                        int renewalCount, boolean chronomatronCompleted,
                                        boolean ultrasequencerCompleted, int chronomatronAttempts,
                                        int ultrasequencerAttempts, PendingResult pendingResult) {
        public PlayerExperimentation {
            if (superpairsBonusClicks < 0 || charges < 0 || charges > 3 || renewalCount < 0 || renewalCount > 3
                    || chronomatronAttempts < 0 || ultrasequencerAttempts < 0 || nextChargeRenewalAt <= 0) {
                throw new IllegalArgumentException("Invalid experimentation state");
            }
        }

        public PlayerExperimentation(int superpairsBonusClicks) {
            this(superpairsBonusClicks, 3, System.currentTimeMillis() + CHARGE_RENEWAL_PERIOD_MILLIS,
                    0, false, false, 0, 0, null);
        }

        public PlayerExperimentation withBonusClicks(int value) {
            return new PlayerExperimentation(value, charges, nextChargeRenewalAt, renewalCount,
                    chronomatronCompleted, ultrasequencerCompleted, chronomatronAttempts,
                    ultrasequencerAttempts, pendingResult);
        }

        public PlayerExperimentation withCharges(int value, long renewalAt) {
            return new PlayerExperimentation(superpairsBonusClicks, value, renewalAt, renewalCount,
                    chronomatronCompleted, ultrasequencerCompleted, chronomatronAttempts,
                    ultrasequencerAttempts, pendingResult);
        }

        public PlayerExperimentation withRenewalCount(int value) {
            return new PlayerExperimentation(superpairsBonusClicks, charges, nextChargeRenewalAt, value,
                    chronomatronCompleted, ultrasequencerCompleted, chronomatronAttempts,
                    ultrasequencerAttempts, pendingResult);
        }

        public PlayerExperimentation withAddOn(ExperimentAddOn addOn, boolean completed, int attempts) {
            return new PlayerExperimentation(superpairsBonusClicks, charges, nextChargeRenewalAt, renewalCount,
                    addOn == ExperimentAddOn.CHRONOMATRON ? completed : chronomatronCompleted,
                    addOn == ExperimentAddOn.ULTRASEQUENCER ? completed : ultrasequencerCompleted,
                    addOn == ExperimentAddOn.CHRONOMATRON ? attempts : chronomatronAttempts,
                    addOn == ExperimentAddOn.ULTRASEQUENCER ? attempts : ultrasequencerAttempts, pendingResult);
        }

        public PlayerExperimentation withPendingResult(PendingResult result) {
            return new PlayerExperimentation(superpairsBonusClicks, charges, nextChargeRenewalAt, renewalCount,
                    chronomatronCompleted, ultrasequencerCompleted, chronomatronAttempts,
                    ultrasequencerAttempts, result);
        }

        public PlayerExperimentation resetForRenewal(int newCharges, long renewalAt, int newRenewalCount) {
            return new PlayerExperimentation(superpairsBonusClicks, newCharges, renewalAt, newRenewalCount,
                    false, false, 0, 0, null);
        }

        public enum ExperimentAddOn {
            CHRONOMATRON,
            ULTRASEQUENCER
        }
    }

    public record PendingResult(String experimentType, String tier, boolean completed, int score, int xpAward,
                                int bonusClicks, List<PendingReward> rewards) {
        public PendingResult {
            if (experimentType == null || experimentType.isBlank() || tier == null || tier.isBlank()) {
                throw new IllegalArgumentException("Pending experiment identity is required");
            }
            if (score < 0 || xpAward < 0 || bonusClicks < 0) throw new IllegalArgumentException("Invalid pending reward");
            rewards = List.copyOf(rewards);
        }
    }

    public record PendingReward(String rewardId, int amount) {
        public PendingReward {
            if (rewardId == null || rewardId.isBlank() || amount < 1) {
                throw new IllegalArgumentException("Invalid pending reward item");
            }
        }
    }
}
