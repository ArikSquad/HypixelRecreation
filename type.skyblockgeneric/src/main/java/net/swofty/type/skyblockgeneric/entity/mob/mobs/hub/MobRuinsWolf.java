package net.swofty.type.skyblockgeneric.entity.mob.mobs.hub;

import lombok.NonNull;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import net.swofty.commons.skyblock.item.ItemType;
import net.swofty.commons.skyblock.statistics.ItemStatistic;
import net.swofty.commons.skyblock.statistics.ItemStatistics;
import net.swofty.type.generic.gui.inventory.item.GUIMaterial;
import net.swofty.type.skyblockgeneric.entity.mob.BestiaryMob;
import net.swofty.type.skyblockgeneric.entity.mob.MobType;
import net.swofty.type.skyblockgeneric.entity.mob.impl.RegionPopulator;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.*;
import net.swofty.type.skyblockgeneric.loottable.OtherLoot;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;
import net.swofty.type.skyblockgeneric.region.RegionType;
import net.swofty.type.skyblockgeneric.skill.SkillCategories;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MobRuinsWolf extends BestiaryMob implements RegionPopulator {

    public MobRuinsWolf() {
        super(EntityType.WOLF);
    }

    @Override
    protected void configureMobAttributes() {
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.3);
        getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(16);
    }

    @Override
    protected void configureMobBrain(MobBrain brain) {
        brain.addGoal(4, new LeapAtTargetGoal(brain, 0.4f));
        brain.addGoal(5, new MeleeAttackGoal(brain, 1, true));
        brain.addGoal(8, new WaterAvoidingRandomStrollGoal(brain, 1));
        brain.addGoal(10, new LookAtPlayerGoal(brain, 8));
        brain.addGoal(10, new RandomLookAroundGoal(brain));
        brain.addTargetGoal(1, new HurtByTargetGoal(brain, true));
        brain.addTargetGoal(2, new NearestAttackablePlayerGoal(brain, true));
    }

    @Override
    public String getDisplayName() {
        return "Wolf";
    }

    @Override
    public Integer getLevel() {
        return 15;
    }

    @Override
    public ItemStatistics getBaseStatistics() {
        return ItemStatistics.builder()
                .withBase(ItemStatistic.HEALTH, 250D)
                .withBase(ItemStatistic.DAMAGE, 90D)
                .withBase(ItemStatistic.SPEED, 100D)
                .build();
    }

    @Override
    public @Nullable SkyBlockLootTable getLootTable() {
        return new SkyBlockLootTable() {
            @Override
            public @NonNull List<LootRecord> getLootTable() {
                return List.of(
                        new LootRecord(ItemType.BONE, makeAmountBetween(1, 3), 20)
                );
            }

            @Override
            public @NotNull CalculationMode getCalculationMode() {
                return CalculationMode.CALCULATE_INDIVIDUAL;
            }
        };
    }

    @Override
    public SkillCategories getSkillCategory() {
        return SkillCategories.COMBAT;
    }

    @Override
    public long damageCooldown() {
        return 500;
    }

    @Override
    public List<Populator> getPopulators() {
        return List.of(
            new Populator(RegionType.RUINS, 20)
        );
    }

    @Override
    public OtherLoot getOtherLoot() {
        return new OtherLoot(15, 1, 4);
    }

    @Override
    public List<MobType> getMobTypes() {
        return List.of(MobType.ANIMAL);
    }

    @Override
    public int getMaxBestiaryTier() {
        return 15;
    }

    @Override
    public int getBestiaryBracket() {
        return 1;
    }

    @Override
    public String getMobID() {
        return "RUIN_WOLF";
    }

    @Override
    public GUIMaterial getGuiMaterial() {
        return new GUIMaterial("f4cb7a6bf6c32c49f2589147e6f0f888e9e35875dd1ea2a8af379ca710589e6b");
    }
}
