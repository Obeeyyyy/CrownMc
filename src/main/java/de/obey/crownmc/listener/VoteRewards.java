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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public final class VoteRewards implements Listener {

    @NonNull
    private final Initializer initializer;

    @EventHandler
    public void on(final VotifierEvent event) {
        final Vote vote = event.getVote();

        if (!initializer.getMessageUtil().hasPlayedBefore(Bukkit.getConsoleSender(), vote.getUsername()))
            return;

        final OfflinePlayer target = Bukkit.getOfflinePlayer(vote.getUsername());

        initializer.getUserHandler().getUser(target.getUniqueId()).thenAcceptAsync(user -> {
            user.addInt(DataType.VOTES, 1);
            initializer.getServerConfig().vote(vote.getUsername());
        });

        initializer.getScoreboardHandler().updateEverythingForEveryone();

        initializer.getDailyPotHandler().addMoney(20000);
    }

}
