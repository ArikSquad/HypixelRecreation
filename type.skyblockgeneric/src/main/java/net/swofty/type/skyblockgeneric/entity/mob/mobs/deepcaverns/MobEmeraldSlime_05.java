package net.swofty.type.skyblockgeneric.entity.mob.mobs.deepcaverns;

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
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.HurtByTargetGoal;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.MobBrain;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.NearestAttackablePlayerGoal;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.SlimeHopGoal;
import net.swofty.type.skyblockgeneric.loottable.OtherLoot;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;
import net.swofty.type.skyblockgeneric.region.RegionType;
import net.swofty.type.skyblockgeneric.skill.SkillCategories;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MobEmeraldSlime_05 extends BestiaryMob implements RegionPopulator {

	public MobEmeraldSlime_05() {
		super(EntityType.SLIME);
	}

	@Override
	protected void configureMobAttributes() {
		getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.3);
		getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(16);
	}

	@Override
	protected void configureMobBrain(MobBrain brain) {
		brain.addGoal(1, new SlimeHopGoal(brain, 3, 7));
		brain.addTargetGoal(1, new NearestAttackablePlayerGoal(brain, false));
		brain.addTargetGoal(2, new HurtByTargetGoal(brain, false));
	}

	@Override
	public String getDisplayName() {
		return "Emerald Slime";
	}

	@Override
	public Integer getLevel() {
		return 5;
	}

	@Override
	public ItemStatistics getBaseStatistics() {
		return ItemStatistics.builder()
				.withBase(ItemStatistic.HEALTH, 80D)
				.withBase(ItemStatistic.DAMAGE, 70D)
				.withBase(ItemStatistic.SPEED, 100D)
				.build();
	}

	@Override
	public @Nullable SkyBlockLootTable getLootTable() {
		return new SkyBlockLootTable() {
			@Override
			public @NonNull List<LootRecord> getLootTable() {
				return List.of(
						new LootRecord(ItemType.SLIME_BALL, 1, 100)
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
		return new OtherLoot(12, 5, 20);
	}

	@Override
	public List<MobType> getMobTypes() {
		return List.of(MobType.CUBIC);
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
		return "EMERALD_SLIME_05";
	}

	@Override
	public GUIMaterial getGuiMaterial() {
		return new GUIMaterial("895aeec6b842ada8669f846d65bc49762597824ab944f22f45bf3bbb941abe6c");
	}

	@Override
	public List<Populator> getPopulators() {
		return List.of(new Populator(RegionType.SLIMEHILL, 20));
	}
}
