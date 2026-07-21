package net.swofty.type.skyblockgeneric.entity.mob.mobs.island;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.metadata.monster.CreeperMeta;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;
import net.swofty.commons.skyblock.item.ItemType;
import net.swofty.type.generic.gui.inventory.item.GUIMaterial;
import net.swofty.type.skyblockgeneric.entity.mob.MobType;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.*;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;

public final class MobPrivateIslandCreeper extends PrivateIslandMob {
    private int swell;
    private int swellDirection = -1;

    public MobPrivateIslandCreeper() {
        super(EntityType.CREEPER, "Creeper", "CREEPER", new GUIMaterial(Material.CREEPER_HEAD),
                MobType.SPOOKY, 80, 20, 8, 2, 2);
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
                // Creepers approach but deal damage only through their explosion.
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
    public SkyBlockLootTable getLootTable() {
        return new PrivateIslandLootTable(new SkyBlockLootTable.LootRecord(ItemType.GUNPOWDER, 1, 100));
    }
}
