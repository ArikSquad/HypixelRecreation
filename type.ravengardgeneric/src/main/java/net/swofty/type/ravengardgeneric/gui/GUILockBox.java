package net.swofty.type.ravengardgeneric.gui;

import net.kyori.adventure.nbt.TagStringIO;
import net.minestom.server.component.DataComponents;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.TooltipDisplay;
import net.swofty.commons.text.Text;
import net.swofty.type.generic.gui.v2.DefaultState;
import net.swofty.type.generic.gui.v2.ViewLayout;
import net.swofty.type.generic.gui.v2.ViewNavigator;
import net.swofty.type.generic.gui.v2.context.ClickContext;
import net.swofty.type.generic.gui.v2.context.ViewContext;
import net.swofty.type.ravengardgeneric.data.RavengardProfileStorage;
import net.swofty.type.ravengardgeneric.profile.RavengardProfiles;
import net.swofty.type.ravengardgeneric.user.RavengardPlayer;
import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * The lock box, rebuilt from a capture: the top five rows are the storage grid, the bottom row is
 * navigation (back left, page arrows right), and every slot the current tier has not unlocked shows
 * the pack's locked pane so the whole box is visible across pages. The next tier to buy carries the
 * captured expand button embedded in its locked region -- tier one opens twenty-eight slots and each
 * expansion adds eight, matching the captured tier two offer of a hundred crowns; later tier prices
 * double, pending captures.
 */
public class GUILockBox extends RavengardView {
    private static final int PANEL_ICON = 0xF00A;
    private static final int CONTENT_SLOTS = 45;
    private static final int BASE_SLOTS = 28;
    private static final int SLOTS_PER_TIER = 8;
    private static final int MAX_TIER = 5;
    private static final int MAX_SLOTS = BASE_SLOTS + SLOTS_PER_TIER * (MAX_TIER - 1);
    private static final int PAGES = (MAX_SLOTS + CONTENT_SLOTS - 1) / CONTENT_SLOTS;
    private static final int[] EXPAND_COSTS = {100, 200, 400, 800};
    private static final int EXPAND_WIDTH = 4;
    private static final int SLOT_PREVIOUS = 51;
    private static final int SLOT_NEXT = 53;
    private static final String LOCKED_MODEL_ROOT = "hypixel_ravengard:ui/menu/generic/storage_locked_";

    private final int page;

    public GUILockBox() {
        this(0);
    }

    public GUILockBox(int page) {
        this.page = Math.floorMod(page, PAGES);
    }

    @Override
    protected String title() {
        return "Lock Box";
    }

    @Override
    protected int panelIcon() {
        return PANEL_ICON;
    }

    @Override
    protected void content(ViewLayout<DefaultState> layout, DefaultState state, ViewContext ctx) {
        if (!(ctx.player() instanceof RavengardPlayer player)) {
            return;
        }
        UUID profile = player.getSelectedProfile();
        int tier = RavengardProfileStorage.lockBoxTier(profile);
        int unlocked = unlockedSlots(tier);
        Map<Integer, String> stored = RavengardProfileStorage.readLockBox(profile);

        for (int gui = 0; gui < CONTENT_SLOTS; gui++) {
            int absolute = page * CONTENT_SLOTS + gui;
            if (absolute >= MAX_SLOTS) {
                continue;
            }
            if (absolute >= unlocked) {
                layout.slot(gui, lockedPane(absolute));
                continue;
            }
            String snbt = stored.get(absolute);
            if (snbt == null) {
                continue;
            }
            ItemStack stack = fromSnbt(snbt);
            if (!stack.isAir()) {
                layout.slot(gui, stack.builder(),
                        (click, viewContext) -> withdraw(viewContext, absolute));
            }
        }

        if (tier < MAX_TIER) {
            placeExpand(layout, unlocked, tier);
        }

        interactive(layout, SLOT_PREVIOUS, RavengardItems.button(RavengardButton.LEFT)
                        .label("Previous Page")
                        .lore("<7>Browse the previous page of items.")
                        .blankLine()
                        .lore("<e>Click to go back to the previous page!"),
                (click, viewContext) -> turnTo(viewContext, page - 1));

        interactive(layout, SLOT_NEXT, RavengardItems.button(RavengardButton.RIGHT)
                        .label("Next Page")
                        .lore("<7>Browse the next page of items.")
                        .blankLine()
                        .lore("<e>Click to go to the next page!"),
                (click, viewContext) -> turnTo(viewContext, page + 1));

        backButton(layout);
    }

    /**
     * The expand widget follows the last unlocked slot: two locked panes then the four wide button.
     * If it would run off the end of its row it flows to the start of the next page, so the button
     * is never split across a page edge.
     */
    private void placeExpand(ViewLayout<DefaultState> layout, int unlocked, int tier) {
        int widgetWidth = 2 + EXPAND_WIDTH;
        int widgetPage = unlocked / CONTENT_SLOTS;
        int widgetCol = unlocked % CONTENT_SLOTS;
        if (widgetCol + widgetWidth > CONTENT_SLOTS) {
            widgetPage += 1;
            widgetCol = 0;
        }
        if (widgetPage != page) {
            return;
        }
        int expandSlot = widgetCol + 2;
        int cost = EXPAND_COSTS[tier - 1];
        int nextTier = tier + 1;
        interactive(layout, expandSlot, RavengardItems.button(RavengardButton.TEXT_EXPAND)
                        .label("Expand Lock Box!")
                        .lore(Text.of("<7>Upgrade your lock box to Tier {}!", nextTier),
                                Text.of("<7>This will unlock an additional {} slots", SLOTS_PER_TIER),
                                Text.of("<7>in your lock box."))
                        .blankLine()
                        .lore(Text.of("<7>Cost: <f>👑<#FFCE47>{} Crowns", cost)),
                (click, viewContext) -> expand(viewContext, tier, cost));
    }

    @Override
    public boolean onBottomClick(ClickContext<DefaultState> click, ViewContext ctx) {
        if (!(ctx.player() instanceof RavengardPlayer player)) {
            return false;
        }
        ItemStack stack = player.getInventory().getItemStack(click.slot());
        if (stack.isAir()) {
            return false;
        }
        UUID profile = player.getSelectedProfile();
        int unlocked = unlockedSlots(RavengardProfileStorage.lockBoxTier(profile));
        Map<Integer, String> stored = new HashMap<>(RavengardProfileStorage.readLockBox(profile));
        int free = -1;
        for (int slot = 0; slot < unlocked; slot++) {
            if (!stored.containsKey(slot)) {
                free = slot;
                break;
            }
        }
        if (free < 0) {
            player.sendMessage("<c>Your lock box is full!");
            return false;
        }
        String snbt = toSnbt(stack);
        if (snbt == null) {
            return false;
        }
        player.getInventory().setItemStack(click.slot(), ItemStack.AIR);
        stored.put(free, snbt);
        RavengardProfileStorage.writeLockBox(profile, stored);
        ViewNavigator.get(player).push(new GUILockBox(free / CONTENT_SLOTS));
        return false;
    }

    private void withdraw(ViewContext ctx, int absolute) {
        if (!(ctx.player() instanceof RavengardPlayer player)) {
            return;
        }
        UUID profile = player.getSelectedProfile();
        Map<Integer, String> stored = new HashMap<>(RavengardProfileStorage.readLockBox(profile));
        String snbt = stored.remove(absolute);
        if (snbt == null) {
            return;
        }
        ItemStack stack = fromSnbt(snbt);
        RavengardProfileStorage.writeLockBox(profile, stored);
        if (!stack.isAir()) {
            player.getInventory().addItemStack(stack);
        }
        ViewNavigator.get(player).push(new GUILockBox(page));
    }

    private void expand(ViewContext ctx, int tier, int cost) {
        if (!(ctx.player() instanceof RavengardPlayer player)) {
            return;
        }
        if (!RavengardProfiles.tryPurchase(player, cost)) {
            player.sendMessage("<c>You don't have enough Crowns!");
            return;
        }
        RavengardProfileStorage.writeLockBoxTier(player.getSelectedProfile(), tier + 1);
        player.sendMessage("<a>Your lock box is now Tier {}!", tier + 1);
        ViewNavigator.get(player).push(new GUILockBox(page));
    }

    private void turnTo(ViewContext ctx, int target) {
        if (!(ctx.player() instanceof RavengardPlayer player) || PAGES <= 1) {
            return;
        }
        int clamped = Math.floorMod(target, PAGES);
        if (clamped == page) {
            return;
        }
        ViewNavigator.get(player).push(new GUILockBox(clamped));
    }

    private static int unlockedSlots(int tier) {
        return BASE_SLOTS + SLOTS_PER_TIER * (Math.max(1, tier) - 1);
    }

    private static ItemStack.Builder lockedPane(int absolute) {
        int region = Math.min(MAX_TIER, 2 + (absolute - BASE_SLOTS) / SLOTS_PER_TIER);
        String side = (absolute - BASE_SLOTS) % 2 == 0 ? "left" : "right";
        return ItemStack.builder(Material.LEATHER_CHESTPLATE)
                .set(DataComponents.ITEM_MODEL, LOCKED_MODEL_ROOT + region + "_" + side)
                .set(DataComponents.TOOLTIP_DISPLAY, new TooltipDisplay(true, Set.of()));
    }

    private static String toSnbt(ItemStack stack) {
        try {
            return TagStringIO.tagStringIO().asString(stack.toItemNBT());
        } catch (Exception exception) {
            Logger.error(exception, "Could not serialise a lock box item");
            return null;
        }
    }

    private static ItemStack fromSnbt(String snbt) {
        try {
            return ItemStack.fromItemNBT(TagStringIO.tagStringIO().asCompound(snbt));
        } catch (Exception exception) {
            Logger.error(exception, "Could not restore a lock box item");
            return ItemStack.AIR;
        }
    }
}
