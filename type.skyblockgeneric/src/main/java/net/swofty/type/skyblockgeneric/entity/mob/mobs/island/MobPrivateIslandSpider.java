package net.swofty.type.skyblockgeneric.entity.mob.mobs.island;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.pathfinding.followers.NodeFollower;
import net.minestom.server.item.Material;
import net.swofty.commons.skyblock.item.ItemType;
import net.swofty.type.generic.gui.inventory.item.GUIMaterial;
import net.swofty.type.skyblockgeneric.entity.mob.MobType;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.*;
import net.swofty.type.skyblockgeneric.entity.pathfinder.navigation.SpiderNodeFollower;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;

public final class MobPrivateIslandSpider extends PrivateIslandMob {
    public MobPrivateIslandSpider() {
        super(EntityType.SPIDER, "Spider", "SPIDER", new GUIMaterial(Material.SPIDER_EYE),
                MobType.ARTHROPOD, 120, 35, 8, 1, 1);
    }

    @Override
    protected void configureMobAttributes() {
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.3);
        getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(16);
    }

    @Override
    protected void configureMobBrain(MobBrain brain) {
        brain.addGoal(4, new LeapAtTargetGoal(brain, 0.4f));
        brain.addGoal(5, new MeleeAttackGoal(brain, 1, true));
        brain.addGoal(7, new WaterAvoidingRandomStrollGoal(brain, 0.8));
        brain.addGoal(8, new LookAtPlayerGoal(brain, 8));
        brain.addGoal(8, new RandomLookAroundGoal(brain));
        brain.addTargetGoal(1, new HurtByTargetGoal(brain, false));
        brain.addTargetGoal(2, new NearestAttackablePlayerGoal(brain, true));
    }

    @Override
    protected NodeFollower createNodeFollower() {
        return new SpiderNodeFollower(this);
    }

    @Override
    public SkyBlockLootTable getLootTable() {
        return new PrivateIslandLootTable(
                new SkyBlockLootTable.LootRecord(ItemType.STRING, 1, 100),
                new SkyBlockLootTable.LootRecord(ItemType.SPIDER_EYE, 1, 50));
    }
}
