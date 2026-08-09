package net.swofty.type.skyblockgeneric.experimentation;

import net.minestom.server.item.Material;
import net.swofty.commons.skyblock.item.ItemType;
import net.swofty.type.skyblockgeneric.enchantment.EnchantmentType;
import net.swofty.type.skyblockgeneric.enchantment.SkyBlockEnchantment;
import net.swofty.type.skyblockgeneric.item.SkyBlockItem;
import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;

import java.util.Arrays;

public enum ExperimentReward {
    EXPERIENCE("§bEnchanting XP", ItemType.EXPERIENCE_BOTTLE, 0),
    GRAND_EXPERIENCE_BOTTLE("§aGrand Experience Bottle", ItemType.GRAND_EXP_BOTTLE, 0),
    TITANIC_EXPERIENCE_BOTTLE("§9Titanic Experience Bottle", ItemType.TITANIC_EXP_BOTTLE, 15_000),
    EXPERIMENT_THE_FISH("§dExperiment the Fish", ItemType.EXPERIMENT_THE_FISH, 50_000),
    METAPHYSICAL_SERUM("§5Metaphysical Serum", ItemType.METAPHYSICAL_SERUM, 50_000),
    GUARDIAN_PET("§5Guardian Pet", ItemType.GUARDIAN_PET, 50_000),
    NADESHIKO_DYE("§5Nadeshiko Dye", ItemType.NADESHIKO_DYE, 500_000),
    SCAVENGER_V("§9Scavenger V", EnchantmentType.SCAVENGER, 5, 150_000),
    SHARPNESS_VI("§9Sharpness VI", EnchantmentType.SHARPNESS, 6, 150_000),
    LIFE_STEAL_IV("§9Life Steal IV", EnchantmentType.LIFE_STEAL, 4, 150_000),
    POWER_VI("§9Power VI", EnchantmentType.POWER, 6, 150_000),
    GROWTH_VI("§9Growth VI", EnchantmentType.GROWTH, 6, 150_000),
    CRITICAL_VI("§9Critical VI", EnchantmentType.CRITICAL, 6, 150_000),
    CUBISM_VI("§9Cubism VI", EnchantmentType.CUBISM, 6, 150_000),
    ENDER_SLAYER_VI("§9Ender Slayer VI", EnchantmentType.ENDER_SLAYER, 6, 150_000),
    EXECUTE_VI("§9Execute VI", EnchantmentType.EXECUTE, 6, 150_000),
    EXPERIENCE_IV("§9Experience IV", EnchantmentType.EXPERIENCE, 4, 150_000),
    FIRE_ASPECT_III("§9Fire Aspect III", EnchantmentType.FIRE_ASPECT, 3, 150_000),
    FIRST_STRIKE_IV("§9First Strike IV", EnchantmentType.FIRST_STRIKE, 4, 150_000),
    FIRE_PROTECTION_VI("§9Fire Protection VI", EnchantmentType.FIRE_PROTECTION, 6, 150_000),
    GIANT_KILLER_VI("§9Giant Killer VI", EnchantmentType.GIANT_KILLER, 6, 150_000),
    PROTECTION_VI("§9Protection VI", EnchantmentType.PROTECTION, 6, 150_000),
    TITAN_KILLER_VI("§9Titan Killer VI", EnchantmentType.TITAN_KILLER, 6, 150_000),
    CHANCE_V("§6Chance V", EnchantmentType.CHANCE, 5, 500_000),
    CRITICAL_VII("§6Critical VII", EnchantmentType.CRITICAL, 7, 500_000),
    ENDER_SLAYER_VII("§6Ender Slayer VII", EnchantmentType.ENDER_SLAYER, 7, 500_000),
    EXECUTE_VII("§6Execute VII", EnchantmentType.EXECUTE, 7, 500_000),
    FIRST_STRIKE_V("§6First Strike V", EnchantmentType.FIRST_STRIKE, 5, 500_000),
    GIANT_KILLER_VII("§6Giant Killer VII", EnchantmentType.GIANT_KILLER, 7, 500_000),
    GROWTH_VII("§6Growth VII", EnchantmentType.GROWTH, 7, 500_000),
    LUCK_VII("§6Luck VII", EnchantmentType.LUCK, 7, 500_000),
    POWER_VII("§6Power VII", EnchantmentType.POWER, 7, 500_000),
    PROTECTION_VII("§6Protection VII", EnchantmentType.PROTECTION, 7, 500_000),
    SHARPNESS_VII("§6Sharpness VII", EnchantmentType.SHARPNESS, 7, 500_000),
    TITAN_KILLER_VII("§6Titan Killer VII", EnchantmentType.TITAN_KILLER, 7, 500_000);

    private final String displayName;
    private final ItemType itemType;
    private final EnchantmentType enchantmentType;
    private final int enchantmentLevel;
    private final int meterRequirement;

    ExperimentReward(String displayName, ItemType itemType, int meterRequirement) {
        this(displayName, itemType, null, 0, meterRequirement);
    }

    ExperimentReward(String displayName, EnchantmentType enchantmentType, int enchantmentLevel, int meterRequirement) {
        this(displayName, ItemType.ENCHANTED_BOOK, enchantmentType, enchantmentLevel, meterRequirement);
    }

    ExperimentReward(String displayName, ItemType itemType, EnchantmentType enchantmentType,
                     int enchantmentLevel, int meterRequirement) {
        this.displayName = displayName;
        this.itemType = itemType;
        this.enchantmentType = enchantmentType;
        this.enchantmentLevel = enchantmentLevel;
        this.meterRequirement = meterRequirement;
    }

    public String displayName() { return displayName; }
    public Material material() { return itemType.material; }
    public int meterRequirement() { return meterRequirement; }

    public SkyBlockItem createItem() {
        SkyBlockItem item = new SkyBlockItem(itemType);
        if (enchantmentType != null) {
            item.getAttributeHandler().addEnchantment(new SkyBlockEnchantment(enchantmentType, enchantmentLevel));
        }
        return item;
    }

    public void give(SkyBlockPlayer player) {
        player.addAndUpdateItem(createItem());
        player.sendMessage("§aExperiment reward: " + displayName + "§a!");
    }

    public static ExperimentReward fromName(String name) {
        return Arrays.stream(values()).filter(reward -> reward.name().equals(name)).findFirst()
                .orElse(TITANIC_EXPERIENCE_BOTTLE);
    }
}
