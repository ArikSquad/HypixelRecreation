package net.swofty.type.skyblockgeneric.entity.mob.mobs.island;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.item.Material;
import net.swofty.commons.skyblock.item.ItemType;
import net.swofty.type.generic.gui.inventory.item.GUIMaterial;
import net.swofty.type.skyblockgeneric.entity.mob.MobType;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.*;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;

public final class MobPrivateIslandWitch extends PrivateIslandMob {
    public MobPrivateIslandWitch() {
        super(EntityType.WITCH, "Witch", "WITCH", new GUIMaterial(Material.POTION),
                MobType.ARCANE, 150, 20, 15, 1, 4);
    }

    @Override
    protected void configureMobAttributes() {
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.25);
        getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(16);
    }

    @Override
    protected void configureMobBrain(MobBrain brain) {
        brain.addGoal(2, new RangedMagicAttackGoal(brain, 1, 12, 60));
        brain.addGoal(6, new WaterAvoidingRandomStrollGoal(brain, 1));
        brain.addGoal(7, new LookAtPlayerGoal(brain, 8));
        brain.addGoal(8, new RandomLookAroundGoal(brain));
        brain.addTargetGoal(1, new HurtByTargetGoal(brain, false));
        brain.addTargetGoal(2, new NearestAttackablePlayerGoal(brain, true));
    }

    @Override
    public SkyBlockLootTable getLootTable() {
        return new PrivateIslandLootTable(
                new SkyBlockLootTable.LootRecord(ItemType.GUNPOWDER, 1, 50),
                new SkyBlockLootTable.LootRecord(ItemType.GLOWSTONE_DUST, 1, 50),
                new SkyBlockLootTable.LootRecord(ItemType.GLASS_BOTTLE, 2, 20));
    }
}
