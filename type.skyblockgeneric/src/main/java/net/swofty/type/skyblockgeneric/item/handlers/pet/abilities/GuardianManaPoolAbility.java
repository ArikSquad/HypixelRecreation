package net.swofty.type.skyblockgeneric.item.handlers.pet.abilities;

import net.swofty.commons.skyblock.item.Rarity;
import net.swofty.type.skyblockgeneric.item.SkyBlockItem;
import net.swofty.type.skyblockgeneric.item.handlers.pet.abstr.PetAbility;
import net.swofty.type.skyblockgeneric.utility.RarityValue;

import java.util.List;

import static net.swofty.commons.StringUtility.decimalify;

public final class GuardianManaPoolAbility implements PetAbility {
    private static final RarityValue<Double> MANA_REGEN_PER_LEVEL = new RarityValue<>(
            0.0, 0.0, 0.0, 0.0, 0.30, 0.30, 0.0);

    @Override
    public String getName() {
        return "Mana Pool";
    }

    @Override
    public List<String> getDescription(SkyBlockItem instance) {
        Rarity rarity = instance.getAttributeHandler().getRarity();
        int level = instance.getAttributeHandler().getPetData().getAsLevel(rarity);
        double regeneration = MANA_REGEN_PER_LEVEL.getForRarity(rarity) * level;
        return List.of("<7>Regenerate <a>" + decimalify(regeneration, 2)
                + "% <stat:mana> <7>extra, doubled near or in water.");
    }
}
