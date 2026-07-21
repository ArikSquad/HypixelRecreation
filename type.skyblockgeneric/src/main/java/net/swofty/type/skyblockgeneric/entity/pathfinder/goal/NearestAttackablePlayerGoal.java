package net.swofty.type.skyblockgeneric.entity.pathfinder.goal;

import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;

import java.util.EnumSet;
import java.util.function.Predicate;

/**
 * Vanilla nearest-player targeting with sight memory and an optional filter.
 */
public class NearestAttackablePlayerGoal extends MobGoal {
    private final MobBrain brain;
    private final boolean mustSee;
    private final Predicate<Player> filter;
    private Player candidate;
    private int unseenTicks;

    public NearestAttackablePlayerGoal(MobBrain brain, boolean mustSee) {
        this(brain, mustSee, player -> true);
    }

    public NearestAttackablePlayerGoal(MobBrain brain, boolean mustSee, Predicate<Player> filter) {
        super(brain);
        this.brain = brain;
        this.mustSee = mustSee;
        this.filter = filter;
        setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (brain.random.nextInt(10) != 0) return false;
        candidate = null;
        double range = brain.followRange();
        double nearest = range * range;
        for (Player player : brain.mob.getInstance().getPlayers()) {
            if (!isAttackable(player) || !filter.test(player) || !brain.isPositionAllowed(player.getPosition()))
                continue;
            double distance = player.getPosition().distanceSquared(brain.mob.getPosition());
            if (distance < nearest && (!mustSee || brain.hasLineOfSight(player))) {
                nearest = distance;
                candidate = player;
            }
        }
        return candidate != null;
    }

    private boolean isAttackable(Player player) {
        return !player.isDead() && player.getGameMode() != GameMode.CREATIVE
                && player.getGameMode() != GameMode.SPECTATOR;
    }

    @Override
    public void start() {
        brain.setTarget(candidate);
        unseenTicks = 0;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = brain.target;
        if (!(target instanceof Player player) || !isAttackable(player) || !filter.test(player)
                || !brain.isPositionAllowed(player.getPosition())) return false;
        double range = brain.followRange();
        if (brain.mob.getPosition().distanceSquared(player.getPosition()) > range * range) return false;
        if (!mustSee) return true;
        if (brain.hasLineOfSight(player)) unseenTicks = 0;
        else unseenTicks++;
        return unseenTicks <= 60;
    }

    @Override
    public void stop() {
        brain.setTarget(null);
        candidate = null;
    }
}
