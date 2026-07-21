package net.swofty.type.skyblockgeneric.entity.mob.mobs.dwarvenmines;

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

public class MobTreasureHoarder extends BestiaryMob implements RegionPopulator {

	public MobTreasureHoarder() {
		super(EntityType.PLAYER);
	}

    @Override
    protected void configureMobAttributes() {
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.3);
        getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(16);
    }

    @Override
    protected void configureMobBrain(MobBrain brain) {
        brain.addGoal(3, new MeleeAttackGoal(brain, 1, false));
        brain.addGoal(7, new WaterAvoidingRandomStrollGoal(brain, 1));
        brain.addGoal(8, new LookAtPlayerGoal(brain, 8));
        brain.addGoal(8, new RandomLookAroundGoal(brain));
        brain.addTargetGoal(1, new HurtByTargetGoal(brain, false));
        brain.addTargetGoal(2, new NearestAttackablePlayerGoal(brain, true));
    }

	@Override
	public int getMaxBestiaryTier() {
		return 15;
	}

	@Override
	public int getBestiaryBracket() {
		return 4;
	}

	@Override
	public String getMobID() {
		return "TREASURE_HOARDER";
	}

	@Override
	public GUIMaterial getGuiMaterial() {
		return null;
	}

	@Override
	public String getDisplayName() {
		return "Treasure Hoarder";
	}

	@Override
	public Integer getLevel() {
		return 70;
	}

	@Override
	public ItemStatistics getBaseStatistics() {
		return ItemStatistics.builder()
				.withBase(ItemStatistic.HEALTH, 15000D)
				.withBase(ItemStatistic.DAMAGE, 500D)
				.withBase(ItemStatistic.SPEED, 100D)
				.build();
	}


	@Override
	public @Nullable SkyBlockLootTable getLootTable() {
		return new SkyBlockLootTable() {
			@Override
			public @NonNull List<LootRecord> getLootTable() {
				return List.of(
						new LootRecord(ItemType.STARFALL, 1, 100),
						new LootRecord(ItemType.STARFALL, 1, 50),
						new LootRecord(ItemType.TREASURITE, 1, 0.5)
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
		return new OtherLoot(70, 0, 0);
	}

	@Override
	public List<MobType> getMobTypes() {
		return List.of(MobType.HUMANOID);
	}

	@Override
	public List<Populator> getPopulators() {
		return List.of(new Populator(RegionType.UPPER_MINES, 20));
	}
}
