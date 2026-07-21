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

/**
 * Ground steering with vanilla spider-style wall climbing.
 */
public final class SpiderNodeFollower implements NodeFollower {
    private static final double JUMP_VELOCITY = 8.4;
    private static final double CLIMB_VELOCITY = 4.0;
    private final Entity entity;
    private final double reachSquared;

    public SpiderNodeFollower(Entity entity) {
        this.entity = entity;
        double reach = Math.clamp(entity.getBoundingBox().width() * 0.6, 0.4, 0.75);
        this.reachSquared = reach * reach;
    }

    @Override
    public void moveTowards(Point target, double speed, Point lookAt) {
        Pos position = entity.getPosition();
        double dx = target.x() - position.x();
        double dz = target.z() - position.z();
        double distance = Math.sqrt(dx * dx + dz * dz);
        if (distance < 1.0E-4) return;

        double movement = Math.min(speed, distance);
        Vec requested = new Vec(dx / distance * movement, 0, dz / distance * movement);
        var physics = CollisionUtils.handlePhysics(entity, requested);
        Pos moved = physics.newPosition().asPos();
        double actualHorizontal = square(moved.x() - position.x()) + square(moved.z() - position.z());

        float yaw = PositionUtils.getLookYaw(lookAt.x() - position.x(), lookAt.z() - position.z());
        entity.refreshPosition(moved.withView(yaw, position.pitch()));

        // Spiders treat a horizontal collision as a climbable surface. Keep the
        // horizontal steering so they leave the wall when the route turns away.
        if (actualHorizontal < movement * movement * 0.15) {
            Vec velocity = entity.getVelocity();
            entity.setVelocity(new Vec(velocity.x(), Math.max(velocity.y(), CLIMB_VELOCITY), velocity.z()));
        }
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
        return square(point.x() - position.x()) + square(point.z() - position.z()) <= reachSquared
                && Math.abs(point.y() - position.y()) <= 0.65;
    }

    @Override
    public double movementSpeed() {
        return entity instanceof LivingEntity living ? living.getAttributeValue(Attribute.MOVEMENT_SPEED) : 0.1;
    }

    private static double square(double value) {
        return value * value;
    }
}
