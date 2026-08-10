package net.swofty.type.skyblockgeneric.item.components;

import lombok.Getter;
import net.minestom.server.item.Material;
import net.swofty.type.skyblockgeneric.item.SkyBlockItemComponent;
import net.swofty.type.skyblockgeneric.item.handlers.anvilcombine.AnvilCombineHandler;
import net.swofty.type.skyblockgeneric.item.handlers.anvilcombine.AnvilCombineRegistry;

@Getter
public final class ArmorDyeComponent extends SkyBlockItemComponent {
    private final String fromColor;
    private final String toColor;
    private final long animationPeriod;

    public ArmorDyeComponent(String itemId, String fromColor, String toColor, long animationPeriod) {
        this.fromColor = normalize(fromColor);
        this.toColor = toColor == null ? null : normalize(toColor);
        this.animationPeriod = animationPeriod;
        AnvilCombineRegistry.register(itemId, new AnvilCombineHandler(
                (armor, dye) -> armor.getAttributeHandler().setDyeColor(serializedColor()),
                (player, armor, dye) -> isVanillaArmor(armor.getMaterial())
                        && !serializedColor().equals(armor.getAttributeHandler().getDyeColor()),
                (armor, dye, player) -> 0,
                (player, armor, dye) -> {
                    if (player.getBits() < 100) {
                        player.sendMessage("<c>You need at least <b>100 Bits <c>to apply this dye!");
                        return false;
                    }
                    player.removeBits(100);
                    player.sendMessage("<a>Dye applied! <7>(<b>-100 Bits<7>)");
                    return true;
                }
        ));
        addInheritedComponent(new AnvilCombinableComponent(itemId));
        addInheritedComponent(new ItemModelComponent("hypixel_skyblock:item/dyes/"
                + itemId.toLowerCase()));
    }

    private String serializedColor() {
        if (toColor == null) return fromColor;
        return "animated:" + fromColor + ":" + toColor + ":" + animationPeriod;
    }

    private static boolean isVanillaArmor(Material material) {
        String name = material.name();
        return name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE")
                || name.endsWith("_LEGGINGS") || name.endsWith("_BOOTS");
    }

    private static String normalize(String color) {
        return color.startsWith("#") ? color.toUpperCase() : "#" + color.toUpperCase();
    }
}
