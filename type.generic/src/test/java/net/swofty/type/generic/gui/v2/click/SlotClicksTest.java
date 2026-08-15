package net.swofty.type.generic.gui.v2.click;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlotClicksTest {

    private static SlotStack stack(String id, int amount) {
        return SlotStack.of(id, amount, 64);
    }

    private static SlotStack stack(String id, int amount, int maxStackSize) {
        return SlotStack.of(id, amount, maxStackSize);
    }

    @Test
    void leftClickPicksUpWholeStack() {
        SlotClicks.Transfer transfer = SlotClicks.left(stack("STONE", 17), SlotStack.EMPTY);

        assertTrue(transfer.changed());
        assertTrue(transfer.slot().isEmpty());
        assertEquals(17, transfer.cursor().amount());
    }

    @Test
    void leftClickMergesUpToTheStackLimit() {
        SlotClicks.Transfer transfer = SlotClicks.left(stack("STONE", 60), stack("STONE", 10));

        assertEquals(64, transfer.slot().amount());
        assertEquals(6, transfer.cursor().amount());
    }

    @Test
    void leftClickSwapsDifferentItems() {
        SlotClicks.Transfer transfer = SlotClicks.left(stack("STONE", 3), stack("DIRT", 5));

        assertEquals("DIRT", transfer.slot().item());
        assertEquals(5, transfer.slot().amount());
        assertEquals("STONE", transfer.cursor().item());
        assertEquals(3, transfer.cursor().amount());
    }

    @Test
    void rightClickTakesHalfRoundingUp() {
        assertEquals(3, SlotClicks.right(stack("STONE", 5), SlotStack.EMPTY).cursor().amount());
        assertEquals(2, SlotClicks.right(stack("STONE", 5), SlotStack.EMPTY).slot().amount());
        assertEquals(1, SlotClicks.right(stack("STONE", 1), SlotStack.EMPTY).cursor().amount());
        assertTrue(SlotClicks.right(stack("STONE", 1), SlotStack.EMPTY).slot().isEmpty());
        assertEquals(32, SlotClicks.right(stack("STONE", 64), SlotStack.EMPTY).cursor().amount());
    }

    @Test
    void rightClickPlacesExactlyOne() {
        SlotClicks.Transfer intoEmpty = SlotClicks.right(SlotStack.EMPTY, stack("STONE", 9));
        assertEquals(1, intoEmpty.slot().amount());
        assertEquals(8, intoEmpty.cursor().amount());

        SlotClicks.Transfer intoStack = SlotClicks.right(stack("STONE", 4), stack("STONE", 9));
        assertEquals(5, intoStack.slot().amount());
        assertEquals(8, intoStack.cursor().amount());
    }

    @Test
    void rightClickOnFullSlotDoesNothing() {
        SlotClicks.Transfer transfer = SlotClicks.right(stack("STONE", 64), stack("STONE", 9));

        assertFalse(transfer.changed());
        assertEquals(64, transfer.slot().amount());
        assertEquals(9, transfer.cursor().amount());
    }

    @Test
    void evenDragSplitsAcrossSlotsAndReturnsTheRemainder() {
        SlotStack[] targets = {SlotStack.EMPTY, SlotStack.EMPTY, SlotStack.EMPTY};

        SlotClicks.DragResult result = SlotClicks.drag(stack("STONE", 11), targets, SlotClicks.DragMode.EVEN);

        assertTrue(result.changed());
        assertEquals(3, result.slots()[0].amount());
        assertEquals(3, result.slots()[1].amount());
        assertEquals(3, result.slots()[2].amount());
        assertEquals(2, result.cursor().amount());
    }

    @Test
    void evenDragTopsUpExistingStacks() {
        SlotStack[] targets = {stack("STONE", 62), SlotStack.EMPTY};

        SlotClicks.DragResult result = SlotClicks.drag(stack("STONE", 10), targets, SlotClicks.DragMode.EVEN);

        assertEquals(64, result.slots()[0].amount());
        assertEquals(5, result.slots()[1].amount());
        assertEquals(3, result.cursor().amount());
    }

    @Test
    void evenDragIsANoOpWhenTheCursorHasFewerItemsThanSlots() {
        SlotStack[] targets = {SlotStack.EMPTY, SlotStack.EMPTY, SlotStack.EMPTY};

        SlotClicks.DragResult result = SlotClicks.drag(stack("STONE", 2), targets, SlotClicks.DragMode.EVEN);

        assertFalse(result.changed());
        assertSame(targets, result.slots());
        assertEquals(2, result.cursor().amount());
    }

    @Test
    void singleDragPlacesOneItemPerSlot() {
        SlotStack[] targets = {SlotStack.EMPTY, stack("STONE", 5), SlotStack.EMPTY};

        SlotClicks.DragResult result = SlotClicks.drag(stack("STONE", 7), targets, SlotClicks.DragMode.SINGLE);

        assertEquals(1, result.slots()[0].amount());
        assertEquals(6, result.slots()[1].amount());
        assertEquals(1, result.slots()[2].amount());
        assertEquals(4, result.cursor().amount());
    }

    @Test
    void fullDragFillsEverySlotAndKeepsTheCursor() {
        SlotStack[] targets = {SlotStack.EMPTY, stack("STONE", 10)};

        SlotClicks.DragResult result = SlotClicks.drag(stack("STONE", 1), targets, SlotClicks.DragMode.FULL);

        assertEquals(64, result.slots()[0].amount());
        assertEquals(64, result.slots()[1].amount());
        assertEquals(1, result.cursor().amount());
    }

    @Test
    void dragRespectsSmallerStackLimits() {
        SlotStack[] targets = {SlotStack.EMPTY, SlotStack.EMPTY};

        SlotClicks.DragResult result = SlotClicks.drag(stack("EGG", 16, 16), targets, SlotClicks.DragMode.EVEN);

        assertEquals(8, result.slots()[0].amount());
        assertEquals(8, result.slots()[1].amount());
        assertTrue(result.cursor().isEmpty());
    }

    @Test
    void quickMoveMergesIntoPartialStacksBeforeUsingAnEmptySlot() {
        SlotStack[] slots = {
                SlotStack.EMPTY,
                stack("STONE", 60),
                stack("DIRT", 5),
                stack("STONE", 62)
        };
        int[] order = {0, 1, 2, 3};

        SlotClicks.QuickMove moved = SlotClicks.quickMove(stack("STONE", 10), slots, order);

        assertTrue(moved.changed());
        assertEquals(64, moved.slots()[1].amount());
        assertEquals(64, moved.slots()[3].amount());
        assertEquals(4, moved.slots()[0].amount());
        assertTrue(moved.leftover().isEmpty());
    }

    @Test
    void quickMoveFollowsTheGivenOrder() {
        SlotStack[] slots = {SlotStack.EMPTY, SlotStack.EMPTY, SlotStack.EMPTY};
        int[] order = {2, 1, 0};

        SlotClicks.QuickMove moved = SlotClicks.quickMove(stack("STONE", 5), slots, order);

        assertEquals(5, moved.slots()[2].amount());
        assertTrue(moved.slots()[0].isEmpty());
        assertTrue(moved.slots()[1].isEmpty());
    }

    @Test
    void quickMoveReportsTheLeftoverWhenNothingFits() {
        SlotStack[] slots = {stack("DIRT", 64), stack("STONE", 64)};
        int[] order = {0, 1};

        SlotClicks.QuickMove moved = SlotClicks.quickMove(stack("STONE", 10), slots, order);

        assertFalse(moved.changed());
        assertEquals(10, moved.leftover().amount());
    }

    @Test
    void quickMoveOnlyFillsASingleEmptySlot() {
        SlotStack[] slots = {SlotStack.EMPTY, SlotStack.EMPTY};
        int[] order = {0, 1};

        SlotClicks.QuickMove moved = SlotClicks.quickMove(stack("EGG", 30, 16), slots, order);

        assertEquals(16, moved.slots()[0].amount());
        assertTrue(moved.slots()[1].isEmpty());
        assertEquals(14, moved.leftover().amount());
    }

    @Test
    void collectTakesPartialStacksBeforeFullOnes() {
        SlotStack[] slots = {stack("STONE", 64), stack("STONE", 3), stack("STONE", 5)};
        int[] order = {0, 1, 2};

        SlotClicks.QuickMove collected = SlotClicks.collect(stack("STONE", 50), slots, order);

        assertEquals(64, collected.leftover().amount());
        assertTrue(collected.slots()[1].isEmpty());
        assertTrue(collected.slots()[2].isEmpty());
        assertEquals(58, collected.slots()[0].amount());
    }
}
