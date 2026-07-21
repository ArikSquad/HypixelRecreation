package net.swofty.type.skyblockgeneric.entity.mob.mobs.island;

import net.minestom.server.entity.EntityType;
import net.swofty.commons.skyblock.statistics.ItemStatistic;
import net.swofty.commons.skyblock.statistics.ItemStatistics;
import net.swofty.type.generic.gui.inventory.item.GUIMaterial;
import net.swofty.type.skyblockgeneric.entity.mob.BestiaryMob;
import net.swofty.type.skyblockgeneric.entity.mob.MobType;
import net.swofty.type.skyblockgeneric.entity.mob.impl.RegionPopulator;
import net.swofty.type.skyblockgeneric.loottable.OtherLoot;
import net.swofty.type.skyblockgeneric.region.RegionType;
import net.swofty.type.skyblockgeneric.skill.SkillCategories;

import java.util.List;

/**
 * Shared SkyBlock presentation and bestiary data for natural private-island mobs.
 */
public abstract class PrivateIslandMob extends BestiaryMob implements RegionPopulator {
    private final String displayName;
    private final String mobId;
    private final GUIMaterial guiMaterial;
    private final MobType mobType;
    private final ItemStatistics statistics;
    private final OtherLoot otherLoot;

    protected PrivateIslandMob(EntityType type, String displayName, String mobId,
                               GUIMaterial guiMaterial, MobType mobType,
                               double health, double damage, long combatXp, int coins, int xpOrbs) {
        super(type);
        this.displayName = displayName;
        this.mobId = mobId;
        this.guiMaterial = guiMaterial;
        this.mobType = mobType;
        this.statistics = ItemStatistics.builder()
                .withBase(ItemStatistic.HEALTH, health)
                .withBase(ItemStatistic.DAMAGE, damage)
                .build();
        this.otherLoot = new OtherLoot(combatXp, coins, xpOrbs);
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public Integer getLevel() {
        return 1;
    }

    @Override
    public long damageCooldown() {
        return 500;
    }

    @Override
    public ItemStatistics getBaseStatistics() {
        return statistics;
    }

    @Override
    public SkillCategories getSkillCategory() {
        return SkillCategories.COMBAT;
    }

    @Override
    public OtherLoot getOtherLoot() {
        return otherLoot;
    }

    @Override
    public List<MobType> getMobTypes() {
        return List.of(mobType);
    }

    @Override
    public int getMaxBestiaryTier() {
        return 5;
    }

    @Override
    public int getBestiaryBracket() {
        return 1;
    }

    @Override
    public String getMobID() {
        return mobId;
    }

    @Override
    public GUIMaterial getGuiMaterial() {
        return guiMaterial;
    }

    @Override
    public List<Populator> getPopulators() {
        return List.of(new Populator(RegionType.PRIVATE_ISLAND, 20));
    }
}
