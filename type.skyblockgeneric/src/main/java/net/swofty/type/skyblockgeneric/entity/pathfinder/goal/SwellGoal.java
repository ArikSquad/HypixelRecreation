package net.swofty.type.skyblockgeneric.entity.pathfinder.goal;

import net.minestom.server.entity.LivingEntity;

import java.util.EnumSet;
import java.util.function.IntConsumer;

/**
 * Controls the creeper fuse direction; the mob implementation owns detonation.
 */
public final class SwellGoal extends MobGoal {
    private final MobBrain brain;
    private final IntConsumer swellDirection;
    private LivingEntity target;

    public SwellGoal(MobBrain brain, IntConsumer swellDirection) {
        super(brain);
        this.brain = brain;
        this.swellDirection = swellDirection;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity current = brain.target;
        return current != null && !current.isDead()
                && brain.mob.getPosition().distanceSquared(current.getPosition()) < 9;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && !target.isDead();
    }

    @Override
    public void start() {
        brain.stopNavigation();
        target = brain.target;
    }

    @Override
    public void tick() {
        boolean defuse = target == null || target.isDead()
                || brain.mob.getPosition().distanceSquared(target.getPosition()) > 49
                || !brain.hasLineOfSight(target);
        swellDirection.accept(defuse ? -1 : 1);
    }

    @Override
    public void stop() {
        target = null;
        swellDirection.accept(-1);
    }
}
