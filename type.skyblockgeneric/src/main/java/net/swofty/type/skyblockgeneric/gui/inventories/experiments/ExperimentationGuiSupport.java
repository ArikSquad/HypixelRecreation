package net.swofty.type.skyblockgeneric.gui.inventories.experiments;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.swofty.commons.StringUtility;
import net.swofty.commons.text.Text;
import net.swofty.type.generic.gui.inventory.ItemStacks;
import net.swofty.type.skyblockgeneric.experimentation.ExperimentTier;
import net.swofty.type.skyblockgeneric.experimentation.ExperimentType;
import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;

import java.util.Arrays;

final class ExperimentationGuiSupport {
    private static final String SUPERPAIRS_TEXTURE = "81b843451184a8ccd8e6e49d0edf3451d3dea50fde5b6a2f98ab7cf1138bcece";
    private static final String CHRONOMATRON_TEXTURE = "bc162a5ab7039b6042c59055e88e05088006c1e9b1487e857f16fbf4f52fb6da";
    private static final String ULTRASEQUENCER_TEXTURE = "8b0cd070eb6d29d0903086f0ca75d30a1484928550c24f48ac07ae7a7000bdc9";

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
                    "<7>Add-on Experiment", "",
                    "<7>Repeat <a>the musical <d>pattern <7>to form the",
                    "<7>longest chain of notes.", "",
                    "<7>Earn extra <3>Enchanting Exp <7>and",
                    "<7>extra clicks on your next", "<d>Superpairs<7>!", "",
                    "<e>Click to play!");
            case SUPERPAIRS -> head("<d>Superpairs", SUPERPAIRS_TEXTURE, 1,
                    "<7>Main Experiment", "",
                    "<7>Find <b>pairs <7>of items on the grid to",
                    "<7>unlock them.", "",
                    "<7>Earn <3>Enchanting Exp <7>and <c>powerful",
                    "<7>enchanted books every day!", "",
                    "<7>Charges: <d>3<5>/<d>3", "",
                    "<d>Play add-ons first!");
            case ULTRASEQUENCER -> head("<d>Ultrasequencer", ULTRASEQUENCER_TEXTURE, 1,
                    "<7>Add-on Experiment", "",
                    "<a>1. <7>Number(s) appear for 2 seconds.",
                    "<e>2. <7>They all disappear!",
                    "<c>3. <7>Click them in order from memory.", "",
                    "<7>Earn extra <3>Enchanting Exp <7>and",
                    "<7>extra clicks on your next", "<d>Superpairs<7>!", "",
                    "<e>Click to play!");
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
        String action = unlocked ? "<e>Click to play!" : "<c>Enchanting level too low!";
        return switch (type) {
            case CHRONOMATRON -> item(color + tier.displayName() + " Experiment", material, 1,
                    "<7>Chronomatron", "",
                    "<7>Colors on board: <d>" + tier.colorCount(), "",
                    "<7>XP Reward: <3>" + shortNumber(tier.xpReward(ExperimentType.CHRONOMATRON)) + " Enchanting Exp",
                    "<7>per <e>note in longest chain<7>!", "",
                    "<7>Superpairs Rewards:",
                    "<7> Chain of 5: <e>+1 Click", "<7> Chain of 9: <e>+2 Clicks", "",
                    requirement, "", action);
            case ULTRASEQUENCER -> item(color + tier.displayName() + " Experiment", material, 1,
                    "<7>Ultrasequencer", "",
                    "<7>Grid Size: <d>" + ultraGridSize(tier), "",
                    "<7>XP Reward: <3>" + shortNumber(tier.xpReward(ExperimentType.ULTRASEQUENCER)) + " Enchanting Exp",
                    "<7>per <e>number in highest series<7>!", "",
                    "<7>Superpairs Rewards:",
                    "<7> Series of 5: <e>+1 Click", "<7> Series of 7: <e>+2 Clicks", "",
                    requirement, "", action);
            case SUPERPAIRS -> item(color + tier.displayName() + " Experiment", material, 1,
                    "<7>Superpairs", "", "<7>Pairs on board: <d>8", "",
                    "<7>XP Reward: <3>" + shortNumber(tier.superPairsXpPerPair()) + " Enchanting Exp",
                    "<7>per matched pair!", "", requirement, "", action);
        };
    }

    private static String ultraGridSize(ExperimentTier tier) {
        return switch (tier) {
            case HIGH, GRAND -> "5x2";
            case SUPREME -> "7x3";
            case TRANSCENDENT -> "7x4";
            case METAPHYSICAL -> "9x4";
        };
    }

    private static String shortNumber(int number) {
        return StringUtility.shortenNumber(number).replace("K", "k");
    }
}
