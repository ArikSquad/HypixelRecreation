package net.swofty.type.skyblockgeneric.entity.mob.mobs.deepcaverns;

import lombok.NonNull;
import net.minestom.server.entity.EntityType;
import net.swofty.commons.skyblock.item.ItemType;
import net.swofty.commons.skyblock.statistics.ItemStatistic;
import net.swofty.commons.skyblock.statistics.ItemStatistics;
import net.swofty.type.generic.gui.inventory.item.GUIMaterial;
import net.swofty.type.skyblockgeneric.entity.mob.BestiaryMob;
import net.swofty.type.skyblockgeneric.entity.mob.MobType;
import net.swofty.type.skyblockgeneric.entity.mob.impl.RegionPopulator;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.*;
import net.minestom.server.entity.attribute.Attribute;
import net.swofty.type.skyblockgeneric.loottable.OtherLoot;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;
import net.swofty.type.skyblockgeneric.region.RegionType;
import net.swofty.type.skyblockgeneric.skill.SkillCategories;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MobRedstonePigman extends BestiaryMob implements RegionPopulator {

	public MobRedstonePigman() {
		super(EntityType.ZOMBIFIED_PIGLIN);
	}

    @Override
    protected void configureMobAttributes() {
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.23);
        getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(35);
    }

    @Override
    protected void configureMobBrain(MobBrain brain) {
        brain.addGoal(2, new MeleeAttackGoal(brain, 1, false));
        brain.addGoal(7, new WaterAvoidingRandomStrollGoal(brain, 1));
        brain.addGoal(8, new LookAtPlayerGoal(brain, 8));
        brain.addGoal(8, new RandomLookAroundGoal(brain));
        brain.addTargetGoal(1, new HurtByTargetGoal(brain, true));
    }

	@Override
	public String getDisplayName() {
		return "Redstone Pigman";
	}

	@Override
	public Integer getLevel() {
		return 3;
	}

	@Override
	public ItemStatistics getBaseStatistics() {
		return ItemStatistics.builder()
				.withBase(ItemStatistic.HEALTH, 250D)
				.withBase(ItemStatistic.DAMAGE, 75D)
				.withBase(ItemStatistic.SPEED, 100D)
				.build();
	}

	@Override
	public @Nullable SkyBlockLootTable getLootTable() {
		return new SkyBlockLootTable() {
			@Override
			public @NonNull List<LootRecord> getLootTable() {
				return List.of(
						new LootRecord(ItemType.GOLD_NUGGET, 2, 50),
						new LootRecord(ItemType.REDSTONE, 5, 50)
						//new LootRecord(ItemType.FLAMING_SWORD, 1, 0.01),
						//new LootRecord(ItemType.EXP_SHARE_CORE, 1, 0.01)
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
	public OtherLoot getOtherLoot() {
		return new OtherLoot(20, 12, 25);
	}

	@Override
	public List<MobType> getMobTypes() {
		return List.of(MobType.UNDEAD);
	}

	@Override
	public int getMaxBestiaryTier() {
		return 10;
	}

	@Override
	public int getBestiaryBracket() {
		return 1;
	}

	@Override
	public String getMobID() {
		return "REDSTONE_PIGMAN";
	}

	@Override
	public GUIMaterial getGuiMaterial() {
		return new GUIMaterial("74e9c6e98582ffd8ff8feb3322cd1849c43fb16b158abb11ca7b42eda7743eb");
	}

	@Override
	public List<Populator> getPopulators() {
		return List.of(new Populator(RegionType.PIGMENS_DEN, 20));
	}
}
