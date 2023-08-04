package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       27.10.2022 / 23:09

*/

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import de.obey.crownmc.Initializer;
import de.obey.crownmc.backend.enums.DataType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public final class VoteListener implements Listener {

    @NonNull
    private final Initializer initializer;

    @EventHandler
    public void on(final VotifierEvent event) {
        final Vote vote = event.getVote();

        if (!initializer.getMessageUtil().hasPlayedBefore(Bukkit.getConsoleSender(), vote.getUsername()))
            return;

        final OfflinePlayer target = Bukkit.getOfflinePlayer(vote.getUsername());

        initializer.getScoreboardHandler().updateEverythingForEveryone();
        initializer.getDailyPotHandler().addMoney(500);

        initializer.getMessageUtil().broadcast("§e§o" + target.getName() + "§7 hat für uns gevotet §8!§7 Vote auch du für eine Belohnung§8.");
        initializer.getMessageUtil().broadcast("§a+§e§o500§6§l$ §7in den §9§lDailyPot§8.");

        initializer.getUserHandler().getUser(target.getUniqueId()).thenAcceptAsync(user -> {
            user.addLong(DataType.VOTES, 1);

            if(System.currentTimeMillis() - user.getLong(DataType.LASTVOTE) > 86400000)
                user.setLong(DataType.VOTESTREAK, 0);

            user.addLong(DataType.VOTESTREAK, 1);
            user.setLong(DataType.LASTVOTE, System.currentTimeMillis());

            if(target.isOnline()) {
                initializer.getServerConfig().vote(target.getPlayer());
                initializer.getMessageUtil().broadcast("§7Voteparty§8: §a" + initializer.getServerConfig().getVotes()+ "§8/§2" + initializer.getServerConfig().getVoteparty());
                initializer.getMessageUtil().sendMessage(target.getPlayer(), "Votestreak§8: §a§o" + user.getLong(DataType.VOTESTREAK) + "§7 Votes§8.");
                target.getPlayer().playSound(target.getPlayer().getLocation(), Sound.LEVEL_UP, 0.5f, 1);
            }
        });

        if(initializer.getServerConfig().getVotes() >= initializer.getServerConfig().getVoteparty()) {
            initializer.getServerConfig().setVotes(0);
            initializer.getVotePartyHandler().startVoteParty();
        }
    }

}
