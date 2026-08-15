package net.swofty.type.skyblockgeneric.item.crafting.grid;

import net.swofty.type.generic.gui.v2.click.SlotClicks;
import net.swofty.type.generic.gui.v2.click.SlotStack;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CraftingGridModelTest {

    private static final String PLANK = "PLANK";
    private static final String STICK = "STICK";
    private static final String ROCK = "ROCK";

    private static SlotStack stack(String id, int amount) {
        return SlotStack.of(id, amount, 64);
    }

    private static SlotStack[] emptyGrid() {
        SlotStack[] grid = new SlotStack[CraftingGridModel.GRID_SIZE];
        Arrays.fill(grid, SlotStack.EMPTY);
        return grid;
    }

    private static SlotStack[] emptyInventory() {
        SlotStack[] inventory = new SlotStack[CraftingGridModel.PLAYER_SIZE];
        Arrays.fill(inventory, SlotStack.EMPTY);
        return inventory;
    }

    private static SlotStack[] gridWithPlanks(int amount) {
        SlotStack[] grid = emptyGrid();
        for (int i = 0; i < 3; i++) {
            grid[i] = stack(PLANK, amount);
        }
        return grid;
    }

    private static CraftingGridModel model(SlotStack[] grid, SlotStack[] inventory, SlotStack cursor) {
        return new CraftingGridModel(new PlankResolver(), grid, inventory, cursor);
    }

    private static final class PlankResolver implements CraftingResolver {
        @Override
        public SlotStack result(SlotStack[] grid) {
            return matches(grid) ? stack(STICK, 4) : SlotStack.EMPTY;
        }

        @Override
        public SlotStack[] consume(SlotStack[] grid) {
            if (!matches(grid)) return grid;

            SlotStack[] out = grid.clone();
            for (int i = 0; i < 3; i++) {
                out[i] = out[i].shrink(1);
            }
            return out;
        }

        private static boolean matches(SlotStack[] grid) {
            for (int i = 0; i < 3; i++) {
                if (grid[i].isEmpty() || !PLANK.equals(grid[i].item())) return false;
            }
            for (int i = 3; i < grid.length; i++) {
                if (!grid[i].isEmpty()) return false;
            }
            return true;
        }
    }

    @Test
    void resultIsRecomputedAfterEveryGridMutation() {
        CraftingGridModel model = model(emptyGrid(), emptyInventory(), stack(PLANK, 3));
        assertTrue(model.result().isEmpty());

        model.drag(List.of(CraftingSlot.grid(0), CraftingSlot.grid(1), CraftingSlot.grid(2)),
                SlotClicks.DragMode.SINGLE);

        assertEquals(STICK, model.result().item());
        assertEquals(4, model.result().amount());
    }

    @Test
    void takingTheResultConsumesExactlyOneCraft() {
        CraftingGridModel model = model(gridWithPlanks(3), emptyInventory(), SlotStack.EMPTY);

        assertTrue(model.pickUp(CraftingSlot.RESULT, false));

        assertEquals(4, model.cursor().amount());
        assertEquals(1, model.craftCount());
        for (int i = 0; i < 3; i++) {
            assertEquals(2, model.grid()[i].amount());
        }
    }

    @Test
    void takingTheResultMergesIntoAMatchingCursor() {
        CraftingGridModel model = model(gridWithPlanks(1), emptyInventory(), stack(STICK, 4));

        assertTrue(model.pickUp(CraftingSlot.RESULT, false));

        assertEquals(8, model.cursor().amount());
        assertEquals(1, model.craftCount());
    }

    @Test
    void takingTheResultIsRefusedWhenTheCursorWouldOverflow() {
        CraftingGridModel model = model(gridWithPlanks(1), emptyInventory(), stack(STICK, 62));

        assertFalse(model.pickUp(CraftingSlot.RESULT, false));

        assertEquals(62, model.cursor().amount());
        assertEquals(0, model.craftCount());
    }

    @Test
    void theResultSlotCannotBePlacedInto() {
        CraftingGridModel model = model(gridWithPlanks(1), emptyInventory(), stack(ROCK, 5));

        assertFalse(model.pickUp(CraftingSlot.RESULT, false));
        assertFalse(model.pickUp(CraftingSlot.RESULT, true));

        assertEquals(ROCK, model.cursor().item());
        assertEquals(5, model.cursor().amount());
        assertEquals(0, model.craftCount());
    }

    @Test
    void shiftClickingTheResultCraftsUntilIngredientsRunOut() {
        CraftingGridModel model = model(gridWithPlanks(3), emptyInventory(), SlotStack.EMPTY);

        assertTrue(model.quickMove(CraftingSlot.RESULT));

        assertEquals(3, model.craftCount());
        assertEquals(12, model.craftedTotal());
        assertEquals(12, model.playerItems()[8].amount());
        for (SlotStack slot : model.grid()) {
            assertTrue(slot.isEmpty());
        }
    }

    @Test
    void shiftClickingTheResultStopsWhenTheInventoryIsFull() {
        SlotStack[] inventory = emptyInventory();
        Arrays.fill(inventory, stack(ROCK, 64));

        CraftingGridModel model = model(gridWithPlanks(2), inventory, SlotStack.EMPTY);

        assertFalse(model.quickMove(CraftingSlot.RESULT));

        assertEquals(0, model.craftCount());
        for (int i = 0; i < 3; i++) {
            assertEquals(2, model.grid()[i].amount());
        }
    }

    @Test
    void shiftClickingTheResultNeverConsumesACraftItCannotDeliver() {
        SlotStack[] inventory = emptyInventory();
        Arrays.fill(inventory, stack(ROCK, 64));
        inventory[8] = stack(STICK, 62);

        CraftingGridModel model = model(gridWithPlanks(2), inventory, SlotStack.EMPTY);

        assertFalse(model.quickMove(CraftingSlot.RESULT));

        assertEquals(0, model.craftCount());
        assertEquals(62, model.playerItems()[8].amount());
        for (int i = 0; i < 3; i++) {
            assertEquals(2, model.grid()[i].amount());
        }
    }

    @Test
    void shiftClickingTheResultFillsTheHotbarFromTheRight() {
        CraftingGridModel model = model(gridWithPlanks(1), emptyInventory(), SlotStack.EMPTY);

        assertTrue(model.quickMove(CraftingSlot.RESULT));

        assertEquals(4, model.playerItems()[8].amount());
    }

    @Test
    void quickMovingOutOfTheGridPrefersTheMainInventory() {
        SlotStack[] grid = emptyGrid();
        grid[0] = stack(PLANK, 5);

        CraftingGridModel model = model(grid, emptyInventory(), SlotStack.EMPTY);

        assertTrue(model.quickMove(CraftingSlot.grid(0)));

        assertEquals(5, model.playerItems()[9].amount());
        assertTrue(model.grid()[0].isEmpty());
    }

    @Test
    void quickMovingOutOfTheGridMergesBeforeUsingAnEmptySlot() {
        SlotStack[] grid = emptyGrid();
        grid[0] = stack(PLANK, 5);

        SlotStack[] inventory = emptyInventory();
        inventory[0] = stack(PLANK, 60);
        inventory[20] = stack(PLANK, 62);

        CraftingGridModel model = model(grid, inventory, SlotStack.EMPTY);

        assertTrue(model.quickMove(CraftingSlot.grid(0)));

        assertEquals(64, model.playerItems()[20].amount());
        assertEquals(63, model.playerItems()[0].amount());
        assertTrue(model.grid()[0].isEmpty());
    }

    @Test
    void quickMovingFromTheInventoryFillsTheGridFirst() {
        SlotStack[] inventory = emptyInventory();
        inventory[9] = stack(PLANK, 3);

        CraftingGridModel model = model(emptyGrid(), inventory, SlotStack.EMPTY);

        assertTrue(model.quickMove(CraftingSlot.player(9)));

        assertEquals(3, model.grid()[0].amount());
        assertTrue(model.playerItems()[9].isEmpty());
    }

    @Test
    void quickMovingFromTheInventoryFallsBackToTheHotbarWhenTheGridIsFull() {
        SlotStack[] grid = emptyGrid();
        Arrays.fill(grid, stack(ROCK, 64));

        SlotStack[] inventory = emptyInventory();
        inventory[9] = stack(PLANK, 3);

        CraftingGridModel model = model(grid, inventory, SlotStack.EMPTY);

        assertTrue(model.quickMove(CraftingSlot.player(9)));

        assertEquals(3, model.playerItems()[0].amount());
        assertTrue(model.playerItems()[9].isEmpty());
    }

    @Test
    void quickMovingFromTheHotbarFallsBackToTheMainInventory() {
        SlotStack[] grid = emptyGrid();
        Arrays.fill(grid, stack(ROCK, 64));

        SlotStack[] inventory = emptyInventory();
        inventory[3] = stack(PLANK, 3);

        CraftingGridModel model = model(grid, inventory, SlotStack.EMPTY);

        assertTrue(model.quickMove(CraftingSlot.player(3)));

        assertEquals(3, model.playerItems()[9].amount());
        assertTrue(model.playerItems()[3].isEmpty());
    }

    @Test
    void draggingSkipsTheResultSlot() {
        CraftingGridModel model = model(emptyGrid(), emptyInventory(), stack(PLANK, 4));

        assertTrue(model.drag(List.of(CraftingSlot.grid(0), CraftingSlot.RESULT, CraftingSlot.grid(1)),
                SlotClicks.DragMode.EVEN));

        assertEquals(2, model.grid()[0].amount());
        assertEquals(2, model.grid()[1].amount());
        assertTrue(model.cursor().isEmpty());
    }

    @Test
    void draggingSpansTheGridAndThePlayerInventory() {
        CraftingGridModel model = model(emptyGrid(), emptyInventory(), stack(PLANK, 6));

        assertTrue(model.drag(List.of(CraftingSlot.grid(0), CraftingSlot.player(4), CraftingSlot.grid(1)),
                SlotClicks.DragMode.EVEN));

        assertEquals(2, model.grid()[0].amount());
        assertEquals(2, model.grid()[1].amount());
        assertEquals(2, model.playerItems()[4].amount());
        assertTrue(model.cursor().isEmpty());
    }

    @Test
    void hotbarSwapExchangesTheGridSlotWithTheHotbar() {
        SlotStack[] grid = emptyGrid();
        grid[4] = stack(PLANK, 2);

        SlotStack[] inventory = emptyInventory();
        inventory[7] = stack(ROCK, 9);

        CraftingGridModel model = model(grid, inventory, SlotStack.EMPTY);

        assertTrue(model.swap(CraftingSlot.grid(4), 7));

        assertEquals(ROCK, model.grid()[4].item());
        assertEquals(9, model.grid()[4].amount());
        assertEquals(PLANK, model.playerItems()[7].item());
        assertEquals(2, model.playerItems()[7].amount());
    }

    @Test
    void hotbarSwapOnTheResultCraftsIntoAnEmptySlot() {
        CraftingGridModel model = model(gridWithPlanks(1), emptyInventory(), SlotStack.EMPTY);

        assertTrue(model.swap(CraftingSlot.RESULT, 2));

        assertEquals(STICK, model.playerItems()[2].item());
        assertEquals(4, model.playerItems()[2].amount());
        assertEquals(1, model.craftCount());
    }

    @Test
    void collectGathersMatchingItemsIntoTheCursor() {
        SlotStack[] grid = emptyGrid();
        grid[5] = stack(PLANK, 7);

        SlotStack[] inventory = emptyInventory();
        inventory[11] = stack(PLANK, 20);

        CraftingGridModel model = model(grid, inventory, stack(PLANK, 30));

        assertTrue(model.collect(model.cursor()));

        assertEquals(57, model.cursor().amount());
        assertTrue(model.grid()[5].isEmpty());
        assertTrue(model.playerItems()[11].isEmpty());
    }
}
