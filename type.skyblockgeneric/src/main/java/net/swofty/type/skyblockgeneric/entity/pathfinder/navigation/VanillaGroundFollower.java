package net.swofty.type.skyblockgeneric.entity.pathfinder.navigation;

import net.minestom.server.collision.CollisionUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.pathfinding.followers.NodeFollower;
import net.minestom.server.utils.position.PositionUtils;
import org.jetbrains.annotations.Nullable;

public final class VanillaGroundFollower implements NodeFollower {
    private static final double VERTICAL_EPSILON = 0.55;
    private static final double JUMP_VELOCITY = 8.4;

    private final Entity entity;
    private final double horizontalEpsilonSquared;
    private Pos previousPosition;
    private int stationaryTicks;

    public VanillaGroundFollower(Entity entity) {
        this.entity = entity;
        double epsilon = Math.clamp(entity.getBoundingBox().width() * 0.6, 0.35, 0.65);
        this.horizontalEpsilonSquared = epsilon * epsilon;
    }

    @Override
    public void moveTowards(Point target, double speed, Point lookAt) {
        Pos position = entity.getPosition();
        double dx = target.x() - position.x();
        double dz = target.z() - position.z();
        double horizontalSquared = dx * dx + dz * dz;
        if (horizontalSquared < horizontalEpsilonSquared) return;

        double horizontal = Math.sqrt(horizontalSquared);
        double movement = Math.min(speed, horizontal);
        Vec requested = new Vec(dx / horizontal * movement, 0, dz / horizontal * movement);
        var physics = CollisionUtils.handlePhysics(entity, requested);

        double lookX = lookAt.x() - position.x();
        double lookZ = lookAt.z() - position.z();
        float yaw = approachAngle(position.yaw(), PositionUtils.getLookYaw(lookX, lookZ), 30.0f);
        // Goals own vertical aiming. Navigation only turns the body horizontally,
        // otherwise a feet-level path node makes pursuing mobs stare at the floor.
        Pos next = physics.newPosition().asPos().withView(yaw, position.pitch());
        entity.refreshPosition(next);

        if (previousPosition != null && previousPosition.distanceSquared(next) < 0.0004) {
            stationaryTicks++;
        } else {
            stationaryTicks = 0;
        }
        previousPosition = next;
        if (stationaryTicks >= 3 && entity.isOnGround() && target.y() > next.y() + 0.2) {
            jump(target, lookAt);
            stationaryTicks = 0;
        }
    }

    private static float approachAngle(float current, float target, float maximumChange) {
        float delta = (target - current) % 360.0f;
        if (delta >= 180.0f) delta -= 360.0f;
        if (delta < -180.0f) delta += 360.0f;
        return current + Math.clamp(delta, -maximumChange, maximumChange);
    }

    @Override
    public void jump(@Nullable Point point, @Nullable Point target) {
        if (!entity.isOnGround()) return;
        Vec velocity = entity.getVelocity();
        entity.setVelocity(new Vec(velocity.x(), JUMP_VELOCITY, velocity.z()));
    }

    @Override
    public boolean isAtPoint(Point point) {
        Pos position = entity.getPosition();
        double dx = point.x() - position.x();
        double dz = point.z() - position.z();
        return dx * dx + dz * dz <= horizontalEpsilonSquared
                && Math.abs(point.y() - position.y()) <= VERTICAL_EPSILON;
    }

    @Override
    public double movementSpeed() {
        return entity instanceof LivingEntity living
                ? living.getAttributeValue(Attribute.MOVEMENT_SPEED)
                : 0.1;
    }
}
