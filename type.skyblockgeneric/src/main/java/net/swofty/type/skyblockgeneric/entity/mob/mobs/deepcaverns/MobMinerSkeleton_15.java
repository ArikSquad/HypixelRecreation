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
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.*;
import net.swofty.type.skyblockgeneric.item.SkyBlockItem;
import net.swofty.type.skyblockgeneric.loottable.OtherLoot;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;
import net.swofty.type.skyblockgeneric.region.RegionType;
import net.swofty.type.skyblockgeneric.skill.SkillCategories;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MobMinerSkeleton_15 extends BestiaryMob implements RegionPopulator {

	public MobMinerSkeleton_15() {
		super(EntityType.SKELETON);
	}

    @Override
    protected void configureMobAttributes() {
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.25);
        getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(16);
    }

    @Override
    protected void configureMobBrain(MobBrain brain) {
        brain.addGoal(4, new BowAttackGoal(brain, 1, 20, 15));
        brain.addGoal(5, new WaterAvoidingRandomStrollGoal(brain, 1));
        brain.addGoal(6, new LookAtPlayerGoal(brain, 8));
        brain.addGoal(6, new RandomLookAroundGoal(brain));
        brain.addTargetGoal(1, new HurtByTargetGoal(brain, false));
        brain.addTargetGoal(2, new NearestAttackablePlayerGoal(brain, true));
    }

	@Override
	public String getDisplayName() {
		return "Miner Skeleton";
	}

	@Override
	public Integer getLevel() {
		return 15;
	}

	@Override
	public void onInit() {
		setBoots(new SkyBlockItem(ItemType.MINER_ARMOR_BOOTS).getItemStack());
		setLeggings(new SkyBlockItem(ItemType.MINER_ARMOR_LEGGINGS).getItemStack());
		setChestplate(new SkyBlockItem(ItemType.MINER_ARMOR_CHESTPLATE).getItemStack());
		setHelmet(new SkyBlockItem(ItemType.MINER_ARMOR_HELMET).getItemStack());
	}

	@Override
	public ItemStatistics getBaseStatistics() {
		return ItemStatistics.builder()
				.withBase(ItemStatistic.HEALTH, 250D)
				.withBase(ItemStatistic.DAMAGE, 200D)
				.withBase(ItemStatistic.SPEED, 100D)
				.build();
	}

	@Override
	public @Nullable SkyBlockLootTable getLootTable() {
		return new SkyBlockLootTable() {
			@Override
			public @NonNull List<LootRecord> getLootTable() {
				return List.of(
						new LootRecord(ItemType.BONE, 1, 300), // hypixel, why is it 300% and what does that mean?
						new LootRecord(ItemType.MINER_ARMOR_BOOTS, 1, 1),
						new LootRecord(ItemType.MINER_ARMOR_LEGGINGS, 1, 1),
						new LootRecord(ItemType.MINER_ARMOR_CHESTPLATE, 1, 1),
						new LootRecord(ItemType.MINER_ARMOR_HELMET, 1, 1)

						//new LootRecord(ItemType.EXP_SHARE_CORE, 1, 0.01)
						// bone dye
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
		return new OtherLoot(20, 12, 30);
	}

	@Override
	public List<MobType> getMobTypes() {
		return List.of(MobType.SKELETAL);
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
		return "DIAMOND_SKELETON_15";
	}

	@Override
	public GUIMaterial getGuiMaterial() {
		return new GUIMaterial("8de8bbd7f6d77a1614865ef6a1d31f53f797550d14ee21d107a8415c14b48ca6");
	}

	@Override
	public List<Populator> getPopulators() {
		return List.of(
				new Populator(RegionType.DIAMOND_RESERVE, 20)
		);
	}
}
