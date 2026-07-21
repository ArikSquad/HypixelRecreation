package net.swofty.type.skyblockgeneric.entity.mob.mobs.island;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.item.Material;
import net.swofty.commons.skyblock.item.ItemType;
import net.swofty.type.generic.gui.inventory.item.GUIMaterial;
import net.swofty.type.skyblockgeneric.entity.mob.MobType;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.*;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;

public class MobZombie_01 extends PrivateIslandMob {
    public MobZombie_01() {
        this("ZOMBIE_01", 100, 20, 1, 1);
    }

    protected MobZombie_01(String id, double health, double damage, int coins, int xpOrbs) {
        super(EntityType.ZOMBIE, "Zombie", id, new GUIMaterial(Material.ZOMBIE_HEAD),
                MobType.UNDEAD, health, damage, 6, coins, xpOrbs);
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
    public SkyBlockLootTable getLootTable() {
        return new PrivateIslandLootTable(
                new SkyBlockLootTable.LootRecord(ItemType.ROTTEN_FLESH, 1, 100),
                new SkyBlockLootTable.LootRecord(ItemType.POISONOUS_POTATO, 1, 2.5),
                new SkyBlockLootTable.LootRecord(ItemType.POTATO, 1, 1),
                new SkyBlockLootTable.LootRecord(ItemType.CARROT, 1, 1));
    }
}
