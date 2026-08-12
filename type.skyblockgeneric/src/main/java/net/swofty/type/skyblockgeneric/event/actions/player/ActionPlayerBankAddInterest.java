package net.swofty.type.skyblockgeneric.event.actions.player;

import net.minestom.server.event.player.PlayerSpawnEvent;
import net.swofty.type.generic.event.EventNodes;
import net.swofty.type.generic.event.HypixelEventClass;
import net.swofty.type.generic.event.phase.EventPhase;
import net.swofty.type.generic.event.phase.PhasedEvent;
import net.swofty.type.skyblockgeneric.bank.BankInterestCalculator;
import net.swofty.type.skyblockgeneric.calendar.SkyBlockCalendar;
import net.swofty.type.skyblockgeneric.data.DataMutexService;
import net.swofty.type.skyblockgeneric.data.SkyBlockDataHandler;
import net.swofty.type.skyblockgeneric.data.datapoints.DatapointBankData;
import net.swofty.type.skyblockgeneric.user.SkyBlockPlayer;

public class ActionPlayerBankAddInterest implements HypixelEventClass {


    @PhasedEvent(node = EventNodes.PLAYER, requireDataLoaded = true, phase = EventPhase.GAMEPLAY)
    public void run(PlayerSpawnEvent event) {
        if (event.isFirstSpawn()) return;
        SkyBlockPlayer player = (SkyBlockPlayer) event.getPlayer();

        if (!player.isCoop()) {
            DatapointBankData datapoint = player.getSkyblockDataHandler()
                    .get(SkyBlockDataHandler.Data.BANK_DATA, DatapointBankData.class);
            DatapointBankData.BankData bankData = datapoint.getValue();

            Double awarded = applyInterestIfDue(bankData);
            if (awarded == null) return;

            datapoint.setValue(bankData);
            announce(player, awarded);
            return;
        }

        double[] awarded = new double[1];
        DataMutexService.Outcome outcome = DataMutexService.withSynchronizedData(
                player.getSkyblockDataHandler().getCurrentProfileId(),
                SkyBlockDataHandler.Data.BANK_DATA,
                (DatapointBankData.BankData latestBankData) -> {
                    Double given = applyInterestIfDue(latestBankData);
                    if (given == null) return null;

                    awarded[0] = given;
                    return latestBankData;
                });

        if (outcome == DataMutexService.Outcome.APPLIED) announce(player, awarded[0]);
    }

    private Double applyInterestIfDue(DatapointBankData.BankData bankData) {
        long difference = SkyBlockCalendar.getDifferenceInHours(bankData.getLastClaimedInterest());
        if (difference < SkyBlockCalendar.INTEREST_INTERVAL) return null;

        int times = (int) Math.min(difference / SkyBlockCalendar.INTEREST_INTERVAL, 2);
        double balance = bankData.getAmount();
        double totalToGive = 0;
        for (int i = 0; i < times; i++) {
            double interest = BankInterestCalculator.calculate(balance, bankData.getAccountTier(), bankData.getMuseumMilestone());
            double awarded = Math.clamp(interest, 0, bankData.getBalanceLimit() - balance);
            balance += awarded;
            totalToGive += awarded;
        }

        bankData.setLastClaimedInterest(SkyBlockCalendar.getElapsed());
        bankData.setLastInterest(totalToGive);
        bankData.setAmount(balance);

        if (totalToGive > 0) {
            bankData.addTransaction(new DatapointBankData.Transaction(
                    System.currentTimeMillis(),
                    totalToGive,
                    "<c>Bank Interest"
            ));
        }

        return totalToGive;
    }

    private void announce(SkyBlockPlayer player, double totalToGive) {
        if (totalToGive == 0) return;

        player.sendMessage("<b>------------------------------------------------");
        player.sendMessage("<a>You have just received <6>{:,} coins<a> as bank interest!", totalToGive);
        player.sendMessage("<b>------------------------------------------------");
    }
}
