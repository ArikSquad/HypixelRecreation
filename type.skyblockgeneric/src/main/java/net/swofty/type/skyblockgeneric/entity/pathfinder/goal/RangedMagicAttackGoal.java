package net.swofty.type.skyblockgeneric.entity.pathfinder.goal;

import net.minestom.server.entity.LivingEntity;

import java.util.EnumSet;

/**
 * Ranged pursuit used by potion-throwing mobs such as witches.
 */
public final class RangedMagicAttackGoal extends MobGoal {
    private final MobBrain brain;
    private final double speedModifier;
    private final float preferredRange;
    private final int attackInterval;
    private int cooldown;

    public RangedMagicAttackGoal(MobBrain brain, double speedModifier, float preferredRange,
                                 int attackInterval) {
        super(brain);
        this.brain = brain;
        this.speedModifier = speedModifier;
        this.preferredRange = preferredRange;
        this.attackInterval = attackInterval;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return brain.target != null && !brain.target.isDead();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void tick() {
        LivingEntity target = brain.target;
        if (target == null) return;
        brain.lookAt(target);
        double distanceSquared = brain.mob.getPosition().distanceSquared(target.getPosition());
        if (distanceSquared > preferredRange * preferredRange) brain.moveTo(target.getPosition(), speedModifier);
        else brain.stopNavigation();
        if (cooldown > 0) cooldown--;
        if (cooldown == 0 && distanceSquared <= preferredRange * preferredRange && brain.hasLineOfSight(target)) {
            cooldown = attackInterval;
            brain.mob.attack(target, true);
        }
    }

    @Override
    public void stop() {
        brain.stopNavigation();
    }
}
