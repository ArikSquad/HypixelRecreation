package net.swofty.type.generic.entity.drop;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.random.RandomGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemDropPhysicsTest {
    private static final double EPSILON = 1.0e-9;

    @Test
    void throwsStraightAheadWhenLookingAtTheHorizonWithoutSpread() {
        Vec velocity = ItemDropPhysics.throwVelocity(0f, 0f, noSpread());

        assertEquals(0.0, velocity.x(), EPSILON);
        assertEquals(ItemDropPhysics.THROW_UPWARDS * 20.0, velocity.y(), EPSILON);
        assertEquals(ItemDropPhysics.THROW_SPEED * 20.0, velocity.z(), EPSILON);
    }

    @Test
    void throwsTowardsNegativeXWhenYawIsNinety() {
        Vec velocity = ItemDropPhysics.throwVelocity(90f, 0f, noSpread());

        assertEquals(-ItemDropPhysics.THROW_SPEED * 20.0, velocity.x(), EPSILON);
        assertEquals(0.0, velocity.z(), EPSILON);
    }

    @Test
    void throwsStraightUpWhenLookingUp() {
        Vec velocity = ItemDropPhysics.throwVelocity(37f, -90f, noSpread());

        assertEquals(0.0, velocity.x(), EPSILON);
        assertEquals(0.0, velocity.z(), EPSILON);
        assertEquals((ItemDropPhysics.THROW_SPEED + ItemDropPhysics.THROW_UPWARDS) * 20.0, velocity.y(), EPSILON);
    }

    @Test
    void throwVelocityMatchesTheLookDirectionScaledByTheVanillaThrowSpeed() {
        float yaw = -143.5f;
        float pitch = 22.25f;

        Vec perTick = ItemDropPhysics.perTick(ItemDropPhysics.throwVelocity(yaw, pitch, noSpread()));
        Vec expected = new Pos(0, 0, 0, yaw, pitch).direction()
                .mul(ItemDropPhysics.THROW_SPEED)
                .add(0, ItemDropPhysics.THROW_UPWARDS, 0);

        assertEquals(expected.x(), perTick.x(), 1.0e-7);
        assertEquals(expected.y(), perTick.y(), 1.0e-7);
        assertEquals(expected.z(), perTick.z(), 1.0e-7);
    }

    @Test
    void throwSpreadIsAppliedOnTopOfTheLookDirection() {
        Vec velocity = ItemDropPhysics.throwVelocity(0f, 0f, new SequenceRandom(0.25, 1.0, 0.0, 0.0));

        assertEquals(0.0, velocity.x(), 1.0e-7);
        assertEquals((ItemDropPhysics.THROW_SPEED + ItemDropPhysics.THROW_SPREAD) * 20.0, velocity.z(), 1.0e-7);
    }

    @Test
    void throwSpreadStaysWithinTheVanillaBounds() {
        RandomGenerator random = new Random(1234);

        for (int i = 0; i < 20000; i++) {
            Vec perTick = ItemDropPhysics.perTick(ItemDropPhysics.throwVelocity(
                    random.nextFloat() * 360f - 180f, random.nextFloat() * 180f - 90f, random));

            double maxHorizontal = ItemDropPhysics.THROW_SPEED + ItemDropPhysics.THROW_SPREAD;
            double maxVertical = ItemDropPhysics.THROW_SPEED + ItemDropPhysics.THROW_UPWARDS
                    + ItemDropPhysics.THROW_VERTICAL_SPREAD;

            assertTrue(Math.abs(perTick.x()) <= maxHorizontal + EPSILON);
            assertTrue(Math.abs(perTick.z()) <= maxHorizontal + EPSILON);
            assertTrue(Math.abs(perTick.y()) <= maxVertical + EPSILON);
        }
    }

    @Test
    void throwPositionSitsSlightlyBelowTheEyes() {
        Pos position = ItemDropPhysics.throwPosition(new Pos(3.5, 64.0, -8.5, 45f, 10f), 1.62);

        assertEquals(3.5, position.x(), EPSILON);
        assertEquals(64.0 + 1.62 - ItemDropPhysics.THROW_EYE_OFFSET, position.y(), EPSILON);
        assertEquals(-8.5, position.z(), EPSILON);
        assertEquals(45f, position.yaw());
        assertEquals(10f, position.pitch());
    }

    @Test
    void blockDropVelocityMatchesVanillaAtTheExtremes() {
        Vec low = ItemDropPhysics.blockDropVelocity(new SequenceRandom(0.0));
        Vec high = ItemDropPhysics.blockDropVelocity(new SequenceRandom(1.0));
        Vec middle = ItemDropPhysics.blockDropVelocity(new SequenceRandom(0.5));

        assertEquals(-0.1 * 20.0, low.x(), EPSILON);
        assertEquals(-0.1 * 20.0, low.z(), EPSILON);
        assertEquals(0.1 * 20.0, high.x(), EPSILON);
        assertEquals(0.1 * 20.0, high.z(), EPSILON);
        assertEquals(0.0, middle.x(), EPSILON);
        assertEquals(0.0, middle.z(), EPSILON);
        assertEquals(ItemDropPhysics.BLOCK_DROP_UPWARDS * 20.0, low.y(), EPSILON);
        assertEquals(ItemDropPhysics.BLOCK_DROP_UPWARDS * 20.0, high.y(), EPSILON);
    }

    @Test
    void blockDropPositionIsInsetByHalfTheItemWidth() {
        Vec block = new Vec(10, 70, -4);
        double inset = ItemDropPhysics.ITEM_WIDTH / 2.0;

        Pos lowest = ItemDropPhysics.blockDropPosition(block, new SequenceRandom(0.0));
        Pos highest = ItemDropPhysics.blockDropPosition(block, new SequenceRandom(1.0));

        assertEquals(10 + inset, lowest.x(), EPSILON);
        assertEquals(70 + inset, lowest.y(), EPSILON);
        assertEquals(-4 + inset, lowest.z(), EPSILON);
        assertEquals(10 + 1 - inset, highest.x(), EPSILON);
        assertEquals(70 + 1 - inset, highest.y(), EPSILON);
        assertEquals(-4 + 1 - inset, highest.z(), EPSILON);
    }

    @Test
    void blockDropPositionAlwaysKeepsTheItemInsideTheBlock() {
        RandomGenerator random = new Random(99);
        double inset = ItemDropPhysics.ITEM_WIDTH / 2.0;

        for (int i = 0; i < 20000; i++) {
            int x = random.nextInt(-512, 512);
            int y = random.nextInt(-64, 320);
            int z = random.nextInt(-512, 512);

            Pos dropped = ItemDropPhysics.blockDropPosition(new Vec(x, y, z), random);

            assertTrue(dropped.x() - inset >= x - EPSILON && dropped.x() + inset <= x + 1 + EPSILON);
            assertTrue(dropped.y() - inset >= y - EPSILON && dropped.y() + inset <= y + 1 + EPSILON);
            assertTrue(dropped.z() - inset >= z - EPSILON && dropped.z() + inset <= z + 1 + EPSILON);
        }
    }

    @Test
    void pickupSoundPitchStaysAroundTwo() {
        RandomGenerator random = new Random(7);

        assertEquals(ItemDropPhysics.PICKUP_SOUND_PITCH_BASE, ItemDropPhysics.pickupSoundPitch(
                new SequenceRandom(0.4, 0.4)), 1.0e-6);
        assertEquals(ItemDropPhysics.PICKUP_SOUND_PITCH_BASE + ItemDropPhysics.PICKUP_SOUND_PITCH_SPREAD,
                ItemDropPhysics.pickupSoundPitch(new SequenceRandom(1.0, 0.0)), 1.0e-6);

        for (int i = 0; i < 10000; i++) {
            float pitch = ItemDropPhysics.pickupSoundPitch(random);
            assertTrue(pitch >= ItemDropPhysics.PICKUP_SOUND_PITCH_BASE - ItemDropPhysics.PICKUP_SOUND_PITCH_SPREAD);
            assertTrue(pitch <= ItemDropPhysics.PICKUP_SOUND_PITCH_BASE + ItemDropPhysics.PICKUP_SOUND_PITCH_SPREAD);
        }
    }

    @Test
    void pickupReachExtendsOneBlockHorizontallyAndHalfABlockVertically() {
        Vec player = new Vec(0, 64, 0);
        double playerWidth = 0.6;
        double playerHeight = 1.8;

        assertTrue(reaches(player, playerWidth, playerHeight, new Vec(1.4, 64, 0)));
        assertFalse(reaches(player, playerWidth, playerHeight, new Vec(1.5, 64, 0)));
        assertTrue(reaches(player, playerWidth, playerHeight, new Vec(0, 64, -1.4)));
        assertFalse(reaches(player, playerWidth, playerHeight, new Vec(0, 64, -1.5)));
        assertTrue(reaches(player, playerWidth, playerHeight, new Vec(0, 63.6, 0)));
        assertFalse(reaches(player, playerWidth, playerHeight, new Vec(0, 63.2, 0)));
        assertTrue(reaches(player, playerWidth, playerHeight, new Vec(0, 66.0, 0)));
        assertFalse(reaches(player, playerWidth, playerHeight, new Vec(0, 66.6, 0)));
    }

    private static boolean reaches(Vec player, double width, double height, Vec item) {
        return ItemDropPhysics.withinPickupReach(player, width, height, item,
                ItemDropPhysics.ITEM_WIDTH, ItemDropPhysics.ITEM_HEIGHT);
    }

    private static RandomGenerator noSpread() {
        return new SequenceRandom(0.0);
    }

    private static final class SequenceRandom implements RandomGenerator {
        private final double[] values;
        private int index;

        private SequenceRandom(double... values) {
            this.values = values;
        }

        @Override
        public long nextLong() {
            throw new UnsupportedOperationException();
        }

        @Override
        public double nextDouble() {
            return values[index++ % values.length];
        }
    }
}
