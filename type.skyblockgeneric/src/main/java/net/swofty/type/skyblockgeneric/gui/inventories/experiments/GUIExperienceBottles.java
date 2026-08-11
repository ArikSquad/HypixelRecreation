package net.swofty.type.skyblockgeneric.gui.inventories.experiments;

import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.swofty.commons.StringUtility;
import net.swofty.commons.text.Text;
import net.swofty.type.generic.gui.inventory.ItemStacks;
import net.swofty.type.generic.gui.v2.Components;
import net.swofty.type.generic.gui.v2.DefaultState;
import net.swofty.type.generic.gui.v2.StatelessView;
import net.swofty.type.generic.gui.v2.ViewConfiguration;
import net.swofty.type.generic.gui.v2.ViewLayout;
import net.swofty.type.generic.gui.v2.context.ViewContext;
import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;

import java.util.List;

public final class GUIExperienceBottles extends StatelessView {
    private static final Bottle[] BOTTLES = {
            new Bottle(11, "<f>Experience Bottle", 8),
            new Bottle(12, "<a>Grand Experience Bottle", 1_500),
            new Bottle(14, "<9>Titanic Experience Bottle", 250_000),
            new Bottle(15, "<5>Colossal Experience Bottle", 500_000)
    };

    @Override
    public ViewConfiguration<DefaultState> configuration() {
        return new ViewConfiguration<>("Bottles of Enchanting", InventoryType.CHEST_4_ROW);
    }

    @Override
    public void layout(ViewLayout<DefaultState> layout, DefaultState state, ViewContext ctx) {
        Components.fill(layout);
        for (Bottle bottle : BOTTLES) {
            layout.slot(bottle.slot(), (s, c) -> bottleItem(bottle, (SkyBlockPlayer) c.player()));
        }
        if (!Components.back(layout, 30, ctx)) Components.close(layout, 30);
        Components.close(layout, 31);
    }

    private static ItemStack.Builder bottleItem(Bottle bottle, SkyBlockPlayer player) {
        long experience = Math.round(bottle.baseExperience() * (1 + player.getSkills()
                .getCurrentLevel(net.swofty.type.skyblockgeneric.skill.SkillCategories.ENCHANTING) * .05));
        int currentLevel = player.getLevel();
        int appliedLevel = levelForExperience(player.getExperience() + experience);
        return ItemStacks.item(Material.EXPERIENCE_BOTTLE, 1, Text.of(bottle.name()), List.of(
                Text.of("<7>Grants <3>{} <7>experience orbs. Buying", StringUtility.commaify(experience)),
                Text.of("<7>this directly will instantly consume it!"),
                Text.empty(),
                Text.of("<7>Your Exp Level: <3>{}", currentLevel),
                Text.of("<7>Level When Applied: <3>{}", appliedLevel),
                Text.empty(),
                Text.of("<7>Cost"),
                Text.of(bottle.name() + " <c>✖"),
                Text.empty(),
                Text.of("<7>Bazaar Price"),
                Text.of("<c>N/A"),
                Text.empty(),
                Text.of("<8><o>You are missing some of the cost,"),
                Text.of("<8><o>but you can buy it from the Bazaar!")
        ));
    }

    private static int levelForExperience(long value) {
        if (value <= 352) return (int) (Math.sqrt(value + 9) - 3);
        if (value <= 1_507) return (int) (8.1 + Math.sqrt((2.0 / 5.0) * (value - 195.975)));
        return (int) (18.0555 + Math.sqrt((2.0 / 9.0) * (value - 752.9861)));
    }

    private record Bottle(int slot, String name, long baseExperience) {
    }
}
