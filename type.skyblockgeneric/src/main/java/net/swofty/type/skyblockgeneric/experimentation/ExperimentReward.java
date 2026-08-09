package net.swofty.type.skyblockgeneric.experimentation;

import net.minestom.server.item.Material;
import net.swofty.commons.skyblock.item.ItemType;
import net.swofty.type.skyblockgeneric.item.SkyBlockItem;
import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;

import java.util.Arrays;

public enum ExperimentReward {
    EXPERIENCE("§bEnchanting XP", Material.EXPERIENCE_BOTTLE, ItemType.EXPERIENCE_BOTTLE, 0),
    GRAND_EXPERIENCE_BOTTLE("§aGrand Experience Bottle", Material.EXPERIENCE_BOTTLE, ItemType.EXPERIENCE_BOTTLE, 0),
    TITANIC_EXPERIENCE_BOTTLE("§9Titanic Experience Bottle", Material.EXPERIENCE_BOTTLE, ItemType.EXPERIENCE_BOTTLE, 15_000),
    EXPERIMENT_THE_FISH("§dExperiment the Fish", Material.TROPICAL_FISH, ItemType.TROPICAL_FISH, 50_000),
    METAPHYSICAL_SERUM("§5Metaphysical Serum", Material.POTION, ItemType.GLASS_BOTTLE, 50_000),
    GUARDIAN_PET("§5Guardian Pet", Material.PLAYER_HEAD, ItemType.ENCHANTED_EGG, 50_000),
    NADESHIKO_DYE("§5Nadeshiko Dye", Material.PINK_DYE, ItemType.PINK_DYE, 500_000),
    SCAVENGER_V("§9Scavenger V", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 150_000),
    SHARPNESS_VI("§9Sharpness VI", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 150_000),
    LIFE_STEAL_IV("§9Life Steal IV", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 150_000),
    POWER_VI("§9Power VI", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 150_000),
    GROWTH_VI("§9Growth VI", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 150_000),
    CRITICAL_VI("§9Critical VI", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 150_000),
    CUBISM_VI("§9Cubism VI", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 150_000),
    ENDER_SLAYER_VI("§9Ender Slayer VI", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 150_000),
    EXECUTE_VI("§9Execute VI", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 150_000),
    EXPERIENCE_IV("§9Experience IV", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 150_000),
    FIRE_ASPECT_III("§9Fire Aspect III", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 150_000),
    FIRST_STRIKE_IV("§9First Strike IV", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 150_000),
    FIRE_PROTECTION_VI("§9Fire Protection VI", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 150_000),
    GIANT_KILLER_VI("§9Giant Killer VI", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 150_000),
    PROTECTION_VI("§9Protection VI", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 150_000),
    TITAN_KILLER_VI("§9Titan Killer VI", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 150_000),
    CHANCE_V("§6Chance V", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 500_000),
    CRITICAL_VII("§6Critical VII", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 500_000),
    ENDER_SLAYER_VII("§6Ender Slayer VII", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 500_000),
    EXECUTE_VII("§6Execute VII", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 500_000),
    FIRST_STRIKE_V("§6First Strike V", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 500_000),
    GIANT_KILLER_VII("§6Giant Killer VII", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 500_000),
    GROWTH_VII("§6Growth VII", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 500_000),
    LUCK_VII("§6Luck VII", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 500_000),
    POWER_VII("§6Power VII", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 500_000),
    PROTECTION_VII("§6Protection VII", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 500_000),
    SHARPNESS_VII("§6Sharpness VII", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 500_000),
    SYPHON_V("§6Syphon V", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 500_000),
    TITAN_KILLER_VII("§6Titan Killer VII", Material.ENCHANTED_BOOK, ItemType.ENCHANTED_BOOK, 500_000);

    private final String displayName;
    private final Material material;
    private final ItemType itemType;
    private final int meterRequirement;

    ExperimentReward(String displayName, Material material, ItemType itemType, int meterRequirement) {
        this.displayName = displayName;
        this.material = material;
        this.itemType = itemType;
        this.meterRequirement = meterRequirement;
    }

    public String displayName() { return displayName; }
    public Material material() { return material; }
    public int meterRequirement() { return meterRequirement; }

    public void give(SkyBlockPlayer player) {
        player.addAndUpdateItem(new SkyBlockItem(itemType));
        player.sendMessage("§aExperiment reward: " + displayName + "§a!");
    }

    public static ExperimentReward fromName(String name) {
        return Arrays.stream(values()).filter(reward -> reward.name().equals(name)).findFirst().orElse(TITANIC_EXPERIENCE_BOTTLE);
    }
}
