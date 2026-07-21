package net.swofty.type.skyblockgeneric.entity.pathfinder.goal;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

import java.util.EnumSet;

public final class PanicGoal extends MobGoal {
    private final MobBrain brain;
    private final double speedModifier;

    public PanicGoal(MobBrain brain, double speedModifier) {
        super(brain);
        this.brain = brain;
        this.speedModifier = speedModifier;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        return brain.lastHurtTimestamp >= 0 && brain.gameTime - brain.lastHurtTimestamp < 100;
    }

    @Override
    public boolean canContinueToUse() {
        return brain.gameTime - brain.lastHurtTimestamp < 100 && !brain.navigationDone();
    }

    @Override
    public void start() {
        Pos origin = brain.mob.getPosition();
        Vec away = brain.lastHurtBy == null
                ? new Vec(brain.random.nextInt(11) - 5, 0, brain.random.nextInt(11) - 5)
                : origin.sub(brain.lastHurtBy.getPosition()).asVec().normalize().mul(5);
        brain.moveTo(origin.add(away.x(), 0, away.z()), speedModifier);
    }

    @Override
    public void stop() {
        brain.stopNavigation();
    }
}
