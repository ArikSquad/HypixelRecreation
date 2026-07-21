package net.swofty.type.skyblockgeneric.entity.pathfinder.goal;

import lombok.Getter;
import net.minestom.server.entity.ai.GoalSelector;

import java.util.EnumSet;

@Getter
/**
 * A Minestom goal with vanilla's lifecycle and control flags.
 *
 * <p>Minestom's {@link GoalSelector} only permits one running selector per AI
 * group. Vanilla permits compatible goals (for example walking and looking) to
 * run together, so {@link MobBrain} performs selection while this class keeps
 * every goal compatible with Minestom's public goal API.</p>
 */
public abstract class MobGoal extends GoalSelector {
    public enum Flag {MOVE, LOOK, JUMP, TARGET}

    private EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);

    protected MobGoal(MobBrain brain) {
        super(brain.mob());
    }

    protected void setFlags(EnumSet<Flag> flags) {
        this.flags = flags;
    }

    public abstract boolean canUse();

    public boolean canContinueToUse() {
        return canUse();
    }

    public boolean isInterruptable() {
        return true;
    }

    public void start() {
    }

    public void stop() {
    }

    public void tick() {
    }

    public boolean requiresUpdateEveryTick() {
        return false;
    }

    @Override
    public final boolean shouldStart() {
        return canUse();
    }

    @Override
    public final boolean shouldEnd() {
        return !canContinueToUse();
    }

    @Override
    public final void tick(long time) {
        tick();
    }

    @Override
    public final void end() {
        stop();
    }
}
