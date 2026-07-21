package net.swofty.type.skyblockgeneric.entity.pathfinder.goal;

import net.minestom.server.entity.Player;

import java.util.EnumSet;

public final class LookAtPlayerGoal extends MobGoal {
    private final MobBrain brain;
    private final float range;
    private final float probability;
    private Player player;
    private int remainingTicks;

    public LookAtPlayerGoal(MobBrain brain, float range) {
        this(brain, range, 0.02f);
    }

    public LookAtPlayerGoal(MobBrain brain, float range, float probability) {
        super(brain);
        this.brain = brain;
        this.range = range;
        this.probability = probability;
        setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (brain.random.nextFloat() >= probability) return false;
        player = null;
        double nearest = range * range;
        for (Player candidate : brain.mob.getInstance().getPlayers()) {
            double distance = candidate.getPosition().distanceSquared(brain.mob.getPosition());
            if (!candidate.isDead() && distance < nearest) {
                nearest = distance;
                player = candidate;
            }
        }
        return player != null;
    }

    @Override
    public boolean canContinueToUse() {
        return player != null && !player.isDead() && remainingTicks > 0
                && brain.mob.getPosition().distanceSquared(player.getPosition()) <= range * range;
    }

    @Override
    public void start() {
        remainingTicks = 40 + brain.random.nextInt(40);
    }

    @Override
    public void tick() {
        remainingTicks--;
        if (player != null) brain.lookAt(player);
    }
}
