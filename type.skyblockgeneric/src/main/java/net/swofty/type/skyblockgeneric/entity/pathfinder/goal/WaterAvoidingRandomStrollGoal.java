package net.swofty.type.skyblockgeneric.entity.pathfinder.goal;

import net.minestom.server.coordinate.Pos;

import java.util.EnumSet;

/**
 * Vanilla RandomStrollGoal using dry, standable destinations.
 */
public class WaterAvoidingRandomStrollGoal extends MobGoal {
    private final MobBrain brain;
    private final double speedModifier;
    private final int interval;
    private Pos destination;

    public WaterAvoidingRandomStrollGoal(MobBrain brain, double speedModifier) {
        this(brain, speedModifier, 120);
    }

    public WaterAvoidingRandomStrollGoal(MobBrain brain, double speedModifier, int interval) {
        super(brain);
        this.brain = brain;
        this.speedModifier = speedModifier;
        this.interval = interval;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (!brain.mob.getPassengers().isEmpty() || brain.target != null) return false;
        if (brain.random.nextInt(interval) != 0) return false;
        destination = findDestination();
        return destination != null;
    }

    private Pos findDestination() {
        var instance = brain.mob.getInstance();
        Pos origin = brain.mob.getPosition();
        for (int attempt = 0; attempt < 10; attempt++) {
            int x = origin.blockX() + brain.random.nextInt(21) - 10;
            int y = origin.blockY() + brain.random.nextInt(15) - 7;
            int z = origin.blockZ() + brain.random.nextInt(21) - 10;
            if (!instance.isChunkLoaded(x >> 4, z >> 4)) continue;
            int minY = instance.getCachedDimensionType().minY();
            for (int drop = 0; drop < 12 && y > minY && instance.getBlock(x, y - 1, z).isAir(); drop++) y--;
            var floor = instance.getBlock(x, y - 1, z);
            if (floor.isSolid() && !floor.isLiquid()
                    && instance.getBlock(x, y, z).isAir()
                    && instance.getBlock(x, y + 1, z).isAir()) {
                Pos candidate = new Pos(x + 0.5, y, z + 0.5);
                if (brain.isPositionAllowed(candidate)) return candidate;
            }
        }
        return null;
    }

    @Override
    public boolean canContinueToUse() {
        return brain.target == null && !brain.navigationDone();
    }

    @Override
    public void start() {
        brain.moveTo(destination, speedModifier);
    }

    @Override
    public void stop() {
        brain.stopNavigation();
    }
}
