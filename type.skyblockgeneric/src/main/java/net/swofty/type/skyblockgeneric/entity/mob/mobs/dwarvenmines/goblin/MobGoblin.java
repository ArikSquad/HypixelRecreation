package net.swofty.type.skyblockgeneric.entity.mob.mobs.dwarvenmines.goblin;

import lombok.NonNull;
import net.minestom.server.entity.EntityType;
import net.swofty.commons.skyblock.item.ItemType;
import net.swofty.commons.skyblock.statistics.ItemStatistic;
import net.swofty.commons.skyblock.statistics.ItemStatistics;
import net.swofty.type.generic.gui.inventory.item.GUIMaterial;
import net.swofty.type.skyblockgeneric.entity.mob.BestiaryMob;
import net.swofty.type.skyblockgeneric.entity.mob.MobType;
import net.swofty.type.skyblockgeneric.entity.mob.impl.MobPlayerSkin;
import net.swofty.type.skyblockgeneric.entity.mob.impl.RegionPopulator;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.*;
import net.swofty.type.skyblockgeneric.entity.pathfinder.navigation.GroundNodeLockedPitchFollower;
import net.minestom.server.entity.pathfinding.followers.NodeFollower;
import net.minestom.server.entity.attribute.Attribute;
import net.swofty.type.skyblockgeneric.loottable.OtherLoot;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;
import net.swofty.type.skyblockgeneric.region.RegionType;
import net.swofty.type.skyblockgeneric.skill.SkillCategories;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MobGoblin extends BestiaryMob implements RegionPopulator, MobPlayerSkin {

	public MobGoblin() {
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
        brain.addTargetGoal(1, new HurtByTargetGoal(brain, true));
        brain.addTargetGoal(2, new NearestAttackablePlayerGoal(brain, true));
    }

    @Override
    protected NodeFollower createNodeFollower() {
        return new GroundNodeLockedPitchFollower(this, 0);
    }

	@Override
	public Integer getLevel() {
		return 25;
	}

	@Override
	public int getMaxBestiaryTier() {
		return 20;
	}

	@Override
	public int getBestiaryBracket() {
		return 2;
	}

	@Override
	public String getMobID() {
		return "GOBLIN_WEAKLING_MELEE_2";
	}

	@Override
	public GUIMaterial getGuiMaterial() {
		return new GUIMaterial("172850906b7f0d952c0e508073cc439fd3374ccf5b889c06f7e8d90cc0cc255c");
	}

	@Override
	public String getDisplayName() {
		return "Goblin";
	}

	@Override
	public void onSpawn() {
        setView(getPosition().yaw(), 0, getPosition().yaw());
	}

	@Override
	public float getNameDisplayHeightOffset() {
		return 0.1f;
	}

	@Override
	public ItemStatistics getBaseStatistics() {
		return ItemStatistics.builder()
				.withBase(ItemStatistic.HEALTH, 800D)
				.withBase(ItemStatistic.DAMAGE,  300D)
				.withBase(ItemStatistic.SPEED, 100D)
				.build();
	}

	@Override
	public @Nullable SkyBlockLootTable getLootTable() {
		return new SkyBlockLootTable() {
			@Override
			public @NonNull List<LootRecord> getLootTable() {
				return List.of(
						new LootRecord(ItemType.GOBLIN_EGG, 1, 100)
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

	// Goblins also have a 1% or 1/100 chance of dropping between 1,000 Coins and 10,000 Coins, which is unaffected by Magic Find.
	@Override
	public OtherLoot getOtherLoot() {
		return new OtherLoot(0, 10, 5);
	}

	@Override
	public List<MobType> getMobTypes() {
		return List.of(MobType.SUBTERRANEAN, MobType.HUMANOID);
	}

	@Override
	public List<Populator> getPopulators() {
		return List.of(new Populator(RegionType.GOBLIN_BURROWS, 20));
	}

	@Override
	public String getSkinTexture() {
		return "ewogICJ0aW1lc3RhbXAiIDogMTYwODMxNDEyODAyMCwKICAicHJvZmlsZUlkIiA6ICI0MWQzYWJjMmQ3NDk0MDBjOTA5MGQ1NDM0ZDAzODMxYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJNZWdha2xvb24iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFjMDNiMTRiN2EyYTBjMmY4MDkwMzAzNWJiZmRmODk4ZmViNDA1ZDdiZTM5ODBiNjdiYzQ5NmYxNTNhOTBkZCIKICAgIH0KICB9Cn0=";
	}

	@Override
	public String getSkinSignature() {
		return "YDiw8Yj4pk0TFQwGfiO8XEGp+Afq4O6IETqnQOrIweXiqo3kKTCXxkPMkM7ALWuWpIL+e/3neOWFLNujAbahF4SCpeYBeXN2KHg4BJW3KwBiDMBWlOaiyody8yx9/B20N/aHB1jRgZIWagi+flvlUhEd0ipb8Mmaz2VP03SD17q2DkX8DpjmQ2LFpUC4f4rwum+tY81d8zmr/htMUPWOoeWr7pmwGukvuE/0/1TnUBdXWzlviuTlII2CSNIs6MFoFZpiocQLtkdzHy+NEL8afqHFvKBD8OE5hIz84v01bFfgdGXvA5LIGr4b8WzvdbCQx6HlcnkliWZtTraWGTDPCca99rQRY2/Sy++uPd6Qf1gE22J3bIfjniTVufz2/VaDpkuVqW/MtKR+7ZQOit9vjVosINxFQN0ZNjFTrSF4NR9Tlcy3qEDdUtziuA7RK1Gj//KG9McRODXJXek8GCkHZep1RQd5VIG5/u/4c97vDez5O25fxdGDiyu7J0n/Rb+xBbhWCua4yJam2FjWAbW6oEFqqHGAH5+PnWDzjMBaGPAnxGcksFDxZ+0gj+1wm+fqfiasIdD2dCimOrMyBIkUE/egP7hbpt+Tr3CjIkdfdIzcIAOeip1UszmxVEUiCKBA5xrP7xK6Y9TXQObvk4TGbhMRdb5vYmSpDsAVMUNgk60=";
	}
}
