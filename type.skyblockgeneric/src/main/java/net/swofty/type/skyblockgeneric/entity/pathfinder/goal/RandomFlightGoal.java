package net.swofty.type.skyblockgeneric.entity.pathfinder.goal;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Vec;

import java.util.EnumSet;

/**
 * Lightweight ambient flight for bats; does not use ground navigation.
 */
public final class RandomFlightGoal extends MobGoal {
    private final MobBrain brain;
    private Point target;
    private int chooseTargetIn;

    public RandomFlightGoal(MobBrain brain) {
        super(brain);
        this.brain = brain;
        setFlags(EnumSet.of(Flag.MOVE));
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
        if (target == null || --chooseTargetIn <= 0
                || brain.mob.getPosition().distanceSquared(target) < 1.0) {
            chooseTargetIn = 30 + brain.random.nextInt(30);
            var origin = brain.mob.getPosition();
            target = new Vec(
                    origin.x() + brain.random.nextInt(15) - 7,
                    origin.y() + brain.random.nextInt(7) - 2,
                    origin.z() + brain.random.nextInt(15) - 7);
        }
        brain.moveDirect(target);
    }
}
