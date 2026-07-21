package net.swofty.type.skyblockgeneric.entity.pathfinder.goal;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;

import java.util.EnumSet;

/**
 * Targets players looking at the enderman's upper body.
 */
public final class EndermanStareTargetGoal extends MobGoal {
    private final MobBrain brain;
    private Player candidate;

    public EndermanStareTargetGoal(MobBrain brain) {
        super(brain);
        this.brain = brain;
        setFlags(EnumSet.of(Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        candidate = null;
        double nearest = 64 * 64;
        for (Player player : brain.mob.getInstance().getPlayers()) {
            if (player.isDead() || player.getGameMode() == GameMode.CREATIVE
                    || player.getGameMode() == GameMode.SPECTATOR || !isStaring(player)) continue;
            double distance = player.getPosition().distanceSquared(brain.mob.getPosition());
            if (distance < nearest) {
                nearest = distance;
                candidate = player;
            }
        }
        return candidate != null;
    }

    private boolean isStaring(Player player) {
        Pos mobPosition = brain.mob.getPosition();
        Pos playerPosition = player.getPosition();
        Vec toMob = new Vec(mobPosition.x() - playerPosition.x(),
                mobPosition.y() + 2.55 - playerPosition.y() - player.getEyeHeight(),
                mobPosition.z() - playerPosition.z());
        double distance = toMob.length();
        if (distance < 0.1 || distance > 64) return false;
        return playerPosition.direction().dot(toMob.normalize()) > 0.985 && brain.hasLineOfSight(player);
    }

    @Override
    public void start() {
        brain.setTarget(candidate);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = brain.target;
        return target != null && !target.isDead()
                && brain.mob.getPosition().distanceSquared(target.getPosition()) < 64 * 64;
    }

    @Override
    public void stop() {
        brain.setTarget(null);
    }
}
