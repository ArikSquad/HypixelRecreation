package net.swofty.type.skyblockgeneric.entity.pathfinder.goal;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.LivingEntity;

import java.util.EnumSet;

public final class LeapAtTargetGoal extends MobGoal {
    private final MobBrain brain;
    private final float verticalVelocity;

    public LeapAtTargetGoal(MobBrain brain, float verticalVelocity) {
        super(brain);
        this.brain = brain;
        this.verticalVelocity = verticalVelocity;
        setFlags(EnumSet.of(Flag.JUMP, Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = brain.target;
        if (target == null || !brain.mob.isOnGround()) return false;
        double distance = brain.mob.getPosition().distanceSquared(target.getPosition());
        return distance >= 4 && distance <= 16 && brain.random.nextInt(5) == 0;
    }

    @Override
    public boolean canContinueToUse() {
        return !brain.mob.isOnGround();
    }

    @Override
    public void start() {
        LivingEntity target = brain.target;
        if (target == null) return;
        Vec delta = target.getPosition().sub(brain.mob.getPosition()).asVec();
        Vec horizontal = new Vec(delta.x(), 0, delta.z());
        if (horizontal.length() < 0.01) return;
        Vec velocity = horizontal.normalize().mul(8).add(brain.mob.getVelocity().mul(0.2));
        brain.mob.setVelocity(new Vec(velocity.x(), verticalVelocity * 20, velocity.z()));
    }
}
