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
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.LookAtPlayerGoal;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.MobBrain;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.RandomLookAroundGoal;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.WaterAvoidingRandomStrollGoal;
import net.swofty.type.skyblockgeneric.item.SkyBlockItem;
import net.swofty.type.skyblockgeneric.loottable.OtherLoot;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;
import net.swofty.type.skyblockgeneric.skill.SkillCategories;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GoldenGoblin extends BestiaryMob {

	public GoldenGoblin() {
		super(EntityType.PLAYER);
	}

    @Override
    protected void configureMobAttributes() {
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.35);
        getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(16);
    }

    @Override
    protected void configureMobBrain(MobBrain brain) {
        brain.addGoal(7, new WaterAvoidingRandomStrollGoal(brain, 1));
        brain.addGoal(8, new LookAtPlayerGoal(brain, 8));
        brain.addGoal(8, new RandomLookAroundGoal(brain));
    }

	@Override
	public Integer getLevel() {
		return 500;
	}

	@Override
	public int getMaxBestiaryTier() {
		return 15;
	}

	@Override
	public int getBestiaryBracket() {
		return 5;
	}

	@Override
	public String getMobID() {
		return "GOBLIN";
	}

	@Override
	public void onInit() {
		setBoots(new SkyBlockItem(ItemType.GOLDEN_BOOTS).getItemStack());
		setLeggings(new SkyBlockItem(ItemType.GOLDEN_LEGGINGS).getItemStack());
		setChestplate(new SkyBlockItem(ItemType.GOLDEN_CHESTPLATE).getItemStack());
		setHelmet(new SkyBlockItem(ItemType.GOLDEN_HELMET).getItemStack());
	}

	@Override
	public GUIMaterial getGuiMaterial() {
		return new GUIMaterial("29d9fbe74cbd9ebeda4d571a4176c66e298ec409dc10510f6ddf1496f48840b1");
	}

	@Override
	public String getDisplayName() {
		return "Golden Goblin";
	}

	@Override
	public ItemStatistics getBaseStatistics() {
		return ItemStatistics.builder()
				.withBase(ItemStatistic.HEALTH, 3D)
				.withBase(ItemStatistic.DAMAGE, 250D)
				.withBase(ItemStatistic.SPEED, 100D)
				.build();
	}

	@Override
	public @Nullable SkyBlockLootTable getLootTable() {
		return new SkyBlockLootTable() {
			@Override
			public @NonNull List<LootRecord> getLootTable() {
				return List.of(
						new LootRecord(ItemType.MITHRIL, 5, 100)
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
		return new OtherLoot(0, 100, 50);
	}

	@Override
	public List<MobType> getMobTypes() {
		return List.of(MobType.HUMANOID, MobType.SHIELDED);
	}

}
