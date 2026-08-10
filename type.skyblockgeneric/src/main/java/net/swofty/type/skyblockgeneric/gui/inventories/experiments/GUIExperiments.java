package net.swofty.type.skyblockgeneric.gui.inventories.experiments;

import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.swofty.type.generic.gui.inventory.ItemStacks;
import net.swofty.type.generic.gui.v2.*;
import net.swofty.type.generic.gui.v2.context.ViewContext;
import net.swofty.type.skyblockgeneric.experimentation.ExperimentType;
import net.swofty.type.skyblockgeneric.experimentation.ExperimentReward;
import net.swofty.type.skyblockgeneric.experimentation.ExperimentationRNGMeter;
import net.swofty.type.skyblockgeneric.rngmeter.RNGMeterService;
import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;

import java.util.Arrays;
import java.util.List;

public final class GUIExperiments extends StatelessView {
    private static final int[] BORDER_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 51, 52, 53};

    @Override
    public ViewConfiguration<DefaultState> configuration() {
        return new ViewConfiguration<>("Experimentation Table", InventoryType.CHEST_6_ROW);
    }

    @Override
    public void layout(ViewLayout<DefaultState> layout, DefaultState state, ViewContext ctx) {
        Components.fill(layout);
        layout.filler(Arrays.stream(BORDER_SLOTS).boxed().toList(),
                ExperimentationGuiSupport.item(" ", Material.PURPLE_STAINED_GLASS_PANE, 1));
        Components.close(layout, 49);

        layout.slot(22, ExperimentationGuiSupport.experimentIcon(ExperimentType.SUPERPAIRS),
                (_, viewCtx) -> viewCtx.push(new GUISuperPairs()));
        layout.slot(29, ExperimentationGuiSupport.experimentIcon(ExperimentType.CHRONOMATRON),
                (_, viewCtx) -> viewCtx.push(new GUIChronomatron()));
        layout.slot(33, ExperimentationGuiSupport.experimentIcon(ExperimentType.ULTRASEQUENCER),
                (_, viewCtx) -> viewCtx.push(new GUIUltrasequencer()));

        layout.filler(List.of(20, 21, 23, 24),
                ExperimentationGuiSupport.item("<7>Pending Experiment...", Material.PINK_STAINED_GLASS_PANE, 1));
        layout.slot(50, ExperimentationGuiSupport.item(
                "<b>Experience Bottles",
                Material.EXPERIENCE_BOTTLE,
                1,
                "<7>Use experience bottles to restore missing experience.",
                "",
                "<7>Experiment rewards grant <b>Enchanting XP<7>."
        ));
        layout.slot(48, (_, viewCtx) -> meterItem((SkyBlockPlayer) viewCtx.player()),
                (_, viewCtx) -> cycleMeter((SkyBlockPlayer) viewCtx.player(), viewCtx));
    }

    private ItemStack.Builder meterItem(SkyBlockPlayer player) {
        var meter = RNGMeterService.get(player, ExperimentationRNGMeter.INSTANCE);
        ExperimentReward reward = ExperimentReward.fromName(meter.selectedReward());
        double percent = meter.storedXp() * 100d / reward.requiredXp();
        return ExperimentationGuiSupport.item("<d>Experimentation RNG Meter", Material.MAGENTA_DYE, 1,
                "<7>Selected: " + reward.displayName(),
                "<7>Progress: <d>" + String.format("%.1f", meter.storedXp()) + "<7>/<d>" + reward.meterRequirement()
                        + " <7>(" + String.format("%.1f", Math.min(100, percent)) + "%)",
                "", "<e>Click to change your selected reward!");
    }

    private void cycleMeter(SkyBlockPlayer player, ViewContext ctx) {
        ExperimentReward current = ExperimentReward.fromName(
                RNGMeterService.get(player, ExperimentationRNGMeter.INSTANCE).selectedReward());
        ExperimentReward[] rewards = Arrays.stream(ExperimentReward.values())
                .filter(reward -> reward.meterRequirement() > 0).toArray(ExperimentReward[]::new);
        int index = Arrays.asList(rewards).indexOf(current);
        ExperimentReward next = rewards[(index + 1) % rewards.length];
        RNGMeterService.select(player, ExperimentationRNGMeter.INSTANCE, next);
        player.sendMessage("<a>You selected " + next.displayName() + "<a> for your Experimentation RNG Meter!");
        ctx.session(DefaultState.class).refresh();
    }
}
