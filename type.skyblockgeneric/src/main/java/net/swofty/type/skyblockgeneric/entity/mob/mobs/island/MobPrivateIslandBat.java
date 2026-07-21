package net.swofty.type.skyblockgeneric.entity.mob.mobs.island;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.pathfinding.followers.NodeFollower;
import net.minestom.server.item.Material;
import net.swofty.commons.skyblock.item.ItemType;
import net.swofty.type.generic.gui.inventory.item.GUIMaterial;
import net.swofty.type.skyblockgeneric.entity.mob.MobType;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.MobBrain;
import net.swofty.type.skyblockgeneric.entity.pathfinder.goal.RandomFlightGoal;
import net.swofty.type.skyblockgeneric.entity.pathfinder.navigation.VanillaFlyingFollower;
import net.swofty.type.skyblockgeneric.loottable.SkyBlockLootTable;

import java.util.List;

public final class MobPrivateIslandBat extends PrivateIslandMob {
    public MobPrivateIslandBat() {
        super(EntityType.BAT, "Bat", "FOREST_ISLAND_BAT", new GUIMaterial(Material.BAT_SPAWN_EGG),
                MobType.AIRBORNE, 100, 0, 33, 100, 100);
        setNoGravity(true);
    }

    @Override
    public Integer getLevel() {
        return 3;
    }

    @Override
    protected void configureMobAttributes() {
        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.1);
        getAttribute(Attribute.FLYING_SPEED).setBaseValue(0.15);
        getAttribute(Attribute.FOLLOW_RANGE).setBaseValue(16);
    }

    @Override
    protected NodeFollower createNodeFollower() {
        return new VanillaFlyingFollower(this);
    }

    @Override
    protected void configureMobBrain(MobBrain brain) {
        brain.addGoal(1, new RandomFlightGoal(brain));
    }

    @Override
    public SkyBlockLootTable getLootTable() {
        return new PrivateIslandLootTable(new SkyBlockLootTable.LootRecord(ItemType.BAT_TALISMAN, 1, 1));
    }

    @Override
    public List<Populator> getPopulators() {
        return List.of(); // Spawned by the Roofed Forest Island's Bat Crystal, not naturally.
    }
}
