package net.swofty.type.skyblockgeneric.item.handlers.pet.abilities;

import net.swofty.commons.skyblock.item.Rarity;
import net.swofty.type.skyblockgeneric.item.SkyBlockItem;
import net.swofty.type.skyblockgeneric.item.handlers.pet.abstr.PetAbility;
import net.swofty.type.skyblockgeneric.utility.RarityValue;

import java.util.List;

import static net.swofty.commons.StringUtility.decimalify;

public final class GuardianLazerbeamAbility implements PetAbility {
    private static final RarityValue<Double> DAMAGE_MULTIPLIER = new RarityValue<>(
            0.02, 0.06, 0.10, 0.15, 0.20, 1.20, 0.0);

    @Override
    public String getName() {
        return "Lazerbeam";
    }

    @Override
    public List<String> getDescription(SkyBlockItem instance) {
        Rarity rarity = instance.getAttributeHandler().getRarity();
        int level = instance.getAttributeHandler().getPetData().getAsLevel(rarity);
        double multiplier = DAMAGE_MULTIPLIER.getForRarity(rarity) * level;
        return List.of("<7>Zaps your enemies for <a>" + decimalify(multiplier, 2)
                + "x <stat:intelligence> <7>every 3s.");
    }
}
