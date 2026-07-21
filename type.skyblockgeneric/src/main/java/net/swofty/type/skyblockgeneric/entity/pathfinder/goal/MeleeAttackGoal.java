package net.swofty.type.skyblockgeneric.entity.pathfinder.goal;

import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;

import java.util.EnumSet;

/**
 * Vanilla-style path-recalculating melee pursuit.
 */
public class MeleeAttackGoal extends MobGoal {
    protected final MobBrain brain;
    private final double speedModifier;
    private final boolean followWithoutSight;
    private long lastUseCheck;
    private int repathDelay;
    private int attackCooldown;

    public MeleeAttackGoal(MobBrain brain, double speedModifier, boolean followWithoutSight) {
        super(brain);
        this.brain = brain;
        this.speedModifier = speedModifier;
        this.followWithoutSight = followWithoutSight;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (brain.gameTime - lastUseCheck < 20) return false;
        lastUseCheck = brain.gameTime;
        return isValidTarget(brain.target);
    }

    @Override
    public boolean canContinueToUse() {
        if (!isValidTarget(brain.target)) return false;
        return followWithoutSight || !brain.navigationDone();
    }

    private boolean isValidTarget(LivingEntity target) {
        if (target == null || target.isDead() || !brain.isPositionAllowed(target.getPosition())) return false;
        return !(target instanceof Player player)
                || player.getGameMode() == GameMode.SURVIVAL
                || player.getGameMode() == GameMode.ADVENTURE;
    }

    @Override
    public void start() {
        brain.aggressive = true;
        repathDelay = 0;
        attackCooldown = 0;
    }

    @Override
    public void stop() {
        brain.aggressive = false;
        brain.stopNavigation();
    }

    @Override
    public void tick() {
        LivingEntity target = brain.target;
        if (!isValidTarget(target)) return;
        brain.lookAt(target);

        if (--repathDelay <= 0 && (followWithoutSight || brain.hasLineOfSight(target))) {
            double distance = brain.mob.getPosition().distance(target.getPosition());
            repathDelay = 4 + brain.random.nextInt(7);
            if (distance > 32) repathDelay += 10;
            else if (distance > 16) repathDelay += 5;
            brain.moveTo(target.getPosition(), speedModifier);
        }

        if (attackCooldown > 0) attackCooldown--;
        if (attackCooldown == 0 && brain.isWithinMeleeAttackRange(target)) {
            attackCooldown = 20;
            attack(target);
        }
    }

    protected void attack(LivingEntity target) {
        brain.mob.attack(target, true);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
