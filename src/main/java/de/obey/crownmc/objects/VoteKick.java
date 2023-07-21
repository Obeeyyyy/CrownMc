package de.obey.crownmc.objects;

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.UUID;

public class VoteKick {

    private final MessageUtil messageUtil;
    private final UUID targetUUid;
    private final String staredBy;
    private final BukkitTask runnable;

    private int yesVotes, noVotes;

    private final ArrayList<UUID> voted = new ArrayList<>();

    public VoteKick(final UUID target, final String startedBy) {
        messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();
        this.targetUUid = target;
        this.staredBy = startedBy;

        runnable = new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                ticks++;

                if(ticks >= 20){
                    end();
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(CrownMain.getInstance(), 20, 20);
    }

    public void end() {
        final boolean kick = noVotes < yesVotes;
        final OfflinePlayer target = Bukkit.getOfflinePlayer(targetUUid);

        messageUtil.broadcast("§8§m------------------------");
        messageUtil.broadcast("");
        messageUtil.broadcast("§7  Die Abstimmung ist beendet§8.");
        messageUtil.broadcast("");
        messageUtil.broadcast("§7Insgesamt haben §f§o" + (yesVotes + noVotes) + "§7 abgestimmt§8.");
        messageUtil.broadcast("§a§o" + yesVotes + "§7 Spieler waren dafür§8.");
        messageUtil.broadcast("§c§o" + noVotes + "§7 Spieler waren dagegen§8.");
        messageUtil.broadcast("");
        if(kick) {
            messageUtil.broadcast("§f§o" + target.getName() + "§7 wurde vom Server gekickt§8.");
        } else {
            messageUtil.broadcast("§f§o" + target.getName() + "§7 wurde nicht vom Server gekickt§8.");
        }
        messageUtil.broadcast("");
        messageUtil.broadcast("§8§m------------------------");

        if(kick) {
            if(!target.isOnline())
                return;

            final Player onlineTarget = Bukkit.getPlayer(targetUUid);

            onlineTarget.kickPlayer("\n§6§lCrownMc.de\n\n" +
                    "§7Du wurdest vom Server gekickt§8.\n\n" +
                    "§7Grund§8: §f§oVotekick von " + staredBy);
        }

    }

    public boolean vote(final Player player, final boolean state) {
        if(voted.contains(player.getUniqueId()))
            return false;

        if(state) {
            yesVotes++;
        } else {
            noVotes++;
        }

        voted.add(player.getUniqueId());

        return true;
    }

}
