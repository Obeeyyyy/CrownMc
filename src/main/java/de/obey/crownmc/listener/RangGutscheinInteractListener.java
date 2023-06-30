package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       15.11.2022 / 20:31

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.Rang;
import de.obey.crownmc.handler.RangHandler;
import de.obey.crownmc.handler.ScoreboardHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
@NonNull
public final class RangGutscheinInteractListener implements Listener {

    private final MessageUtil messageUtil;
    private final RangHandler rangHandler;
    private final ScoreboardHandler scoreboardHandler;

    @EventHandler
    public void on(final PlayerInteractEvent event) {

        final Player player = event.getPlayer();

        if (InventoryUtil.isItemInHandWithDisplayname(player, "§a§lRANGGUTSCHEIN")) {

            event.setCancelled(true);

            if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
                return;

            final String rangShowPrefix = player.getItemInHand().getItemMeta().getLore().get(5).split(" ")[2];
            final Rang rang = rangHandler.getRangFromShowPrefix(rangShowPrefix);

            if (rang == null) {
                messageUtil.sendMessage(player, "Dein Rangbuch scheint veraltet§8, §7bitte melde dich bei einem Teammitglied§8.");
                player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
                return;
            }

            final Rang currentRang = rangHandler.getPlayerRang(player);

            if (rang.getId() >= currentRang.getId()) {
                messageUtil.sendMessage(player, "Du hast diesen oder einen besseren Rang bereits§8.");
                player.playSound(player.getLocation(), Sound.EXPLODE, 0.2f, 1);
                return;
            }

            InventoryUtil.removeItemInHand(player, 1);

            rangHandler.setPlayerRang(player, rang.getName(), Bukkit.getConsoleSender());
            player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1, 1);
            messageUtil.sendMessage(player, "Du hast den Rang §8'§e§o" + rangShowPrefix + "§8'§7 freigeschaltet§8.");
            scoreboardHandler.updateEverythingForEveryone();

            return;
        }

        if(InventoryUtil.isItemInHandWithDisplayname(player, "§6§lCrown §7Rang §8(§f1 Woche§8)")) {
            event.setCancelled(true);

            if(player.hasPermission("group.crown")) {
                messageUtil.sendMessage(player, "Du hast den §6§lCrown §7Rang bereits§8.");
                player.playSound(player.getLocation(), Sound.EXPLODE, 1, 1);
                return;
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + player.getName() + " group addtemp crown 1w");
            CrownMain.getInstance().getInitializer().getScoreboardHandler().updateScoreboard(player);
            InventoryUtil.removeItemInHand(player, 1);

            messageUtil.sendMessage(player, "Du hast den Crown Rang für eine Woche erhalten§8.");
            player.playSound(player.getLocation(), Sound.ENDERMAN_DEATH,1 , 0.1f);

            return;
        }
    }

}
