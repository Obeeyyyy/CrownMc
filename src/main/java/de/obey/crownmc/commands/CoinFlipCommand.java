package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       05.12.2022 / 11:20

*/

import de.obey.crownmc.handler.CoinflipHandler;
import de.obey.crownmc.objects.gambling.CoinFlip;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
public final class CoinFlipCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final CoinflipHandler coinflipHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 0) {
            coinflipHandler.updateInventory();
            player.openInventory(coinflipHandler.getInventory());
            player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1, 1);
            return false;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("delete")) {

                final CoinFlip coinFlip = coinflipHandler.getCoinflipFromPlayer(player);

                if (coinFlip == null) {
                    messageUtil.sendMessage(player, "Du bist ein keinem Coinflip§8.");
                    return false;
                }

                if (coinFlip.getState() != 0) {
                    messageUtil.sendMessage(player, "Der Coinflip läuft bereits§8.");
                    return false;
                }

                coinflipHandler.removeCoinflip(coinFlip);
                messageUtil.sendMessage(player, "Du hast deinen Coinflip gelöscht§8.");

                return false;
            }

            if(args[0].equalsIgnoreCase("list")) {
                if(!PermissionUtil.hasPermission(sender, "admin", true))
                    return false;

                messageUtil.sendMessage(player, "Es existieren " + coinflipHandler.getCoinflips().size());

                return false;
            }

            long amount = 0L;

            try {
                amount = Long.parseLong(args[0]);

                if (amount < 100) {
                    messageUtil.sendMessage(sender, "Der Wetteinsatz muss mind. 100$ hoch sein§8.");
                    return false;
                }

                coinflipHandler.createCoinFlip(player, amount);

            } catch (final NumberFormatException exception) {

                amount = MathUtil.getLongFromStringwithSuffix(args[0]);

                if (amount <= 0) {
                    messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an, oder benutze folgende Abkürzungen§8. (§7k, m, mrd, b, brd, t§8)");
                    return false;
                }

                if (amount < 100) {
                    messageUtil.sendMessage(player, "Der Wetteinsatz muss mind. 100$ hoch sein§8.");
                    return false;
                }

                coinflipHandler.createCoinFlip(player, amount);
            }

            return false;
        }

        if (args.length == 2) {
            if(args[0].equalsIgnoreCase("remove")) {
                if(!PermissionUtil.hasPermission(sender, "admin", true))
                    return false;

                final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                if(target != null) {
                    coinflipHandler.getCoinflips().remove(target.getUniqueId());
                    messageUtil.sendMessage(player, target.getName() + " wurde gecleart§8.");
                }
            }
        }

        return false;
    }

    @EventHandler
    public void onGlobal(final InventoryClickEvent event) {
        if (!InventoryUtil.isInventoryTitle(event.getInventory(), "§6§lCoinFlips"))
            return;

        event.setCancelled(true);

        if (!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "§6§lCoinFlips"))
            return;

        final Player player = (Player) event.getWhoClicked();

        if (event.getSlot() == 9) {
            player.closeInventory();
            coinflipHandler.getCreatingCoinflip().add(player);
            messageUtil.sendMessage(player, "Du §a§oerstellst§7 einen Coinflip§8, §7schreibe den §a§ogewünschten Betrag§7 in den Chat§8.");
            messageUtil.sendMessage(player, "Schreibe §c§ocancel§7 um den Vorgang abzubrechen§8.");
            player.playSound(player.getLocation(), Sound.STEP_LADDER, 0.5f, 1);
            return;
        }

        if (event.getSlot() == 36) {
            if(coinflipHandler.getCoinflips().containsKey(player.getUniqueId())) {
                messageUtil.sendMessage(player, "Du hast bereits einen Coinflip§8.");
                return;
            }

            player.closeInventory();
            coinflipHandler.getCreatingServerCoinflip().add(player);
            messageUtil.sendMessage(player, "Schreibe den §a§ogewünschten Betrag§7 in den Chat§8,§7 der Coinflip wird direkt gestartet§8.");
            messageUtil.sendMessage(player, "Schreibe §c§ocancel§7 um den Vorgang abzubrechen§8.");
            player.playSound(player.getLocation(), Sound.STEP_LADDER, 0.5f, 1);
            return;
        }

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.SKULL_ITEM)
            return;

        final CoinFlip coinFlip = coinflipHandler.getCoinflipFromPressedItem(event.getCurrentItem());

        if (coinFlip == null)
            return;

        if (coinFlip.getPlayer() == player) {

            if (coinFlip.getState() != 0) {
                player.openInventory(coinFlip.getInventory());
                return;
            }

            coinflipHandler.removeCoinflip(coinFlip);
            messageUtil.sendMessage(player, "Dein CoinFlip wurde geschlossen§8.");

            return;
        }

        coinflipHandler.joinCoinflip((Player) event.getWhoClicked(), coinFlip);
    }

    @EventHandler
    public void onRunning(final InventoryClickEvent event) {
        if (!InventoryUtil.startsWithInventoryTitle(event.getInventory(), "§6§lCF "))
            return;

        event.setCancelled(true);
    }
}
