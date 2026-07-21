package net.swofty.type.skyblockgeneric.entity.mob.mobs.seacreature;

import net.minestom.server.entity.attribute.Attribute;
import net.swofty.commons.skyblock.statistics.ItemStatistics;
import net.swofty.type.skyblockgeneric.entity.mob.MobType;
import net.swofty.type.skyblockgeneric.entity.mob.SkyBlockMob;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.*;
import net.swofty.type.skyblockgeneric.loottable.OtherLoot;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;
import net.swofty.type.skyblockgeneric.skill.SkillCategories;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A sea creature is fully described by a {@link SeaCreatureProfile} — one
 * concrete class drives every variant. The thread-local handoff
 * ({@link #IN_CONSTRUCTION}) is the standard workaround for SkyBlockMob's
 * super-constructor calling abstract accessors before subclass field
 * assignment happens; without it those accessors would NPE on the profile
 * field. Once super() returns the field is set and future calls hit the
 * fast path.
 */
public final class SeaCreatureMob extends SkyBlockMob {

    private static final ThreadLocal<SeaCreatureProfile> IN_CONSTRUCTION = new ThreadLocal<>();

    private final SeaCreatureProfile profile;

    private SeaCreatureMob(SeaCreatureProfile profile) {
        super(profile.entityType());
        this.profile = profile;
    }

    public static SeaCreatureMob create(SeaCreatureProfile profile) {
        IN_CONSTRUCTION.set(profile);
        try {
            return new SeaCreatureMob(profile);
        } finally {
            IN_CONSTRUCTION.remove();
        }
    }

    public String getSeaCreatureId() {
        return live().id();
    }

    public SeaCreatureProfile getProfile() {
        return live();
    }

    @Override
    protected void configureMobAttributes() {
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(Math.max(0.1, live().speed() / 100D));
        getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(24);
    }

    @Override
    protected void configureMobBrain(MobBrain brain) {
        SeaCreatureBehaviour behaviour = live().behaviour();
        if (behaviour instanceof SeaCreatureBehaviour.Aggressive aggressive) {
            brain.addGoal(3, new MeleeAttackGoal(brain, aggressive.speed(), false));
            brain.addTargetGoal(1, new HurtByTargetGoal(brain, false));
            brain.addTargetGoal(2, new NearestAttackablePlayerGoal(brain, true));
        } else if (behaviour instanceof SeaCreatureBehaviour.Passive) {
            brain.addGoal(1, new PanicGoal(brain, 1.25));
        }
        brain.addGoal(7, new WaterAvoidingRandomStrollGoal(brain, 1));
        brain.addGoal(8, new LookAtPlayerGoal(brain, 8));
        brain.addGoal(8, new RandomLookAroundGoal(brain));
    }

    @Override public String getDisplayName() { return live().displayName(); }
    @Override public Integer getLevel() { return live().level(); }
    @Override public List<MobType> getMobTypes() { return live().mobTypes(); }
    @Override public ItemStatistics getBaseStatistics() { return live().asBaseStatistics(); }
    @Override public OtherLoot getOtherLoot() { return live().asOtherLoot(); }
    @Override public long damageCooldown() { return live().damageCooldownMs(); }
    @Override public @Nullable SkyBlockLootTable getLootTable() { return live().lootTable(); }
    @Override public SkillCategories getSkillCategory() { return SkillCategories.FISHING; }

    private SeaCreatureProfile live() {
        return profile != null ? profile : IN_CONSTRUCTION.get();
    }
}
