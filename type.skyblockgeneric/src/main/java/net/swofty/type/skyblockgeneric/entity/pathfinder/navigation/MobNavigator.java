package net.swofty.type.skyblockgeneric.entity.pathfinder.navigation;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.pathfinding.followers.NodeFollower;
import net.swofty.type.skyblockgeneric.entity.pathfinder.MobPathFinder;
import net.swofty.type.skyblockgeneric.region.RegionType;

import java.util.List;

/**
 * Navigation state for {@link EntityCreature} instances using the custom
 * vanilla-inspired A* implementation. This class never touches Minestom's
 * native Navigator or PathGenerator.
 */
public final class MobNavigator {
    private final EntityCreature mob;
    private final double baseSpeed;
    private final MobPathFinder.Settings settings;
    private final NodeFollower follower;

    private List<Point> path;
    private int pathIndex;
    private Point destination;

    public MobNavigator(EntityCreature mob, NodeFollower follower, java.util.Set<RegionType> allowedRegions) {
        this.mob = mob;
        this.baseSpeed = mob.getAttributeValue(Attribute.MOVEMENT_SPEED);
        var boundingBox = mob.getBoundingBox();
        this.settings = new MobPathFinder.Settings(
                (float) boundingBox.width(), (float) boundingBox.height(), false, allowedRegions);
        this.follower = follower;
    }

    public void moveTo(Point target, double speedModifier, float maxPathLength) {
        mob.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(baseSpeed * speedModifier);
        if (mob.getInstance() == null) return;
        if (destination != null && path != null && !isDone()
                && destination.distanceSquared(target) < 1) return;

        path = MobPathFinder.findPath(mob.getInstance(), mob, settings, target, maxPathLength);
        pathIndex = 0;
        destination = target;
    }

    public void stop() {
        mob.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(baseSpeed);
        path = null;
        pathIndex = 0;
        destination = null;
    }

    public boolean isDone() {
        return path == null || pathIndex >= path.size();
    }

    public Point destination() {
        return destination;
    }

    /**
     * Moves directly in 3D using the installed follower (used by flying goals).
     */
    public void moveDirect(Point target) {
        follower.moveTowards(target, follower.movementSpeed(), target);
    }

    public void tick() {
        if (path == null || isDone()) {
            if (path != null) stop();
            return;
        }

        Point waypoint = path.get(pathIndex);
        while (follower.isAtPoint(waypoint)) {
            if (++pathIndex >= path.size()) {
                stop();
                return;
            }
            waypoint = path.get(pathIndex);
        }
        Point steeringTarget = waypoint;
        if (pathIndex + 1 < path.size()) {
            Point next = path.get(pathIndex + 1);
            double reach = Math.max(0.55, mob.getBoundingBox().width() * 0.75);
            if (mob.getPosition().distanceSquared(waypoint) <= reach * reach) {
                steeringTarget = next;
            }
        }
        if (waypoint.y() > mob.getPosition().y() + 0.45 && mob.isOnGround()) {
            follower.jump(waypoint, steeringTarget);
        }
        follower.moveTowards(steeringTarget, follower.movementSpeed(), steeringTarget);
    }
}
