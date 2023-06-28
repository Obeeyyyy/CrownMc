package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       26.10.2022 / 00:27

*/

import de.obey.crownmc.handler.RankingHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

@RequiredArgsConstructor
@NonNull
public final class RankingCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final RankingHandler rankingHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 1 && PermissionUtil.hasPermission(player, "*", false)) {
            if (args[0].equalsIgnoreCase("reload")) {
                rankingHandler.updateInventories();
                messageUtil.sendMessage(player, "Das Ranking wird neu geladen.");
                return false;
            }
        }

        try {
            player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.4f, 0.4f);
            player.openInventory(rankingHandler.getInventory("kills"));
        } catch (NullPointerException exception) {
            messageUtil.sendMessage(sender, "Bitte warte einen Moment.");
        }

        return false;
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {
        if (!InventoryUtil.startsWithInventoryTitle(event.getInventory(), "§9§lRanking "))
            return;

        event.setCancelled(true);

        if (!InventoryUtil.startsWithInventoryTitle(event.getClickedInventory(), "§9§lRanking "))
            return;

        final Player player = (Player) event.getWhoClicked();

        final int slot = event.getSlot();

        if (slot == 4)
            openInv(player, "crowns");

        if (slot == 10)
            openInv(player, "kills");

        if (slot == 11)
            openInv(player, "killstreak");

        if (slot == 12)
            openInv(player, "elopoints");

        if (slot == 13)
            openInv(player, "money");

        if (slot == 14)
            openInv(player, "xp");

        if (slot == 15)
            openInv(player, "votes");

        if (slot == 16)
            openInv(player, "playtime");
    }

    private void openInv(final Player player, final String what) {
        player.openInventory(rankingHandler.getInventory(what));
        player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
    }
}
