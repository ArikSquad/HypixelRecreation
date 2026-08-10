package net.swofty.type.skyblockgeneric.gui.inventories.experiments;

import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.Material;
import net.swofty.type.generic.gui.inventory.ItemStacks;
import net.swofty.type.generic.gui.v2.*;
import net.swofty.type.generic.gui.v2.context.ViewContext;
import net.swofty.type.skyblockgeneric.experimentation.ExperimentTier;
import net.swofty.type.skyblockgeneric.experimentation.ExperimentType;

public final class GUIExperimentOver extends StatelessView {
    private final ExperimentType experiment;
    private final ExperimentTier tier;
    private final boolean completed;
    private final String message;
    private final int score;
    private final int xp;
    private final int bonusClicks;

    public GUIExperimentOver(ExperimentType experiment, ExperimentTier tier, boolean completed, String message,
                             int score, int xp, int bonusClicks) {
        this.experiment = experiment;
        this.tier = tier;
        this.completed = completed;
        this.message = message;
        this.score = score;
        this.xp = xp;
        this.bonusClicks = bonusClicks;
    }

    @Override
    public ViewConfiguration<DefaultState> configuration() {
        return new ViewConfiguration<>(experiment.displayName() + " Results", InventoryType.CHEST_3_ROW);
    }

    @Override
    public void layout(ViewLayout<DefaultState> layout, DefaultState state, ViewContext ctx) {
        Components.fill(layout);
        Components.backOrClose(layout, 22, ctx);
        layout.slot(13, ExperimentationGuiSupport.item(
                completed ? "<a>Experiment Complete!" : "<c>Experiment Over",
                completed ? Material.LIME_DYE : Material.RED_DYE,
                1,
                "<7>" + experiment.displayName() + " · " + tier.displayName(),
                "",
                "<7>" + message,
                "",
                "<7>Best score: <e>" + score,
                "<7>Enchanting XP: <b>+" + xp,
                bonusClicks > 0 ? "<7>Superpairs clicks earned: <a>+" + bonusClicks : ""
        ));
    }
}
