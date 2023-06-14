package de.obey.slayer.listener;
/*

    Author - Obey -> SkySlayer-v4
       15.11.2022 / 20:31

*/

import de.obey.slayer.backend.Rang;
import de.obey.slayer.handler.RangHandler;
import de.obey.slayer.handler.ScoreboardHandler;
import de.obey.slayer.util.InventoryUtil;
import de.obey.slayer.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

        if (!InventoryUtil.isItemInHandWithDisplayname(player, "§a§lRANGGUTSCHEIN"))
            return;

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
    }

}
