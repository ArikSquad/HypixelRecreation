package net.swofty.type.skyblockgeneric.data;

import net.swofty.commons.data.SwoftyData;
import net.swofty.commons.skyblock.CoopLinks;
import net.swofty.commons.skyblock.SkyBlockPlayerProfiles;
import net.swofty.type.generic.data.mongodb.ProfilesDatabase;
import net.swofty.type.generic.data.mongodb.UserDatabase;
import net.swofty.type.skyblockgeneric.data.datapoints.DatapointUUID;
import net.swofty.type.skyblockgeneric.data.monogdb.CoopDatabase;
import net.swofty.type.skyblockgeneric.data.monogdb.IslandDatabase;
import net.swofty.type.skyblockgeneric.user.island.SkyBlockIsland;
import org.bson.Document;
import org.tinylog.Logger;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ProfileWipeService {
    private static final Set<UUID> inProgress = ConcurrentHashMap.newKeySet();

    private ProfileWipeService() {}

    public static boolean begin(UUID playerUuid) {
        return inProgress.add(playerUuid);
    }

    public static void finish(UUID playerUuid) {
        inProgress.remove(playerUuid);
    }

    public static boolean isInProgress(UUID playerUuid) {
        return inProgress.contains(playerUuid);
    }

    public static List<SkyBlockDataHandler.Data> profileFieldsToClear() {
        return Arrays.stream(SkyBlockDataHandler.Data.values())
                .filter(data -> data.coopField() == null)
                .toList();
    }

    public static List<UUID> wipe(UUID playerUuid) {
        UserDatabase userDatabase = new UserDatabase(playerUuid);
        SkyBlockPlayerProfiles profiles = userDatabase.getProfiles();
        List<UUID> owned = List.copyOf(profiles.getProfiles());

        for (UUID profileId : owned) {
            try {
                wipeProfile(playerUuid, profileId);
            } catch (Exception e) {
                Logger.error(e, "Failed to wipe profile {} of user {}", profileId, playerUuid);
            }
            profiles.removeProfile(profileId);
        }

        userDatabase.deleteSelf();
        profiles.setCurrentlySelected(null);

        try {
            SwoftyData.account().unload(playerUuid);
        } catch (Exception e) {
            Logger.error(e, "Failed to release the account container of wiped user {}", playerUuid);
        }
        return owned;
    }

    private static void wipeProfile(UUID playerUuid, UUID profileId) {
        Set<UUID> islandIds = islandIdsOf(playerUuid, profileId);
        CoopDatabase.Coop coop = CoopDatabase.getFromMemberProfile(profileId);
        UUID coopId = coop == null ? profileId : coop.coopUUID();
        boolean coopEmptied = true;

        if (coop != null) {
            CoopDatabase.Coop remaining = CoopDatabase.update(coopId, latest -> {
                latest.members().remove(playerUuid);
                latest.removeInvite(playerUuid);
                latest.memberProfiles().remove(profileId);
            });
            coopEmptied = remaining == null
                    || (remaining.members().isEmpty() && remaining.memberProfiles().isEmpty());
        }

        SwoftyData.profile().unlink(profileId, CoopLinks.COOP);
        if (coopEmptied) {
            SwoftyData.profile().deleteLink(CoopLinks.COOP, coopId);
            for (UUID islandId : islandIds) {
                SkyBlockIsland.discard(islandId);
                new IslandDatabase(islandId.toString()).remove(islandId.toString());
            }
        }

        SwoftyData.profile().transaction(profileId, transaction -> {
            for (SkyBlockDataHandler.Data data : profileFieldsToClear()) {
                transaction.set(data.profileField(), null);
            }
            transaction.set(ProfilesDatabase.DOCUMENT, null);
            transaction.set(CoopLinks.COOP_KEY, null);
        });

        try {
            SwoftyData.profile().unload(profileId);
        } catch (Exception e) {
            Logger.error(e, "Failed to release the container of wiped profile {}", profileId);
        }
    }

    private static Set<UUID> islandIdsOf(UUID playerUuid, UUID profileId) {
        Set<UUID> islandIds = new LinkedHashSet<>();
        islandIds.add(profileId);

        SkyBlockDataHandler handler = SkyBlockDataHandler.initUserWithDefaultData(playerUuid, profileId);
        try {
            addIslandId(islandIds, SkyBlockDataHandler.Data.ISLAND_UUID.readData(handler));
        } catch (Exception e) {
            Logger.error(e, "Failed to read the linked island of profile {}", profileId);
        }

        try {
            Document document = ProfilesDatabase.fetchDocument(profileId);
            if (document != null) {
                addIslandId(islandIds, document.getString(SkyBlockDataHandler.Data.ISLAND_UUID.getKey()));
            }
        } catch (Exception e) {
            Logger.error(e, "Failed to read the stored document of profile {}", profileId);
        }
        return islandIds;
    }

    private static void addIslandId(Set<UUID> islandIds, String serialized) {
        if (serialized == null) return;
        try {
            DatapointUUID datapoint = new DatapointUUID(SkyBlockDataHandler.Data.ISLAND_UUID.getKey());
            datapoint.deserializeValue(serialized);
            if (datapoint.getValue() != null) islandIds.add(datapoint.getValue());
        } catch (Exception e) {
            Logger.error(e, "Failed to deserialize the island id {}", serialized);
        }
    }
}
