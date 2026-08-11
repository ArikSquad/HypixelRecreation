package net.swofty.type.skyblockgeneric.gui.inventories.experiments;

import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.Material;
import net.swofty.type.generic.gui.v2.Components;
import net.swofty.type.generic.gui.v2.DefaultState;
import net.swofty.type.generic.gui.v2.StatelessView;
import net.swofty.type.generic.gui.v2.ViewConfiguration;
import net.swofty.type.generic.gui.v2.ViewLayout;
import net.swofty.type.generic.gui.v2.context.ViewContext;
import net.swofty.type.skyblockgeneric.experimentation.ExperimentType;
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
                ExperimentationGuiSupport.item("<7>Pending experiment...", Material.PINK_STAINED_GLASS_PANE, 1));
        layout.slot(50, ExperimentationGuiSupport.item(
                "<3>Experience Bottles",
                Material.EXPERIENCE_BOTTLE,
                1,
                "<7>Missing experience?",
                "<7>Simple! Just consume the <3>Experience",
                "<3>Bottles <7>from your inventories",
                "<7>directly or purchase some at the",
                "<7>current <6>Bazaar <7>price!",
                "",
                "<e>Click to view!"), (_, viewCtx) -> viewCtx.push(new GUIExperienceBottles()));
        layout.slot(48, (_, viewCtx) -> RNGMeterGuiSupport.meterItem(
                        net.swofty.type.skyblockgeneric.experimentation.ExperimentationRNGMeter.INSTANCE,
                        (SkyBlockPlayer) viewCtx.player()),
                (_, viewCtx) -> viewCtx.push(new GUIExperimentationRNGMeter()));
    }
}
