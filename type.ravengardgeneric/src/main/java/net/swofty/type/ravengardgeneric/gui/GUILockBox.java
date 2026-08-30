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
 * The lock box, rebuilt from a live capture: four storage rows a page, the next tier's first two
 * slots rendered as the pack's locked pair, the four slot wide expand button beside them, and
 * page arrows flanking the back button. Tier one opens twenty-eight slots and every expansion
 * adds eight, which is what the captured tier two offer sells for a hundred crowns; later tier
 * prices are uncaptured and double each time.
 */
public class GUILockBox extends RavengardView {
    private static final int PANEL_ICON = 0xF00A;
    private static final int PAGE_CAPACITY = 36;
    private static final int BASE_SLOTS = 28;
    private static final int SLOTS_PER_TIER = 8;
    private static final int MAX_TIER = 5;
    private static final int[] EXPAND_COSTS = {100, 200, 400, 800};
    private static final int SLOT_PREVIOUS = 51;
    private static final int SLOT_NEXT = 53;
    private static final String LOCKED_MODEL_ROOT = "hypixel_ravengard:ui/menu/generic/storage_locked_";

    private final int page;

    public GUILockBox() {
        this(0);
    }

    public GUILockBox(int page) {
        this.page = page;
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

        for (Map.Entry<Integer, String> entry : stored.entrySet()) {
            int absolute = entry.getKey();
            if (absolute / PAGE_CAPACITY != page || absolute >= unlocked) {
                continue;
            }
            ItemStack stack = fromSnbt(entry.getValue());
            if (stack.isAir()) {
                continue;
            }
            layout.slot(absolute % PAGE_CAPACITY, stack.builder(),
                    (click, viewContext) -> withdraw(viewContext, absolute));
        }

        if (tier < MAX_TIER && unlocked / PAGE_CAPACITY == page) {
            int local = unlocked % PAGE_CAPACITY;
            int nextTier = tier + 1;
            layout.slot(local, lockedPane(nextTier, "left"));
            if (local + 1 < PAGE_CAPACITY) {
                layout.slot(local + 1, lockedPane(nextTier, "right"));
            }
            int cost = EXPAND_COSTS[tier - 1];
            interactive(layout, Math.min(local + 2, PAGE_CAPACITY - 4),
                    RavengardItems.button(RavengardButton.TEXT_EXPAND)
                            .label("Expand Lock Box!")
                            .lore(Text.of("<7>Upgrade your lock box to Tier {}!", nextTier),
                                    Text.of("<7>This will unlock an additional {} slots", SLOTS_PER_TIER),
                                    Text.of("<7>in your lock box."))
                            .blankLine()
                            .lore(Text.of("<7>Cost: <f>👑<#FFCE47>{} Crowns", cost)),
                    (click, viewContext) -> expand(viewContext, tier, cost));
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
        ViewNavigator.get(player).push(new GUILockBox(free / PAGE_CAPACITY));
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
        ViewNavigator.get(player).push(new GUILockBox(page));
    }

    private void turnTo(ViewContext ctx, int target) {
        if (!(ctx.player() instanceof RavengardPlayer player)) {
            return;
        }
        int tier = RavengardProfileStorage.lockBoxTier(player.getSelectedProfile());
        int pages = Math.max(1, (unlockedSlots(tier) + PAGE_CAPACITY - 1) / PAGE_CAPACITY);
        int clamped = Math.floorMod(target, pages);
        if (clamped == page) {
            return;
        }
        ViewNavigator.get(player).push(new GUILockBox(clamped));
    }

    private static int unlockedSlots(int tier) {
        return BASE_SLOTS + SLOTS_PER_TIER * (tier - 1);
    }

    private static ItemStack.Builder lockedPane(int tier, String side) {
        return ItemStack.builder(Material.LEATHER_CHESTPLATE)
                .set(DataComponents.ITEM_MODEL, LOCKED_MODEL_ROOT + tier + "_" + side)
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
