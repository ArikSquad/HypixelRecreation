package net.swofty.type.skyblockgeneric.entity.pathfinder.goal;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityProjectile;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;

import java.util.EnumSet;

/**
 * Skeleton bow goal with vanilla-style pursuit, strafing, and sight checks.
 */
public final class BowAttackGoal extends MobGoal {
    private final MobBrain brain;
    private final double speedModifier;
    private final int attackInterval;
    private final float attackRadius;
    private int attackCooldown;
    private int strafingTicks;
    private boolean clockwise;

    public BowAttackGoal(MobBrain brain, double speedModifier, int attackInterval, float attackRadius) {
        super(brain);
        this.brain = brain;
        this.speedModifier = speedModifier;
        this.attackInterval = attackInterval;
        this.attackRadius = attackRadius;
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
    public void stop() {
        brain.stopNavigation();
        attackCooldown = 0;
        strafingTicks = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = brain.target;
        if (target == null) return;
        double distanceSquared = brain.mob.getPosition().distanceSquared(target.getPosition());
        boolean visible = brain.hasLineOfSight(target);

        if (visible && distanceSquared <= attackRadius * attackRadius) {
            brain.stopNavigation();
            if (++strafingTicks > 20 && brain.random.nextFloat() < 0.3f) clockwise = !clockwise;
            Vec direction = target.getPosition().sub(brain.mob.getPosition()).asVec();
            Vec sideways = new Vec(-direction.z(), 0, direction.x()).normalize().mul(clockwise ? 0.7 : -0.7);
            brain.mob.setVelocity(brain.mob.getVelocity().add(sideways));
        } else {
            brain.moveTo(target.getPosition(), speedModifier);
            strafingTicks = 0;
        }

        brain.lookAt(target);
        if (attackCooldown > 0) attackCooldown--;
        if (attackCooldown == 0 && visible) {
            attackCooldown = attackInterval;
            EntityProjectile arrow = new EntityProjectile(brain.mob, EntityType.ARROW);
            Pos origin = brain.mob.getPosition().add(0, brain.mob.getEyeHeight() - 0.1, 0);
            arrow.setInstance(brain.mob.getInstance(), origin);
            Vec velocity = target.getPosition().add(0, target.getEyeHeight() / 2, 0)
                    .sub(origin).asVec().normalize().mul(32).add(0, 3, 0);
            arrow.setVelocity(velocity);
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
