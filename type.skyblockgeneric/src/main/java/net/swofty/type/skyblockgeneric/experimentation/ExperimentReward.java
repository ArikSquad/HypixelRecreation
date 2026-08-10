package net.swofty.type.skyblockgeneric.experimentation;

import net.minestom.server.item.Material;
import net.swofty.commons.skyblock.item.ItemType;
import net.swofty.type.skyblockgeneric.enchantment.EnchantmentType;
import net.swofty.type.skyblockgeneric.enchantment.SkyBlockEnchantment;
import net.swofty.type.skyblockgeneric.item.SkyBlockItem;
import net.swofty.type.skyblockgeneric.rngmeter.RNGMeterReward;
import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;

import java.util.Arrays;
import java.util.function.Supplier;

public enum ExperimentReward implements RNGMeterReward {
    EXPERIENCE("<b>Enchanting XP", ItemType.EXPERIENCE_BOTTLE, 0),
    GRAND_EXPERIENCE_BOTTLE("<a>Grand Experience Bottle", ItemType.GRAND_EXP_BOTTLE, 0),
    TITANIC_EXPERIENCE_BOTTLE("<9>Titanic Experience Bottle", ItemType.TITANIC_EXP_BOTTLE, 15_000),
    EXPERIMENT_THE_FISH("<d>Experiment the Fish", ItemType.EXPERIMENT_THE_FISH, 50_000),
    METAPHYSICAL_SERUM("<5>Metaphysical Serum", ItemType.METAPHYSICAL_SERUM, 50_000),
    GUARDIAN_PET("<5>Guardian Pet", ItemType.GUARDIAN_PET, 50_000),
    NADESHIKO_DYE("<5>Nadeshiko Dye", ItemType.NADESHIKO_DYE, 500_000),
    SCAVENGER_V("<9>Scavenger V", EnchantmentType.SCAVENGER, 5, 150_000),
    SHARPNESS_VI("<9>Sharpness VI", EnchantmentType.SHARPNESS, 6, 150_000),
    LIFE_STEAL_IV("<9>Life Steal IV", EnchantmentType.LIFE_STEAL, 4, 150_000),
    POWER_VI("<9>Power VI", EnchantmentType.POWER, 6, 150_000),
    GROWTH_VI("<9>Growth VI", EnchantmentType.GROWTH, 6, 150_000),
    CRITICAL_VI("<9>Critical VI", EnchantmentType.CRITICAL, 6, 150_000),
    CUBISM_VI("<9>Cubism VI", EnchantmentType.CUBISM, 6, 150_000),
    ENDER_SLAYER_VI("<9>Ender Slayer VI", EnchantmentType.ENDER_SLAYER, 6, 150_000),
    EXECUTE_VI("<9>Execute VI", EnchantmentType.EXECUTE, 6, 150_000),
    EXPERIENCE_IV("<9>Experience IV", EnchantmentType.EXPERIENCE, 4, 150_000),
    FIRE_ASPECT_III("<9>Fire Aspect III", EnchantmentType.FIRE_ASPECT, 3, 150_000),
    FIRST_STRIKE_IV("<9>First Strike IV", EnchantmentType.FIRST_STRIKE, 4, 150_000),
    FIRE_PROTECTION_VI("<9>Fire Protection VI", EnchantmentType.FIRE_PROTECTION, 6, 150_000),
    GIANT_KILLER_VI("<9>Giant Killer VI", EnchantmentType.GIANT_KILLER, 6, 150_000),
    PROTECTION_VI("<9>Protection VI", EnchantmentType.PROTECTION, 6, 150_000),
    TITAN_KILLER_VI("<9>Titan Killer VI", EnchantmentType.TITAN_KILLER, 6, 150_000),
    CHANCE_V("<6>Chance V", EnchantmentType.CHANCE, 5, 500_000),
    CRITICAL_VII("<6>Critical VII", EnchantmentType.CRITICAL, 7, 500_000),
    ENDER_SLAYER_VII("<6>Ender Slayer VII", EnchantmentType.ENDER_SLAYER, 7, 500_000),
    EXECUTE_VII("<6>Execute VII", EnchantmentType.EXECUTE, 7, 500_000),
    FIRST_STRIKE_V("<6>First Strike V", EnchantmentType.FIRST_STRIKE, 5, 500_000),
    GIANT_KILLER_VII("<6>Giant Killer VII", EnchantmentType.GIANT_KILLER, 7, 500_000),
    GROWTH_VII("<6>Growth VII", EnchantmentType.GROWTH, 7, 500_000),
    LUCK_VII("<6>Luck VII", EnchantmentType.LUCK, 7, 500_000),
    POWER_VII("<6>Power VII", EnchantmentType.POWER, 7, 500_000),
    PROTECTION_VII("<6>Protection VII", EnchantmentType.PROTECTION, 7, 500_000),
    SHARPNESS_VII("<6>Sharpness VII", EnchantmentType.SHARPNESS, 7, 500_000),
    TITAN_KILLER_VII("<6>Titan Killer VII", EnchantmentType.TITAN_KILLER, 7, 500_000);

    private final String displayName;
    private final Material material;
    private final Supplier<SkyBlockItem> itemSupplier;
    private final int meterRequirement;

    ExperimentReward(String displayName, ItemType itemType, int meterRequirement) {
        this(displayName, itemType.material, () -> new SkyBlockItem(itemType), meterRequirement);
    }

    ExperimentReward(String displayName, EnchantmentType enchantmentType, int enchantmentLevel, int meterRequirement) {
        this(displayName, ItemType.ENCHANTED_BOOK.material,
                () -> enchantedBook(enchantmentType, enchantmentLevel), meterRequirement);
    }

    ExperimentReward(String displayName, Material material, Supplier<SkyBlockItem> itemSupplier, int meterRequirement) {
        this.displayName = displayName;
        this.material = material;
        this.itemSupplier = itemSupplier;
        this.meterRequirement = meterRequirement;
    }

    public String displayName() { return displayName; }
    public String id() { return name(); }
    public double requiredXp() { return meterRequirement; }
    public Material material() { return material; }
    public int meterRequirement() { return meterRequirement; }

    public SkyBlockItem createItem() {
        return itemSupplier.get();
    }

    public void give(SkyBlockPlayer player) {
        player.addAndUpdateItem(createItem());
        player.sendMessage("<a>Experiment reward: " + displayName + "<a>!");
    }

    public static ExperimentReward fromName(String name) {
        return Arrays.stream(values()).filter(reward -> reward.name().equals(name)).findFirst()
                .orElse(TITANIC_EXPERIENCE_BOTTLE);
    }

    private static SkyBlockItem enchantedBook(EnchantmentType type, int level) {
        SkyBlockItem item = new SkyBlockItem(ItemType.ENCHANTED_BOOK);
        item.getAttributeHandler().addEnchantment(new SkyBlockEnchantment(type, level));
        return item;
    }
}
