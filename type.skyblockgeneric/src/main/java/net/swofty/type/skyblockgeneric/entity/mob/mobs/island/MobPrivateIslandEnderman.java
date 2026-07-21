package net.swofty.type.skyblockgeneric.entity.mob.mobs.island;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.Material;
import net.swofty.commons.skyblock.item.ItemType;
import net.swofty.type.generic.gui.inventory.item.GUIMaterial;
import net.swofty.type.skyblockgeneric.entity.mob.MobType;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.*;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public final class MobPrivateIslandEnderman extends PrivateIslandMob {
    public MobPrivateIslandEnderman() {
        super(EntityType.ENDERMAN, "Enderman", "ENDERMAN", new GUIMaterial(Material.ENDER_PEARL),
                MobType.ENDER, 160, 40, 15, 2, 4);
    }

    @Override
    protected void configureMobAttributes() {
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.3);
        getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(64);
    }

    @Override
    protected void configureMobBrain(MobBrain brain) {
        brain.addGoal(2, new MeleeAttackGoal(brain, 1, false));
        brain.addGoal(7, new WaterAvoidingRandomStrollGoal(brain, 1));
        brain.addGoal(8, new LookAtPlayerGoal(brain, 8));
        brain.addGoal(8, new RandomLookAroundGoal(brain));
        brain.addTargetGoal(1, new HurtByTargetGoal(brain, false));
        brain.addTargetGoal(2, new EndermanStareTargetGoal(brain));
    }

    public void teleportRandomly() {
        Instance instance = getInstance();
        if (instance == null) return;
        Pos origin = getPosition();
        for (int attempt = 0; attempt < 16; attempt++) {
            int x = origin.blockX() + ThreadLocalRandom.current().nextInt(-32, 33);
            int z = origin.blockZ() + ThreadLocalRandom.current().nextInt(-32, 33);
            if (!instance.isChunkLoaded(x >> 4, z >> 4)) continue;
            int top = Math.min(origin.blockY() + 16, instance.getCachedDimensionType().minY()
                    + instance.getCachedDimensionType().height() - 3);
            int bottom = Math.max(origin.blockY() - 24, instance.getCachedDimensionType().minY());
            for (int y = top; y >= bottom; y--) {
                if (!instance.getBlock(x, y, z).isSolid()) continue;
                if (!instance.getBlock(x, y, z).isLiquid()
                        && instance.getBlock(x, y + 1, z).isAir()
                        && instance.getBlock(x, y + 2, z).isAir()) {
                    teleport(new Pos(x + 0.5, y + 1, z + 0.5, origin.yaw(), origin.pitch()));
                }
                return;
            }
        }
    }

    @Override
    public boolean damage(@NotNull Damage damage) {
        boolean applied = super.damage(damage);
        if (applied && !isDead() && ThreadLocalRandom.current().nextFloat() < 0.9f) teleportRandomly();
        return applied;
    }

    @Override
    public SkyBlockLootTable getLootTable() {
        return new PrivateIslandLootTable(new SkyBlockLootTable.LootRecord(ItemType.ENDER_PEARL, 1, 100));
    }
}
