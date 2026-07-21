package net.swofty.type.skyblockgeneric.entity.mob.mobs.island;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.swofty.commons.skyblock.item.ItemType;
import net.swofty.type.generic.gui.inventory.item.GUIMaterial;
import net.swofty.type.skyblockgeneric.entity.mob.MobType;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.*;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;

public final class MobPrivateIslandSkeleton extends PrivateIslandMob {
    public MobPrivateIslandSkeleton() {
        super(EntityType.SKELETON, "Skeleton", "SKELETON", new GUIMaterial(Material.SKELETON_SKULL),
                MobType.UNDEAD, 100, 15, 6, 1, 1);
    }

    @Override
    public void onInit() {
        setEquipment(EquipmentSlot.MAIN_HAND, ItemStack.of(Material.BOW));
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
    public SkyBlockLootTable getLootTable() {
        return new PrivateIslandLootTable(new SkyBlockLootTable.LootRecord(ItemType.BONE, 1, 100));
    }
}
