package net.swofty.type.skyblockgeneric.entity.mob.mobs.dwarvenmines;

import lombok.NonNull;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.item.Material;
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

public class MobGlaciteWalker extends BestiaryMob implements RegionPopulator {

	public MobGlaciteWalker() {
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
	public Integer getLevel() {
		return 45;
	}

	@Override
	public int getMaxBestiaryTier() {
		return 15;
	}

	@Override
	public int getBestiaryBracket() {
		return 2;
	}

	@Override
	public String getMobID() {
		return "ICE_WALKER";
	}

	@Override
	public void onInit() {
		setBoots(new SkyBlockItem(ItemType.LAPIS_ARMOR_BOOTS).getItemStack());
		setLeggings(new SkyBlockItem(ItemType.LAPIS_ARMOR_LEGGINGS).getItemStack());
		setChestplate(new SkyBlockItem(ItemType.LAPIS_ARMOR_CHESTPLATE).getItemStack());
		setHelmet(new SkyBlockItem(ItemType.LAPIS_ARMOR_HELMET).getItemStack());
	}

	@Override
	public GUIMaterial getGuiMaterial() {
		return new GUIMaterial(Material.PACKED_ICE);
	}

	@Override
	public String getDisplayName() {
		return "Glacite Walker";
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
						new LootRecord(ItemType.GLACITE_JEWEL, 1, 0.5)
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
		return new OtherLoot(40, 8, 8);
	}

	@Override
	public List<MobType> getMobTypes() {
		return List.of(MobType.GLACIAL, MobType.HUMANOID);
	}

	@Override
	public List<Populator> getPopulators() {
		return List.of(new Populator(RegionType.GREAT_ICE_WALL, 20));
	}
}
