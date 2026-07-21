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

public final class VanillaFlyingFollower implements NodeFollower {
    private final Entity entity;

    public VanillaFlyingFollower(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void moveTowards(Point target, double speed, Point lookAt) {
        Pos position = entity.getPosition();
        Vec delta = new Vec(target.x() - position.x(), target.y() - position.y(), target.z() - position.z());
        double distance = delta.length();
        if (distance < 0.3) return;
        var physics = CollisionUtils.handlePhysics(entity, delta.normalize().mul(Math.min(speed, distance)));
        double lookX = lookAt.x() - position.x();
        double lookY = lookAt.y() - (position.y() + entity.getEyeHeight());
        double lookZ = lookAt.z() - position.z();
        entity.refreshPosition(physics.newPosition().asPos().withView(
                PositionUtils.getLookYaw(lookX, lookZ),
                PositionUtils.getLookPitch(lookX, lookY, lookZ)));
    }

    @Override
    public void jump(@Nullable Point point, @Nullable Point target) {
    }

    @Override
    public boolean isAtPoint(Point point) {
        return entity.getPosition().distanceSquared(point) < 0.35 * 0.35;
    }

    @Override
    public double movementSpeed() {
        return entity instanceof LivingEntity living
                ? living.getAttributeValue(Attribute.FLYING_SPEED) : 0.1;
    }
}
