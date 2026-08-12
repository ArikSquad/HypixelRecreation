package net.swofty.type.skyblockgeneric.commands;

import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.TaskSchedule;
import net.swofty.commons.data.SwoftyData;
import net.swofty.commons.skyblock.SkyBlockPlayerProfiles;
import net.swofty.type.generic.command.CommandParameters;
import net.swofty.type.generic.command.HypixelCommand;
import net.swofty.type.generic.data.mongodb.ProfilesDatabase;
import net.swofty.type.generic.data.mongodb.UserDatabase;
import net.swofty.type.generic.user.categories.Rank;
import net.swofty.type.skyblockgeneric.data.CoopLinks;
import net.swofty.type.skyblockgeneric.data.monogdb.CoopDatabase;
import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;

import java.util.UUID;

@CommandParameters(labels = "cooperativeleave",
        description = "Leaves the current coop",
        usage = "/coopleave",
        permission = Rank.DEFAULT,
        allowsConsole = false)
public class CoopLeaveCommand extends HypixelCommand {
    @Override
    public void registerUsage(MinestomCommand command) {
        command.addSyntax((sender, context) -> {
            if (!permissionCheck(sender)) return;

            SkyBlockPlayer player = (SkyBlockPlayer) sender;

            if (!player.isCoop()) {
                player.sendMessage("<b>[Co-op] <c>You are not on a coop profile!");
                return;
            }

            if (player.getProfiles().getProfiles().size() == 1) {
                player.sendMessage("<b>[Co-op] <c>You cannot leave your last profile!");
                player.sendMessage("<b>[Co-op] <e>Make another profile before deleting this one.");
                return;
            }

            CoopDatabase.Coop coop = CoopDatabase.getFromMember(player.getUuid());
            if (coop == null) {
                player.sendMessage("<b>[Co-op] <c>You are not part of a co-op!");
                return;
            }

            UUID coopId = coop.coopUUID();
            UUID profileId = player.getProfiles().getCurrentlySelected();

            player.kick("<c>You must reconnect for this change to take effect");

            MinecraftServer.getSchedulerManager().scheduleTask(() -> {
                CoopDatabase.Coop remaining = CoopDatabase.update(coopId, latest -> {
                    latest.members().remove(player.getUuid());
                    latest.removeInvite(player.getUuid());
                    latest.memberProfiles().remove(profileId);
                });

                SwoftyData.profile().unlink(profileId, CoopLinks.COOP);
                if (remaining == null || (remaining.members().isEmpty() && remaining.memberProfiles().isEmpty())) {
                    SwoftyData.profile().unloadLink(CoopLinks.COOP, coopId);
                }

                ProfilesDatabase.deleteDocument(profileId.toString());

                SkyBlockPlayerProfiles profiles = player.getProfiles();
                profiles.removeProfile(profileId);
                profiles.setCurrentlySelected(profiles.getProfiles().getFirst());
                new UserDatabase(player.getUuid()).saveProfiles(profiles);
            }, TaskSchedule.tick(4), TaskSchedule.stop());
        });
    }
}
