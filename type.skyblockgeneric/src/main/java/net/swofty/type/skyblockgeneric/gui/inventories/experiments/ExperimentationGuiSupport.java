package net.swofty.type.skyblockgeneric.gui.inventories.experiments;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.swofty.commons.StringUtility;
import net.swofty.type.generic.gui.inventory.ItemStacks;
import net.swofty.commons.text.Text;
import net.swofty.type.skyblockgeneric.experimentation.ExperimentTier;
import net.swofty.type.skyblockgeneric.experimentation.ExperimentType;
import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;

import java.util.Arrays;

final class ExperimentationGuiSupport {
    private static final String CHRONOMATRON_TEXTURE = "ewogICJ0aW1lc3RhbXAiIDogMTYyMjcwOTgxNjYwOSwKICAicHJvZmlsZUlkIiA6ICI2MTZiODhkNDMwNzM0ZTM3OWM3NDc1ODdlZTJkNzlmZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJfX25vdGFodW1hbl9fIiwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzgxYjg0MzQ1MTE4NGE4Y2NkOGU2ZTQ5ZDBlZGYzNDUxZDNkZWE1MGZkZTViNmEyZjk4YWI3Y2YxMTM4YmNlY2UiLAogICAgICAibWV0YWRhdGEiIDogewogICAgICAgICJtb2RlbCIgOiAic2xpbSIKICAgICAgfQogICAgfQogIH0KfQ==";
    private static final String SUPERPAIRS_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA3M2JlMzBjMDgxNzZlMTI5ZmE1N2VlOTAyNTQwNzE5NTBkMWVhNWFlNDIyYTc4NTEyZjdhNjQ3ZDk4YzViNSJ9fX0=";
    private static final String ULTRASEQUENCER_TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2M2MWE3ZDExNmFiOWNkOWE1ZjljMTZhMTNlZjg3NzlhMmI4ZWUyNzQyMzRmYWVjOGJmNThkY2NiMGQ5NDc0NiJ9fX0=";

    private ExperimentationGuiSupport() {
    }

    static ItemStack.Builder item(String name, Material material, int amount, String... lore) {
        return ItemStacks.item(material, amount, Text.of(name),
                Arrays.stream(lore).map(Text::of).toList());
    }

    static ItemStack.Builder head(String name, String texture, int amount, String... lore) {
        return ItemStacks.head(texture, amount, Text.of(name),
                Arrays.stream(lore).map(Text::of).toList());
    }

    static ItemStack.Builder experimentIcon(ExperimentType type) {
        return switch (type) {
            case CHRONOMATRON -> head("<d>Chronomatron", CHRONOMATRON_TEXTURE, 1,
                    "<7>Repeat the pattern to form the longest chain.", "", "<e>Click to browse tiers!");
            case SUPERPAIRS -> head("<d>Superpairs", SUPERPAIRS_TEXTURE, 1,
                    "<7>Find pairs of items on the grid.", "", "<e>Click to browse tiers!");
            case ULTRASEQUENCER -> head("<d>Ultrasequencer", ULTRASEQUENCER_TEXTURE, 1,
                    "<7>Remember the numbers and click them in order.", "", "<e>Click to browse tiers!");
        };
    }

    static ItemStack.Builder tierIcon(ExperimentType type, ExperimentTier tier, SkyBlockPlayer player) {
        boolean unlocked = tier.isUnlocked(player);
        Material material = unlocked ? tier.icon() : Material.GRAY_DYE;
        String color = switch (tier) {
            case HIGH -> "<a>";
            case GRAND -> "<e>";
            case SUPREME -> "<6>";
            case TRANSCENDENT -> "<c>";
            case METAPHYSICAL -> "<d>";
        };
        String requirement = "<7>Requires: <b>Enchanting "
                + StringUtility.getAsRomanNumeral(tier.requiredEnchantingLevel());
        String action = unlocked ? "<e>Click to play!" : "<c>Locked";
        return item(
                color + tier.displayName() + " Experiment",
                material,
                1,
                "<7>" + type.displayName(),
                "",
                requirement,
                "<7>XP per step: <b>" + tier.xpPerStep(),
                "",
                action
        );
    }
}
