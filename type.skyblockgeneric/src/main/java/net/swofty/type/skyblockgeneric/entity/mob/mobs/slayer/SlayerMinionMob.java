package net.swofty.type.skyblockgeneric.entity.mob.mobs.slayer;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import net.swofty.commons.skyblock.statistics.ItemStatistic;
import net.swofty.commons.skyblock.statistics.ItemStatistics;
import net.swofty.type.skyblockgeneric.entity.mob.MobType;
import net.swofty.type.skyblockgeneric.entity.mob.SkyBlockMob;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.*;
import net.swofty.type.skyblockgeneric.loottable.OtherLoot;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;
import net.swofty.type.skyblockgeneric.skill.SkillCategories;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public final class SlayerMinionMob extends SkyBlockMob {
    private static final ThreadLocal<SlayerMinionProfile> IN_CONSTRUCTION = new ThreadLocal<>();

    private final SlayerMinionProfile profile;
    private final UUID ownerUuid;

    private SlayerMinionMob(UUID ownerUuid, SlayerMinionProfile profile) {
        super(profile.entityType());
        this.ownerUuid = ownerUuid;
        this.profile = profile;
    }

    public static SlayerMinionMob create(UUID ownerUuid, SlayerMinionProfile profile) {
        IN_CONSTRUCTION.set(profile);
        try {
            return new SlayerMinionMob(ownerUuid, profile);
        } finally {
            IN_CONSTRUCTION.remove();
        }
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    @Override
    protected void configureMobAttributes() {
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(Math.max(0.1, live().speed() / 100D));
        getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(24);
    }

    @Override
    protected void configureMobBrain(MobBrain brain) {
        brain.addGoal(3, new MeleeAttackGoal(brain, 1.8, false));
        brain.addGoal(7, new WaterAvoidingRandomStrollGoal(brain, 1));
        brain.addGoal(8, new LookAtPlayerGoal(brain, 8));
        brain.addGoal(8, new RandomLookAroundGoal(brain));
        brain.addTargetGoal(1, new HurtByTargetGoal(brain, false));
        brain.addTargetGoal(2, new NearestAttackablePlayerGoal(brain, true));
    }

    @Override public String getDisplayName() { return live().displayName(); }
    @Override public Integer getLevel() { return live().level(); }
    @Override public List<MobType> getMobTypes() { return live().mobTypes(); }
    @Override public @Nullable SkyBlockLootTable getLootTable() { return null; }
    @Override public SkillCategories getSkillCategory() { return SkillCategories.COMBAT; }
    @Override public long damageCooldown() { return 700L; }
    @Override public OtherLoot getOtherLoot() { return new OtherLoot(0, 0, 0); }

    @Override
    public ItemStatistics getBaseStatistics() {
        return ItemStatistics.builder()
            .withBase(ItemStatistic.HEALTH, live().health())
            .withBase(ItemStatistic.DAMAGE, live().damage())
            .withBase(ItemStatistic.SPEED, live().speed())
            .build();
    }

    private SlayerMinionProfile live() {
        return profile != null ? profile : IN_CONSTRUCTION.get();
    }

    public record SlayerMinionProfile(
        String displayName,
        int level,
        EntityType entityType,
        double health,
        double damage,
        double speed,
        List<MobType> mobTypes
    ) {
    }
}
