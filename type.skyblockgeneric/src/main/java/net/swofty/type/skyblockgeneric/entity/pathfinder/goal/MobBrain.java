package net.swofty.type.skyblockgeneric.entity.pathfinder.goal;

import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.pathfinding.followers.NodeFollower;
import net.minestom.server.instance.Instance;
import net.minestom.server.timer.TaskSchedule;
import net.swofty.type.skyblockgeneric.entity.mob.impl.RegionPopulator;
import net.swofty.type.skyblockgeneric.entity.pathfinder.navigation.MobNavigator;
import net.swofty.type.skyblockgeneric.region.RegionType;
import net.swofty.type.skyblockgeneric.region.SkyBlockRegion;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class MobBrain {
    private static final Map<Integer, MobBrain> BRAINS = new ConcurrentHashMap<>();

    public final EntityCreature mob;
    public final Random random = new Random();

    private final List<Wrapped> goals = new ArrayList<>();
    private final List<Wrapped> targetGoals = new ArrayList<>();
    private final Map<MobGoal.Flag, Wrapped> lockedFlags = new EnumMap<>(MobGoal.Flag.class);

    public LivingEntity target;
    public LivingEntity lastHurtBy;
    public long lastHurtTimestamp = -1;
    public long gameTime;
    public boolean aggressive;
    private final MobNavigator navigator;
    private final Set<RegionType> allowedRegions;

    private static final class Wrapped {
        final int priority;
        final MobGoal goal;
        boolean running;

        Wrapped(int priority, MobGoal goal) {
            this.priority = priority;
            this.goal = goal;
        }

        boolean canBeReplacedBy(Wrapped other) {
            return goal.isInterruptable() && other.priority < priority;
        }
    }

    public MobBrain(EntityCreature mob, NodeFollower follower) {
        this.mob = mob;
        this.allowedRegions = mob instanceof RegionPopulator populator
                ? populator.getPopulators().stream().map(RegionPopulator.Populator::regionType).collect(Collectors.toUnmodifiableSet())
                : Set.of();
        this.navigator = new MobNavigator(mob, follower, allowedRegions);
        BRAINS.put(mob.getEntityId(), this);
        mob.scheduler().buildTask(this::tick).repeat(TaskSchedule.tick(1)).schedule();
    }

    public EntityCreature mob() {
        return mob;
    }

    public static MobBrain of(net.minestom.server.entity.Entity entity) {
        return BRAINS.get(entity.getEntityId());
    }

    public void addGoal(int priority, MobGoal goal) {
        goals.add(new Wrapped(priority, goal));
        goals.sort(java.util.Comparator.comparingInt(wrapped -> wrapped.priority));
    }

    public void addTargetGoal(int priority, MobGoal goal) {
        targetGoals.add(new Wrapped(priority, goal));
        targetGoals.sort(java.util.Comparator.comparingInt(wrapped -> wrapped.priority));
    }

    private void tick() {
        if (mob.isRemoved() || mob.isDead()) {
            BRAINS.remove(mob.getEntityId(), this);
            return;
        }
        if (mob.getInstance() == null) return;
        gameTime++;
        tickSelector(targetGoals);
        tickSelector(goals);
        navigator.tick();
    }

    /**
     * The vanilla GoalSelector.tick() algorithm.
     */
    private void tickSelector(List<Wrapped> selector) {
        for (Wrapped w : selector) {
            if (w.running && !w.goal.canContinueToUse()) {
                w.running = false;
                w.goal.stop();
            }
        }
        lockedFlags.entrySet().removeIf(e -> !e.getValue().running);

        for (Wrapped w : selector) {
            if (w.running || !canBeReplacedForAllFlags(w) || !w.goal.canUse()) continue;
            for (MobGoal.Flag flag : w.goal.getFlags()) {
                Wrapped current = lockedFlags.get(flag);
                if (current != null && current.running) {
                    current.running = false;
                    current.goal.stop();
                }
                lockedFlags.put(flag, w);
            }
            w.running = true;
            w.goal.start();
        }

        for (Wrapped w : selector) {
            if (w.running) w.goal.tick();
        }
    }

    private boolean canBeReplacedForAllFlags(Wrapped candidate) {
        for (MobGoal.Flag flag : candidate.goal.getFlags()) {
            Wrapped current = lockedFlags.get(flag);
            if (current != null && current.running && !current.canBeReplacedBy(candidate)) return false;
        }
        return true;
    }


    public void hurtBy(LivingEntity attacker) {
        this.lastHurtBy = attacker;
        this.lastHurtTimestamp = gameTime;
    }

    public void setTarget(LivingEntity target) {
        this.target = target == null || isPositionAllowed(target.getPosition()) ? target : null;
    }

    /**
     * Empty means unrestricted; populated mobs may only choose and traverse their declared regions.
     */
    public boolean isPositionAllowed(Point point) {
        if (allowedRegions.isEmpty()) return true;
        SkyBlockRegion region = SkyBlockRegion.getRegionOfPosition(point);
        return region != null && allowedRegions.contains(region.getType());
    }

    public double followRange() {
        double value = mob.getAttributeValue(Attribute.FOLLOW_RANGE);
        return value > 0 ? value : 16;
    }

    public boolean hasLineOfSight(LivingEntity other) {
        Instance instance = mob.getInstance();
        if (instance == null || other.getInstance() != instance) return false;
        Vec from = mob.getPosition().add(0, mob.getEyeHeight(), 0).asVec();
        Vec to = other.getPosition().add(0, other.getEyeHeight(), 0).asVec();
        Vec delta = to.sub(from);
        double length = delta.length();
        if (length < 0.01) return true;
        Vec step = delta.normalize().mul(0.5);
        Vec at = from;
        for (double d = 0; d < length; d += 0.5) {
            at = at.add(step);
            if (!instance.isChunkLoaded(at.blockX() >> 4, at.blockZ() >> 4)) return false;
            var block = instance.getBlock(at);
            if (block.isSolid() && block.registry().occludes()) return false;
        }
        return true;
    }

    public void lookAt(Point point) {
        Pos pos = mob.getPosition();
        double dx = point.x() - pos.x();
        double dy = point.y() - (pos.y() + mob.getEyeHeight());
        double dz = point.z() - pos.z();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90f;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, horizontal));
        mob.setView(yaw, pitch);
    }

    public void lookAt(LivingEntity entity) {
        lookAt(entity.getPosition().add(0, entity.getEyeHeight(), 0));
    }

    // navigation with vanilla speed modifiers, routed by the vanilla A* port
    public void moveTo(Point point, double speedModifier) {
        if (isPositionAllowed(point)) navigator.moveTo(point, speedModifier, (float) followRange());
    }

    public void moveDirect(Point point) {
        navigator.moveDirect(point);
    }

    public void stopNavigation() {
        navigator.stop();
    }

    public boolean navigationDone() {
        return navigator.isDone();
    }

    public Point navigationGoal() {
        return navigator.destination();
    }

    public boolean isWithinMeleeAttackRange(LivingEntity other) {
        double reach = mob.getEntityType().registry().width() / 2 + 0.8
                + other.getEntityType().registry().width() / 2;
        double dx = mob.getPosition().x() - other.getPosition().x();
        double dz = mob.getPosition().z() - other.getPosition().z();
        double dy = Math.abs(mob.getPosition().y() - other.getPosition().y());
        return dx * dx + dz * dz <= reach * reach && dy <= 2.0;
    }
}
