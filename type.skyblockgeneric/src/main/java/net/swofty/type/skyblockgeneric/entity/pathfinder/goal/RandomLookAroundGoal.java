package net.swofty.type.skyblockgeneric.entity.pathfinder.goal;

import java.util.EnumSet;

public final class RandomLookAroundGoal extends MobGoal {
    private final MobBrain brain;
    private double xDirection;
    private double zDirection;
    private int remainingTicks;

    public RandomLookAroundGoal(MobBrain brain) {
        super(brain);
        this.brain = brain;
        setFlags(EnumSet.of(Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return brain.random.nextFloat() < 0.02f;
    }

    @Override
    public boolean canContinueToUse() {
        return remainingTicks > 0;
    }

    @Override
    public void start() {
        double angle = brain.random.nextDouble() * Math.PI * 2;
        xDirection = Math.cos(angle);
        zDirection = Math.sin(angle);
        remainingTicks = 20 + brain.random.nextInt(20);
    }

    @Override
    public void tick() {
        remainingTicks--;
        var position = brain.mob.getPosition();
        brain.lookAt(position.add(xDirection, brain.mob.getEyeHeight(), zDirection));
    }
}
