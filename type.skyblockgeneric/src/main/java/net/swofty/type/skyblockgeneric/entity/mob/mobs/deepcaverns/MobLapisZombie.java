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
import net.swofty.type.skyblockgeneric.item.SkyBlockItem;
import net.swofty.type.skyblockgeneric.loottable.OtherLoot;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;
import net.swofty.type.skyblockgeneric.region.RegionType;
import net.swofty.type.skyblockgeneric.skill.SkillCategories;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MobLapisZombie extends BestiaryMob implements RegionPopulator {

	public MobLapisZombie() {
		super(EntityType.ZOMBIE);
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
		return "Lapis Zombie";
	}

	@Override
	public Integer getLevel() {
		return 7;
	}

	@Override
	public void onInit() {
		setBoots(new SkyBlockItem(ItemType.LAPIS_ARMOR_BOOTS).getItemStack());
		setLeggings(new SkyBlockItem(ItemType.LAPIS_ARMOR_LEGGINGS).getItemStack());
		setChestplate(new SkyBlockItem(ItemType.LAPIS_ARMOR_CHESTPLATE).getItemStack());
		setHelmet(new SkyBlockItem(ItemType.LAPIS_ARMOR_HELMET).getItemStack());
	}

	@Override
	public ItemStatistics getBaseStatistics() {
		return ItemStatistics.builder()
				.withBase(ItemStatistic.HEALTH, 200D)
				.withBase(ItemStatistic.DAMAGE, 50D)
				.withBase(ItemStatistic.SPEED, 100D)
				.build();
	}

	@Override
	public @Nullable SkyBlockLootTable getLootTable() {
		return new SkyBlockLootTable() {
			@Override
			public @NonNull List<LootRecord> getLootTable() {
				return List.of(
						new LootRecord(ItemType.ROTTEN_FLESH, 1, 100),
						new LootRecord(ItemType.LAPIS_ARMOR_BOOTS, 1, 1),
						new LootRecord(ItemType.LAPIS_ARMOR_LEGGINGS, 1, 1),
						new LootRecord(ItemType.LAPIS_ARMOR_CHESTPLATE, 1, 1),
						new LootRecord(ItemType.LAPIS_ARMOR_HELMET, 1, 1)
						//new LootRecord(ItemType.EXP_SHARE_CORE, 1, 0.01),
						//new LootRecord(ItemType.LAPIS_CRYSTAL, 1, 1)
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
		return new OtherLoot(12, 5, 10);
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
		return "lapis_zombie";
	}

	@Override
	public GUIMaterial getGuiMaterial() {
		return new GUIMaterial("e9f7979b25001087969d58c06e14d00b8dab57dab060b4c8b483c1b7f869940");
	}

	@Override
	public List<Populator> getPopulators() {
		return List.of(new Populator(RegionType.SLIMEHILL, 20));
	}
}
