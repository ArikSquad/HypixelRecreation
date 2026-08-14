package net.swofty.type.skyblockgeneric.commands;

import net.swofty.commons.ServerType;
import net.swofty.proxyapi.ProxyPlayer;
import net.swofty.type.generic.HypixelConst;
import net.swofty.type.generic.command.CommandParameters;
import net.swofty.type.generic.command.HypixelCommand;
import net.swofty.type.generic.data.domain.PlayerDataService;
import net.swofty.type.generic.user.HypixelPlayer;
import net.swofty.type.generic.user.categories.Rank;
import net.swofty.type.generic.utility.ScheduleUtility;
import net.swofty.type.skyblockgeneric.data.ProfileWipeService;
import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;
import org.tinylog.Logger;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@CommandParameters(description = "Allows the player to wipe themselves",
        usage = "/wipeme",
        permission = Rank.STAFF,
        labels = "deletemyprofiles wipeme",
        allowsConsole = false)
public class WipeMeCommand extends HypixelCommand {
    private static final long TRANSFER_TIMEOUT_SECONDS = 20;

    @Override
    public void registerUsage(MinestomCommand command) {
        command.addSyntax((sender, _) -> {
            if (!permissionCheck(sender)) return;

            HypixelPlayer sendingPlayer = (HypixelPlayer) sender;
            if (!(sendingPlayer instanceof SkyBlockPlayer player)) {
                sendingPlayer.sendMessage("<c>You can only wipe yourself from a SkyBlock server.");
                return;
            }

            UUID playerUuid = player.getUuid();
            if (!ProfileWipeService.begin(playerUuid)) {
                player.sendMessage("<c>You are already being wiped.");
                return;
            }

            ServerType type = HypixelConst.getTypeLoader().getType();
            player.sendMessage("<c>Wiping every profile you own, you will be moved to the lobby.");
            Thread.startVirtualThread(() -> wipe(player, playerUuid, type));
        });
    }

    private void wipe(SkyBlockPlayer player, UUID playerUuid, ServerType type) {
        try {
            PlayerDataService.discardAll(type, playerUuid);
            List<UUID> wiped = ProfileWipeService.wipe(playerUuid);
            Logger.info("Wiped {} profile(s) belonging to {}", wiped.size(), playerUuid);

            new ProxyPlayer(playerUuid).transferWithoutDataTo(ServerType.PROTOTYPE_LOBBY)
                    .orTimeout(TRANSFER_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .exceptionally(error -> {
                        Logger.error(error, "Failed to move wiped user {} to the prototype lobby", playerUuid);
                        kick(player);
                        return null;
                    });
        } catch (Exception e) {
            Logger.error(e, "Failed to wipe user {}", playerUuid);
            kick(player);
        } finally {
            ProfileWipeService.finish(playerUuid);
        }
    }

    private void kick(SkyBlockPlayer player) {
        ScheduleUtility.nextTick(() -> {
            if (player.isOnline()) player.kick("<c>You have been wiped");
        });
    }
}
