package net.swofty.type.generic.entity.drop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemStackMergingTest {

    @Test
    void mergesTwoStacksThatFitTogether() {
        assertTrue(ItemStackMerging.canMerge(30, 34, 64));
        assertEquals(64, ItemStackMerging.mergedAmount(30, 34));
    }

    @Test
    void refusesToMergeWhenTheResultWouldOverflowTheMaxStackSize() {
        assertFalse(ItemStackMerging.canMerge(40, 30, 64));
        assertFalse(ItemStackMerging.canMerge(1, 1, 1));
        assertFalse(ItemStackMerging.canMerge(16, 1, 16));
    }

    @Test
    void refusesToMergeEmptyStacks() {
        assertFalse(ItemStackMerging.canMerge(0, 5, 64));
        assertFalse(ItemStackMerging.canMerge(5, 0, 64));
        assertFalse(ItemStackMerging.canMerge(-1, 5, 64));
    }

    @Test
    void respectsSmallMaxStackSizes() {
        assertTrue(ItemStackMerging.canMerge(8, 8, 16));
        assertEquals(16, ItemStackMerging.mergedAmount(8, 8));
        assertFalse(ItemStackMerging.canMerge(9, 8, 16));
    }

    @Test
    void mergingIsSymmetric() {
        for (int first = 1; first <= 64; first++) {
            for (int second = 1; second <= 64; second++) {
                assertEquals(ItemStackMerging.canMerge(first, second, 64),
                        ItemStackMerging.canMerge(second, first, 64));
                assertEquals(ItemStackMerging.mergedAmount(first, second),
                        ItemStackMerging.mergedAmount(second, first));
            }
        }
    }
}
