package net.swofty.type.skyblockgeneric.entity.mob.mobs.deepcaverns;

import lombok.NonNull;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.metadata.monster.CreeperMeta;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;
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

public class MobSneakyCreeper extends BestiaryMob implements RegionPopulator {
    private int swell;
    private int swellDirection = -1;

	public MobSneakyCreeper() {
		super(EntityType.CREEPER);
	}

    @Override
    protected void configureMobAttributes() {
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.25);
        getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(16);
    }

    @Override
    protected void configureMobBrain(MobBrain brain) {
        brain.addGoal(2, new SwellGoal(brain, direction -> swellDirection = direction));
        brain.addGoal(4, new MeleeAttackGoal(brain, 1, false) {
            @Override
            protected void attack(LivingEntity target) {
            }
        });
        brain.addGoal(5, new WaterAvoidingRandomStrollGoal(brain, 0.8));
        brain.addGoal(6, new LookAtPlayerGoal(brain, 8));
        brain.addGoal(6, new RandomLookAroundGoal(brain));
        brain.addTargetGoal(1, new NearestAttackablePlayerGoal(brain, true));
        brain.addTargetGoal(2, new HurtByTargetGoal(brain, false));
        scheduler().buildTask(this::tickFuse).repeat(TaskSchedule.tick(1)).schedule();
    }

    private void tickFuse() {
        if (isDead() || getInstance() == null) return;
        int previous = swell;
        swell = Math.clamp(swell + swellDirection, 0, 30);
        CreeperMeta meta = (CreeperMeta) getEntityMeta();
        if (previous == 0 && swell > 0) meta.setState(CreeperMeta.State.FUSE);
        else if (previous > 0 && swell == 0) meta.setState(CreeperMeta.State.IDLE);
        if (swell < 30) return;
        double radius = meta.isCharged() ? 6 : 3;
        getInstance().getPlayers().stream()
                .filter(player -> player.getPosition().distanceSquared(getPosition()) <= radius * radius)
                .forEach(player -> attack(player, false));
        remove();
    }

	@Override
	public String getDisplayName() {
		return "Sneaky Creeper";
	}

	@Override
	public Integer getLevel() {
		return 3;
	}

	@Override
	public void onInit() {
		setInvisible(true);
	}

	@Override
	public ItemStatistics getBaseStatistics() {
		return ItemStatistics.builder()
				.withBase(ItemStatistic.HEALTH, 120D)
				.withBase(ItemStatistic.DAMAGE, 80D)
				.withBase(ItemStatistic.SPEED, 100D)
				.build();
	}

	@Override
	public @Nullable SkyBlockLootTable getLootTable() {
		return new SkyBlockLootTable() {
			@Override
			public @NonNull List<LootRecord> getLootTable() {
				return List.of(
						new LootRecord(ItemType.GUNPOWDER, 1, 100)
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
		return new OtherLoot(0, 3, 2);
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
		return "INVISIBLE_CREEPER";
	}

	@Override
	public GUIMaterial getGuiMaterial() {
		return new GUIMaterial(Material.CREEPER_HEAD);
	}

	@Override
	public List<Populator> getPopulators() {
		return List.of(new Populator(RegionType.GUNPOWDER_MINES, 10));
	}
}
