package net.swofty.type.skyblockgeneric.entity.pathfinder.goal;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;

import java.util.EnumSet;

/**
 * Retaliates against the newest attacker and optionally alerts nearby allies.
 */
public final class HurtByTargetGoal extends MobGoal {
    private final MobBrain brain;
    private final boolean alertAllies;
    private long handledTimestamp = -1;

    public HurtByTargetGoal(MobBrain brain, boolean alertAllies) {
        super(brain);
        this.brain = brain;
        this.alertAllies = alertAllies;
        setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        return brain.lastHurtTimestamp != handledTimestamp
                && brain.lastHurtBy != null && !brain.lastHurtBy.isDead()
                && brain.isPositionAllowed(brain.lastHurtBy.getPosition());
    }

    @Override
    public void start() {
        handledTimestamp = brain.lastHurtTimestamp;
        brain.setTarget(brain.lastHurtBy);
        if (!alertAllies) return;

        double rangeSquared = brain.followRange() * brain.followRange();
        for (Entity entity : brain.mob.getInstance().getEntities()) {
            if (entity == brain.mob || entity.getEntityType() != brain.mob.getEntityType()) continue;
            if (entity.getPosition().distanceSquared(brain.mob.getPosition()) > rangeSquared) continue;
            if (Math.abs(entity.getPosition().y() - brain.mob.getPosition().y()) > 10) continue;
            MobBrain ally = MobBrain.of(entity);
            if (ally != null && ally.target == null) ally.setTarget(brain.lastHurtBy);
        }
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = brain.target;
        if (target == null || target.isDead() || !brain.isPositionAllowed(target.getPosition())) return false;
        double range = brain.followRange();
        return brain.mob.getPosition().distanceSquared(target.getPosition()) <= range * range;
    }

    @Override
    public void stop() {
        if (brain.target == brain.lastHurtBy) brain.setTarget(null);
    }
}
