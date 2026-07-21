package net.swofty.type.skyblockgeneric.entity.pathfinder.goal;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.LivingEntity;

import java.util.EnumSet;

/**
 * Vanilla slime move-control rhythm: face the target and hop in bursts.
 */
public final class SlimeHopGoal extends MobGoal {
    private final MobBrain brain;
    private final double horizontalVelocity;
    private final double jumpVelocity;
    private int jumpDelay;
    private int attackCooldown;

    public SlimeHopGoal(MobBrain brain, double horizontalVelocity, double jumpVelocity) {
        super(brain);
        this.brain = brain;
        this.horizontalVelocity = horizontalVelocity;
        this.jumpVelocity = jumpVelocity;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return true;
    }

    @Override
    public void tick() {
        LivingEntity target = brain.target;
        if (attackCooldown > 0) attackCooldown--;
        if (target != null && !target.isDead() && attackCooldown == 0
                && brain.isWithinMeleeAttackRange(target)) {
            brain.mob.attack(target, true);
            attackCooldown = 20;
        }
        if (!brain.mob.isOnGround() || --jumpDelay > 0) return;
        Vec direction;
        if (target != null && !target.isDead()) {
            brain.lookAt(target);
            direction = target.getPosition().sub(brain.mob.getPosition()).asVec();
            direction = direction.length() < 0.1 ? Vec.ZERO : direction.normalize();
            jumpDelay = (10 + brain.random.nextInt(20)) / 3;
        } else {
            double angle = brain.random.nextDouble() * Math.PI * 2;
            direction = new Vec(Math.cos(angle), 0, Math.sin(angle));
            jumpDelay = 10 + brain.random.nextInt(20);
        }
        brain.mob.setVelocity(new Vec(direction.x() * horizontalVelocity, jumpVelocity,
                direction.z() * horizontalVelocity));
    }
}
