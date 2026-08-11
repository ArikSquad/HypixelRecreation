package net.swofty.type.skyblockgeneric.experimentation;

import net.kyori.adventure.key.Key;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.swofty.commons.skyblock.item.ItemType;
import net.swofty.commons.text.Text;
import net.swofty.type.generic.gui.inventory.ItemStacks;
import net.swofty.type.skyblockgeneric.enchantment.EnchantmentType;
import net.swofty.type.skyblockgeneric.enchantment.SkyBlockEnchantment;
import net.swofty.type.skyblockgeneric.item.SkyBlockItem;
import net.swofty.type.skyblockgeneric.loottable.BossDropRarity;
import net.swofty.type.skyblockgeneric.loottable.LootAnnouncement;
import net.swofty.type.skyblockgeneric.rngmeter.RNGMeterLoot;
import net.swofty.type.skyblockgeneric.rngmeter.RNGMeterReward;
import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public enum ExperimentReward implements RNGMeterReward {
    EXPERIENCE("<b>Enchanting XP", ItemType.EXPERIENCE_BOTTLE, 0),
    GRAND_EXPERIENCE_BOTTLE("<a>Grand Experience Bottle", ItemType.GRAND_EXP_BOTTLE, 0),

    TITANIC_EXPERIENCE_BOTTLE("<9>Titanic Experience Bottle", ItemType.TITANIC_EXP_BOTTLE, 15_000),
    EXPERIMENT_THE_FISH("<c>Experiment the Fish", ItemType.EXPERIMENT_THE_FISH, 50_000),
    METAPHYSICAL_SERUM("<5>Metaphysical Serum", ItemType.METAPHYSICAL_SERUM, 50_000),
    SCAVENGER_V("<a>Scavenger V", EnchantmentType.SCAVENGER, 5, 150_000),
    SHARPNESS_VI("<9>Sharpness VI", EnchantmentType.SHARPNESS, 6, 150_000),
    LIFE_STEAL_IV("<f>Life Steal IV", EnchantmentType.LIFE_STEAL, 4, 150_000),
    POWER_VI("<9>Power VI", EnchantmentType.POWER, 6, 150_000),
    ENDER_SLAYER_VI("<9>Ender Slayer VI", EnchantmentType.ENDER_SLAYER, 6, 150_000),
    THUNDERBOLT_VI("<9>Thunderbolt VI", EnchantmentType.THUNDERBOLT, 6, 150_000),
    GROWTH_VI("<9>Growth VI", EnchantmentType.GROWTH, 6, 150_000),
    CHANCE_IV("<f>Chance IV", 150_000),
    BLAST_PROTECTION_VI("<9>Blast Protection VI", 150_000),
    RESPITE_III("<f>Respite III", 150_000),
    VENOMOUS_VI("<9>Venomous VI", EnchantmentType.VENOMOUS, 6, 150_000),
    PROJECTILE_PROTECTION_VI("<9>Projectile Protection VI", 150_000),
    GUARDIAN_PET("<7>[Lvl 1] <6>Guardian", ItemType.GUARDIAN_PET, 150_000),
    FIRE_PROTECTION_VI("<9>Fire Protection VI", EnchantmentType.FIRE_PROTECTION, 6, 150_000),
    WOODSPLITTER_VI("<9>Woodsplitter VI", EnchantmentType.WOODSPLITTER, 6, 150_000),
    GIANT_KILLER_VI("<9>Giant Killer VI", EnchantmentType.GIANT_KILLER, 6, 150_000),
    DRAIN_IV("<f>Drain IV", EnchantmentType.DRAIN, 4, 150_000),
    PROTECTION_VI("<9>Protection VI", EnchantmentType.PROTECTION, 6, 150_000),
    TITAN_KILLER_VI("<9>Titan Killer VI", EnchantmentType.TITAN_KILLER, 6, 150_000),

    A_BEGINNERS_GUIDE_TO_PESTHUNTING("<6>A Beginner's Guide to Pesthunting", Material.PLAYER_HEAD, 500_000),
    SEVERED_PINCER("<6>Severed Pincer", Material.PLAYER_HEAD, 500_000),
    CHANCE_V("<a>Chance V", EnchantmentType.CHANCE, 5, 500_000),
    THUNDERLORD_VII("<5>Thunderlord VII", EnchantmentType.THUNDERLORD, 7, 500_000),
    ENSNARED_SNAIL("<6>Ensnared Snail", Material.PLAYER_HEAD, 500_000),
    GIANT_KILLER_VII("<5>Giant Killer VII", EnchantmentType.GIANT_KILLER, 7, 500_000),

    GRAVITY_VI("<9>Gravity VI", EnchantmentType.GRAVITY, 6, 500_000),
    GOLDEN_BOUNTY("<6>Golden Bounty", Material.PLAYER_HEAD, 500_000),
    SEVERED_HAND("<6>Severed Hand", Material.PLAYER_HEAD, 500_000),
    CRITICAL_VII("<5>Critical VII", EnchantmentType.CRITICAL, 7, 500_000),
    VIBRANT_CORAL("<6>Vibrant Coral", Material.PLAYER_HEAD, 500_000),
    SNIPE_IV("<f>Snipe IV", 500_000),
    LIFE_STEAL_V("<a>Life Steal V", EnchantmentType.LIFE_STEAL, 5, 500_000),
    GOLD_BOTTLE_CAP("<6>Gold Bottle Cap", Material.PLAYER_HEAD, 500_000),
    LOOTING_V("<a>Looting V", EnchantmentType.LOOTING, 5, 500_000),
    FIRST_STRIKE_V("<a>First Strike V", EnchantmentType.FIRST_STRIKE, 5, 500_000),
    FIRE_PROTECTION_VII("<5>Fire Protection VII", EnchantmentType.FIRE_PROTECTION, 7, 500_000),
    THUNDERBOLT_VII("<5>Thunderbolt VII", EnchantmentType.THUNDERBOLT, 7, 500_000),
    CUBISM_VI("<9>Cubism VI", EnchantmentType.CUBISM, 6, 500_000),
    TRIPLE_STRIKE_V("<a>Triple-Strike V", 500_000),
    CHAIN_OF_THE_END_TIMES("<6>Chain of the End Times", Material.PLAYER_HEAD, 500_000),
    DRAIN_V("<a>Drain V", EnchantmentType.DRAIN, 5, 500_000),
    FATEFUL_STINGER("<6>Fateful Stinger", Material.PLAYER_HEAD, 500_000),
    BLAST_PROTECTION_VII("<5>Blast Protection VII", 500_000),
    CLEAVE_VI("<9>Cleave VI", EnchantmentType.CLEAVE, 6, 500_000),
    OCTOPUS_TENDRIL("<6>Octopus Tendril", Material.PLAYER_HEAD, 500_000),
    TITAN_KILLER_VII("<5>Titan Killer VII", EnchantmentType.TITAN_KILLER, 7, 500_000),
    LUCK_VII("<5>Luck VII", EnchantmentType.LUCK, 7, 500_000),
    END_STONE_IDOL("<6>End Stone Idol", Material.PLAYER_HEAD, 500_000),
    EXECUTE_VI("<9>Execute VI", EnchantmentType.EXECUTE, 6, 500_000),
    POWER_VII("<5>Power VII", EnchantmentType.POWER, 7, 500_000),
    TROUBLED_BUBBLE("<6>Troubled Bubble", Material.PLAYER_HEAD, 500_000),
    PROJECTILE_PROTECTION_VII("<5>Projectile Protection VII", 500_000),
    GROWTH_VII("<5>Growth VII", EnchantmentType.GROWTH, 7, 500_000),

    SHARPNESS_VII("<5>Sharpness VII", EnchantmentType.SHARPNESS, 7, 500_000),
    PROTECTION_VII("<5>Protection VII", EnchantmentType.PROTECTION, 7, 500_000),
    PROSECUTE_VI("<9>Prosecute VI", EnchantmentType.PROSECUTE, 6, 500_000),
    NADESHIKO_DYE("<d>Nadeshiko Dye", ItemType.NADESHIKO_DYE, 2_500_000);

    private final String displayName;
    private final Material material;
    private final Supplier<SkyBlockItem> itemSupplier;
    private final int meterRequirement;
    private static final Map<ExperimentReward, RNGMeterLoot> LOOT = createLootRegistry();

    ExperimentReward(String displayName, ItemType itemType, int meterRequirement) {
        this(displayName, itemType.material, () -> new SkyBlockItem(itemType), meterRequirement);
    }

    ExperimentReward(String displayName, EnchantmentType enchantmentType, int enchantmentLevel, int meterRequirement) {
        this(displayName, ItemType.ENCHANTED_BOOK.material,
                () -> enchantedBook(enchantmentType, enchantmentLevel), meterRequirement);
    }

    ExperimentReward(String displayName, int meterRequirement) {
        this(displayName, ItemType.ENCHANTED_BOOK.material,
                () -> new SkyBlockItem(ItemType.ENCHANTED_BOOK), meterRequirement);
    }

    ExperimentReward(String displayName, Material material, int meterRequirement) {
        this(displayName, material, () -> new SkyBlockItem(material), meterRequirement);
    }

    ExperimentReward(String displayName, Material material, Supplier<SkyBlockItem> itemSupplier, int meterRequirement) {
        this.displayName = displayName;
        this.material = material;
        this.itemSupplier = itemSupplier;
        this.meterRequirement = meterRequirement;
    }

    public String displayName() {
        return displayName;
    }

    public String id() {
        return name();
    }

    public double requiredXp() {
        return meterRequirement;
    }

    public Material material() {
        return material;
    }

    public int meterRequirement() {
        return meterRequirement;
    }

    public SkyBlockItem createItem() {
        return itemSupplier.get();
    }

    public ItemStack.Builder displayItem(SkyBlockPlayer player) {
        String texture = menuTexture();
        if (texture != null) return ItemStacks.head(texture, 1, Text.of(displayName), List.of());
        return ItemStacks.item(menuMaterial(), 1, Text.of(displayName), List.of());
    }

    public void give(SkyBlockPlayer player) {
        player.addAndUpdateItem(createItem());
        player.sendMessage("<a>Experiment reward: " + displayName + "<a>!");
        loot().announcement().announce(player, Text.of(displayName));
    }

    public RNGMeterLoot loot() {
        return LOOT.get(this);
    }

    private RNGMeterLoot createLoot() {
        BossDropRarity rarity;
        double chance;
        LootAnnouncement announcement;
        switch (this) {
            case EXPERIENCE, GRAND_EXPERIENCE_BOTTLE -> {
                rarity = BossDropRarity.COMMON;
                chance = 100;
                announcement = LootAnnouncement.NONE;
            }
            case TITANIC_EXPERIENCE_BOTTLE, EXPERIMENT_THE_FISH, METAPHYSICAL_SERUM -> {
                rarity = BossDropRarity.EXTRAORDINARY;
                chance = 1.0638;
                announcement = LootAnnouncement.NONE;
            }
            case SCAVENGER_V, SHARPNESS_VI, LIFE_STEAL_IV, POWER_VI, ENDER_SLAYER_VI,
                 THUNDERBOLT_VI, GROWTH_VI, CHANCE_IV, BLAST_PROTECTION_VI, RESPITE_III,
                 VENOMOUS_VI, PROJECTILE_PROTECTION_VI, FIRE_PROTECTION_VI, WOODSPLITTER_VI,
                 GIANT_KILLER_VI, DRAIN_IV, PROTECTION_VI, TITAN_KILLER_VI -> {
                rarity = BossDropRarity.RARE;
                chance = 5.3191;
                announcement = LootAnnouncement.RARE;
            }
            case NADESHIKO_DYE -> {
                rarity = BossDropRarity.RNGESUS_INCARNATE;
                chance = 0.004;
                announcement = LootAnnouncement.INSANE;
            }
            case GUARDIAN_PET, A_BEGINNERS_GUIDE_TO_PESTHUNTING, SEVERED_PINCER, CHANCE_V,
                 THUNDERLORD_VII, ENSNARED_SNAIL, GIANT_KILLER_VII, GRAVITY_VI, GOLDEN_BOUNTY,
                 SEVERED_HAND, CRITICAL_VII, VIBRANT_CORAL, SNIPE_IV, LIFE_STEAL_V,
                 GOLD_BOTTLE_CAP, LOOTING_V, FIRST_STRIKE_V, FIRE_PROTECTION_VII,
                 THUNDERBOLT_VII, CUBISM_VI, TRIPLE_STRIKE_V, CHAIN_OF_THE_END_TIMES, DRAIN_V,
                 FATEFUL_STINGER, BLAST_PROTECTION_VII, CLEAVE_VI, OCTOPUS_TENDRIL,
                 TITAN_KILLER_VII, LUCK_VII, END_STONE_IDOL, EXECUTE_VI, POWER_VII,
                 TROUBLED_BUBBLE, PROJECTILE_PROTECTION_VII, GROWTH_VII, SHARPNESS_VII,
                 PROTECTION_VII, PROSECUTE_VI -> {
                rarity = BossDropRarity.RNGESUS_INCARNATE;
                chance = 0.0263;
                announcement = LootAnnouncement.INSANE;
            }
            default -> throw new IllegalStateException("Missing loot definition for " + name());
        }
        return new RNGMeterLoot(Key.key("skyblock", "experimentation/" + name().toLowerCase()),
                rarity, chance, announcement);
    }

    private static Map<ExperimentReward, RNGMeterLoot> createLootRegistry() {
        Map<ExperimentReward, RNGMeterLoot> loot = new EnumMap<>(ExperimentReward.class);
        for (ExperimentReward reward : values()) loot.put(reward, reward.createLoot());
        return Map.copyOf(loot);
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

    private Material menuMaterial() {
        return switch (this) {
            case EXPERIMENT_THE_FISH -> Material.PUFFERFISH;
            case TITANIC_EXPERIENCE_BOTTLE, METAPHYSICAL_SERUM, SCAVENGER_V, GUARDIAN_PET,
                 A_BEGINNERS_GUIDE_TO_PESTHUNTING, SEVERED_PINCER, ENSNARED_SNAIL,
                 GOLDEN_BOUNTY, SEVERED_HAND, VIBRANT_CORAL, GOLD_BOTTLE_CAP,
                 CHAIN_OF_THE_END_TIMES, FATEFUL_STINGER, OCTOPUS_TENDRIL,
                 END_STONE_IDOL, TROUBLED_BUBBLE, NADESHIKO_DYE -> Material.PLAYER_HEAD;
            case SHARPNESS_VI, SHARPNESS_VII -> Material.DIAMOND_SWORD;
            case LIFE_STEAL_IV, LIFE_STEAL_V -> Material.NETHER_WART;
            case POWER_VI, POWER_VII -> Material.BOW;
            case ENDER_SLAYER_VI -> Material.ENDER_PEARL;
            case THUNDERBOLT_VI, THUNDERBOLT_VII -> Material.DIAMOND_BLOCK;
            case GROWTH_VI -> Material.LEATHER_CHESTPLATE;
            case CHANCE_IV, CHANCE_V -> Material.GLOWSTONE;
            case BLAST_PROTECTION_VI, BLAST_PROTECTION_VII -> Material.IRON_LEGGINGS;
            case RESPITE_III -> Material.GOLDEN_APPLE;
            case VENOMOUS_VI -> Material.MAGMA_CREAM;
            case PROJECTILE_PROTECTION_VI, PROJECTILE_PROTECTION_VII -> Material.LEATHER_HELMET;
            case FIRE_PROTECTION_VI, FIRE_PROTECTION_VII -> Material.LEATHER_LEGGINGS;
            case WOODSPLITTER_VI, GRAVITY_VI, CUBISM_VI -> Material.ENCHANTED_BOOK;
            case GIANT_KILLER_VI, GIANT_KILLER_VII -> Material.ZOMBIE_HEAD;
            case DRAIN_IV, DRAIN_V -> Material.POPPY;
            case PROTECTION_VI, PROTECTION_VII -> Material.DIAMOND_CHESTPLATE;
            case TITAN_KILLER_VI, TITAN_KILLER_VII -> Material.DRAGON_EGG;
            case THUNDERLORD_VII -> Material.ANVIL;
            case CRITICAL_VII -> Material.GOLDEN_SWORD;
            case SNIPE_IV -> Material.APPLE;
            case LOOTING_V -> Material.WHEAT;
            case FIRST_STRIKE_V -> Material.GLISTERING_MELON_SLICE;
            case TRIPLE_STRIKE_V -> Material.LEAD;
            case CLEAVE_VI -> Material.MELON_SEEDS;
            case LUCK_VII -> Material.LEATHER;
            case EXECUTE_VI -> Material.BONE;
            case PROSECUTE_VI -> Material.SEA_LANTERN;
            default -> material;
        };
    }

    private String menuTexture() {
        return switch (this) {
            case TITANIC_EXPERIENCE_BOTTLE -> "e8452831c100a14756082c588178e62704c6ebb730517c915ce28504118a50af";
            case METAPHYSICAL_SERUM -> "1dbf5e10d0837f20dd7ae691aa6b5c508e4ce11eb1c5aa7b0acc71a554fcbd81";
            case SCAVENGER_V -> "16b90f4fa3ec106bfef21f3b75f541a18e4757674f7d58250fa7e74952f087dc";
            case GUARDIAN_PET -> "221025434045bda7025b3e514b316a4b770c6faa4ba9adb4be3809526db77f9d";
            case A_BEGINNERS_GUIDE_TO_PESTHUNTING -> "38a7c9e0c1dab89e14b788d96157ab0152136f8fd7a23d6db3eebe555d448ec3";
            case SEVERED_PINCER -> "7e1e0701f1f9e974765e4f2a7c33a9458c598334401538dc9f21f5ed7e35f79a";
            case ENSNARED_SNAIL -> "64532b7e0fbd976d559fbb218d9f4e53c9354f68b1f363c0482127092724274d";
            case GOLDEN_BOUNTY -> "8e6e10a9ef1b2cdd998557554b209f369ada7bc50b47c81ed14ab2632dab3834";
            case SEVERED_HAND -> "111ee65b56d50cd1ef81a7d33ae2421927592ccd92ad7c2ec4fb1aac29ba422f";
            case VIBRANT_CORAL -> "453dd6c6add09c11ff19f5fe75370b6cec17324945cac05c0e2c4631ff418672";
            case GOLD_BOTTLE_CAP -> "269698fd92fb14827af97e54a3f28f5e2685d7e94bd128c0c27f259df996717c";
            case CHAIN_OF_THE_END_TIMES -> "93d9bc6294ecb85d9c4af66136027faf67413f1dbabbe1b6dfee289083fc2049";
            case FATEFUL_STINGER -> "4e2c26ad88fdd10381284650770d9b59d3688a52868b3bcc7762f88a34303de8";
            case OCTOPUS_TENDRIL -> "4bf58283ffca8bdc59436e83d94e9f134426316b99bab5b9af90dffdb082a056";
            case END_STONE_IDOL -> "c24d46459b70b9ad641506f97b8d93349d8705d0935ebbf16012fad3aca32cb4";
            case TROUBLED_BUBBLE -> "bdfcc530ad4f645a201abc05912f194862611c33739a0826a8bb9ad0d9b98375";
            case NADESHIKO_DYE -> "34e5f13bd7f2b9e5dd8c00c1d245995d2b33ba4f841e3c37da663997ab01251f";
            default -> null;
        };
    }
}
