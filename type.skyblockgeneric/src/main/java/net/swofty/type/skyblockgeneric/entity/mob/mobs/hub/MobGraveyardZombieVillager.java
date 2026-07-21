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

public class MobGraveyardZombieVillager extends BestiaryMob implements RegionPopulator {

	public MobGraveyardZombieVillager() {
		super(EntityType.ZOMBIE_VILLAGER);
	}

    @Override
    protected void configureMobAttributes() {
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.23);
        getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(35);
    }

    @Override
    protected void configureMobBrain(MobBrain brain) {
        brain.addGoal(3, new MeleeAttackGoal(brain, 1, false));
        brain.addGoal(7, new WaterAvoidingRandomStrollGoal(brain, 1));
        brain.addGoal(8, new LookAtPlayerGoal(brain, 8));
        brain.addGoal(8, new RandomLookAroundGoal(brain));
        brain.addTargetGoal(1, new HurtByTargetGoal(brain, true));
        brain.addTargetGoal(2, new NearestAttackablePlayerGoal(brain, true));
    }

	@Override
	public String getDisplayName() {
		return "Zombie Villager";
	}

	@Override
	public Integer getLevel() {
		return 1;
	}

	@Override
	public ItemStatistics getBaseStatistics() {
		return ItemStatistics.builder()
				.withBase(ItemStatistic.HEALTH, 120D)
				.withBase(ItemStatistic.DAMAGE, 24D)
				.withBase(ItemStatistic.SPEED, 100D)
				.build();
	}

	@Override
	public @Nullable SkyBlockLootTable getLootTable() {
		return new SkyBlockLootTable() {
			@Override
			public @NonNull List<LootRecord> getLootTable() {
				return List.of(
						new LootRecord(ItemType.ROTTEN_FLESH, makeAmountBetween(1, 3), 20)
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
		return new OtherLoot(7, 1, 2);
	}

	@Override
	public List<MobType> getMobTypes() {
		return List.of(MobType.UNDEAD);
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
		return "ZOMBIE_VILLIGER";
	}

	@Override
	public GUIMaterial getGuiMaterial() {
		return new GUIMaterial("69198f410a10f99314aa0fbe9a3db10697bbc1c011f019507d96673c64217f5a");
	}

	@Override
	public List<Populator> getPopulators() {
		return List.of(
            new Populator(RegionType.GRAVEYARD, 5)
        );
	}
}
