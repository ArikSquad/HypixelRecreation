package net.swofty.type.skyblockgeneric.entity.mob.mobs.island;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.metadata.cube.AbstractCubeMeta;
import net.minestom.server.item.Material;
import net.swofty.commons.skyblock.item.ItemType;
import net.swofty.type.generic.gui.inventory.item.GUIMaterial;
import net.swofty.type.skyblockgeneric.entity.mob.MobType;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.HurtByTargetGoal;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.MobBrain;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.NearestAttackablePlayerGoal;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.SlimeHopGoal;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;

public final class MobPrivateIslandSlime extends PrivateIslandMob {
    public MobPrivateIslandSlime() {
        super(EntityType.SLIME, "Slime", "SLIME", new GUIMaterial(Material.SLIME_BALL),
                MobType.CUBIC, 80, 15, 4, 1, 1);
        if (getEntityMeta() instanceof AbstractCubeMeta meta) meta.setSize(1);
    }

    @Override
    protected void configureMobAttributes() {
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.3);
        getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(16);
    }

    @Override
    protected void configureMobBrain(MobBrain brain) {
        brain.addGoal(1, new SlimeHopGoal(brain, 3, 7));
        brain.addTargetGoal(1, new NearestAttackablePlayerGoal(brain, false));
        brain.addTargetGoal(2, new HurtByTargetGoal(brain, false));
    }

    @Override
    public SkyBlockLootTable getLootTable() {
        return new PrivateIslandLootTable(new SkyBlockLootTable.LootRecord(ItemType.SLIME_BALL, 1, 100));
    }
}
