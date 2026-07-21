package net.swofty.type.skyblockgeneric.entity.mob.mobs.minionMobs;

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

public class MobMinionRabbit extends SkyBlockMob {

    public MobMinionRabbit() {
        super(EntityType.RABBIT);
    }

    @Override
    protected void configureMobAttributes() {
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.3);
    }

    @Override
    protected void configureMobBrain(MobBrain brain) {
        brain.addGoal(1, new PanicGoal(brain, 1.25));
        brain.addGoal(5, new WaterAvoidingRandomStrollGoal(brain, 1));
        brain.addGoal(6, new LookAtPlayerGoal(brain, 6));
        brain.addGoal(7, new RandomLookAroundGoal(brain));
    }

    @Override
    public String getDisplayName() {
        return "Rabbit";
    }

    @Override
    public Integer getLevel() {
        return 1;
    }

    @Override
    public ItemStatistics getBaseStatistics() {
        return ItemStatistics.builder()
                .withBase(ItemStatistic.HEALTH, 30D)
                .withBase(ItemStatistic.SPEED, 70D)
                .build();
    }

    @Override
    public @Nullable SkyBlockLootTable getLootTable() {
        return null;
    }

    @Override
    public SkillCategories getSkillCategory() {
        return SkillCategories.FARMING;
    }

    @Override
    public long damageCooldown() {
        return 200;
    }

    @Override
    public OtherLoot getOtherLoot() {
        return new OtherLoot(0, 0, 5);
    }

    @Override
    public List<MobType> getMobTypes() {
        return List.of(MobType.ANIMAL);
    }
}
