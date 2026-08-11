package net.swofty.type.skyblockgeneric.item.handlers.pet.abilities;

import net.swofty.commons.skyblock.item.Rarity;
import net.swofty.type.skyblockgeneric.item.SkyBlockItem;
import net.swofty.type.skyblockgeneric.item.handlers.pet.abstr.PetAbility;
import net.swofty.type.skyblockgeneric.utility.RarityValue;

import java.util.List;

import static net.swofty.commons.StringUtility.decimalify;

public final class GuardianLuckySevenAbility implements PetAbility {
    private static final RarityValue<Double> CHANCE_PER_LEVEL = new RarityValue<>(
            0.0, 0.0, 0.0, 0.0, 0.0, 0.07, 0.0);

    @Override
    public String getName() {
        return "Lucky Seven";
    }

    @Override
    public List<String> getDescription(SkyBlockItem instance) {
        Rarity rarity = instance.getAttributeHandler().getRarity();
        int level = instance.getAttributeHandler().getPetData().getAsLevel(rarity);
        return List.of("<7>Gain <a>" + decimalify(CHANCE_PER_LEVEL.getForRarity(rarity) * level, 2)
                + "% <7>chance to find ultra-rare books in Superpairs.");
    }
}
