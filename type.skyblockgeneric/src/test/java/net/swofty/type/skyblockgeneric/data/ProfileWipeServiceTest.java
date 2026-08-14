package net.swofty.type.skyblockgeneric.data;

import net.swofty.commons.skyblock.item.attribute.ItemAttribute;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfileWipeServiceTest {
    @BeforeAll
    static void registerItemAttributes() {
        ItemAttribute.registerItemAttributes();
    }

    @Test
    void clearsEveryProfileBackedDatapoint() {
        List<SkyBlockDataHandler.Data> cleared = ProfileWipeService.profileFieldsToClear();
        List<SkyBlockDataHandler.Data> expected = Arrays.stream(SkyBlockDataHandler.Data.values())
                .filter(data -> data.coopField() == null)
                .toList();

        assertEquals(expected, cleared);
        assertTrue(cleared.contains(SkyBlockDataHandler.Data.COINS));
        assertTrue(cleared.contains(SkyBlockDataHandler.Data.INVENTORY));
        cleared.forEach(data -> assertNotNull(data.profileField()));
    }

    @Test
    void leavesCoopSharedDatapointsToTheCoopItself() {
        List<SkyBlockDataHandler.Data> coopBacked = Arrays.stream(SkyBlockDataHandler.Data.values())
                .filter(data -> data.coopField() != null)
                .toList();

        assertFalse(coopBacked.isEmpty());
        assertTrue(coopBacked.contains(SkyBlockDataHandler.Data.BANK_DATA));
        assertTrue(coopBacked.contains(SkyBlockDataHandler.Data.ISLAND_UUID));
        coopBacked.forEach(data -> assertFalse(ProfileWipeService.profileFieldsToClear().contains(data)));
    }

    @Test
    void refusesASecondWipeWhileOneIsRunning() {
        UUID playerUuid = UUID.randomUUID();

        assertTrue(ProfileWipeService.begin(playerUuid));
        assertTrue(ProfileWipeService.isInProgress(playerUuid));
        assertFalse(ProfileWipeService.begin(playerUuid));

        ProfileWipeService.finish(playerUuid);
        assertFalse(ProfileWipeService.isInProgress(playerUuid));
        assertTrue(ProfileWipeService.begin(playerUuid));
        ProfileWipeService.finish(playerUuid);
    }
}
