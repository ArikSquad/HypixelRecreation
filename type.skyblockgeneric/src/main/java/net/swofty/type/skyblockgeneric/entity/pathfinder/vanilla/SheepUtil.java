package net.swofty.type.skyblockgeneric.entity.pathfinder.vanilla;

import net.minestom.server.color.DyeColor;
import net.minestom.server.item.Material;
import net.minestom.server.registry.*;
import net.minestom.server.world.biome.Biome;
import net.minestom.server.world.biome.BiomeTags;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SheepUtil {
    private static final Registry<Biome> BIOME_REGISTRY =
            Registries.vanilla().biome();

    private static final Map<DyeColor, Material> WOOL_BY_COLOR = Map.ofEntries(
            Map.entry(DyeColor.WHITE, Material.WHITE_WOOL), Map.entry(DyeColor.ORANGE, Material.ORANGE_WOOL),
            Map.entry(DyeColor.MAGENTA, Material.MAGENTA_WOOL), Map.entry(DyeColor.LIGHT_BLUE, Material.LIGHT_BLUE_WOOL),
            Map.entry(DyeColor.YELLOW, Material.YELLOW_WOOL), Map.entry(DyeColor.LIME, Material.LIME_WOOL),
            Map.entry(DyeColor.PINK, Material.PINK_WOOL), Map.entry(DyeColor.GRAY, Material.GRAY_WOOL),
            Map.entry(DyeColor.LIGHT_GRAY, Material.LIGHT_GRAY_WOOL), Map.entry(DyeColor.CYAN, Material.CYAN_WOOL),
            Map.entry(DyeColor.PURPLE, Material.PURPLE_WOOL), Map.entry(DyeColor.BLUE, Material.BLUE_WOOL),
            Map.entry(DyeColor.BROWN, Material.BROWN_WOOL), Map.entry(DyeColor.GREEN, Material.GREEN_WOOL),
            Map.entry(DyeColor.RED, Material.RED_WOOL), Map.entry(DyeColor.BLACK, Material.BLACK_WOOL));

    private static final Set<RegistryKey<Biome>> WARM_BIOMES = biomeSet(
            Set.of(
                    Biome.DESERT,
                    Biome.WARM_OCEAN,
                    Biome.MANGROVE_SWAMP,
                    Biome.DEEP_LUKEWARM_OCEAN,
                    Biome.LUKEWARM_OCEAN
            ),
            BiomeTags.IS_JUNGLE,
            BiomeTags.IS_SAVANNA,
            BiomeTags.IS_NETHER,
            BiomeTags.IS_BADLANDS
    );

    private static final Set<RegistryKey<Biome>> COLD_BIOMES = biomeSet(
            Set.of(
                    Biome.SNOWY_PLAINS,
                    Biome.ICE_SPIKES,
                    Biome.FROZEN_PEAKS,
                    Biome.JAGGED_PEAKS,
                    Biome.SNOWY_SLOPES,
                    Biome.FROZEN_OCEAN,
                    Biome.DEEP_FROZEN_OCEAN,
                    Biome.GROVE,
                    Biome.DEEP_DARK,
                    Biome.FROZEN_RIVER,
                    Biome.SNOWY_BEACH,
                    Biome.COLD_OCEAN,
                    Biome.DEEP_COLD_OCEAN,
                    Biome.WINDSWEPT_FOREST,
                    Biome.WINDSWEPT_GRAVELLY_HILLS,
                    Biome.WINDSWEPT_HILLS,
                    Biome.STONY_PEAKS
            ),
            BiomeTags.IS_END,
            BiomeTags.IS_TAIGA
    );

    @SafeVarargs
    private static Set<RegistryKey<Biome>> biomeSet(
            final Set<RegistryKey<Biome>> explicitBiomes,
            final TagKey<Biome>... tagKeys
    ) {
        Set<RegistryKey<Biome>> result = new HashSet<>(explicitBiomes);

        for (TagKey<Biome> tagKey : tagKeys) {
            RegistryTag<Biome> tag = Objects.requireNonNull(
                    BIOME_REGISTRY.getTag(tagKey),
                    () -> "Missing vanilla biome tag: " + tagKey.key()
            );

            tag.forEach(result::add);
        }

        return Set.copyOf(result);
    }

    public static boolean isWarm(final RegistryKey<Biome> biome) {
        return WARM_BIOMES.contains(biome);
    }

    public static boolean isCold(final RegistryKey<Biome> biome) {
        return COLD_BIOMES.contains(biome);
    }

    @NotNull
    public static DyeColor randomColor(final @NotNull RegistryKey<Biome> biome, final @NotNull Random random) {
        int r = random.nextInt(100);
        if (isWarm(biome)) {
            if (r < 5) return DyeColor.GRAY;
            if (r < 10) return DyeColor.LIGHT_GRAY;
            if (r < 15) return DyeColor.WHITE;
            if (r < 18) return DyeColor.BLACK;
            return commonOrPink(DyeColor.BROWN, random);
        } else if (isCold(biome)) {
            if (r < 5) return DyeColor.LIGHT_GRAY;
            if (r < 10) return DyeColor.GRAY;
            if (r < 15) return DyeColor.WHITE;
            if (r < 18) return DyeColor.BROWN;
            return commonOrPink(DyeColor.BLACK, random);
        } else {
            if (r < 5) return DyeColor.BLACK;
            if (r < 10) return DyeColor.GRAY;
            if (r < 15) return DyeColor.LIGHT_GRAY;
            if (r < 18) return DyeColor.BROWN;
            return commonOrPink(DyeColor.WHITE, random);
        }
    }

    private static DyeColor commonOrPink(final @NotNull DyeColor common, final @NotNull Random random) {
        return random.nextInt(500) == 0 ? DyeColor.PINK : common;
    }
}
