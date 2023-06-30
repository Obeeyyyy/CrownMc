package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       30.06.2023 / 00:50

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.JackPotHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.objects.gambling.JackPot;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MathUtil;
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
public final class JackPotCommand implements CommandExecutor, Listener {

    @NonNull
    private final MessageUtil messageUtil;

    @NonNull
    private final UserHandler userHandler;

    @NonNull
    private final JackPotHandler jackPotHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(args.length == 0) {
            if(jackPotHandler.getJackPot() == null) {
                if(!PermissionUtil.hasPermission(sender, "jackpot.create", false)) {
                    messageUtil.sendMessage(sender, "Aktuell läuft §c§okein§7 Jackpot§8.");
                    return false;
                }

                jackPotHandler.openCreationGui(player);

                return false;
            }

            jackPotHandler.openJackpotGui(player);

            return false;
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("start")) {

                if(!PermissionUtil.hasPermission(sender, "jackpot.create", true))
                    return false;

                if(jackPotHandler.getJackPot() != null) {
                    messageUtil.sendMessage(sender, "Es läuft bereits ein Jackpot§8.");
                    return false;
                }

                long amount = 0;

                try {
                    amount = Long.parseLong(args[1]);
                } catch (final NumberFormatException exception) {
                    amount = MathUtil.getLongFromStringwithSuffix(args[1]);
                }

                if(amount < 100) {
                    messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an die größer als 99 ist§8.");
                    return false;
                }

                jackPotHandler.setJackPot(new JackPot(player, amount));
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 1);


                return false;
            }
        }

        messageUtil.sendSyntax(sender,
                "/jackpot - öffnet das Gui.",
                "/jackpot start <einsatz>",
                "/jackpot join <money>");

        return false;
    }


    @EventHandler
    public void on(final InventoryClickEvent event) {
        if(InventoryUtil.isInventoryTitle(event.getInventory(), "§7Jackpot erstellen")) {
            event.setCancelled(true);

            if(!InventoryUtil.isInventoryTitle(event.getClickedInventory(), "§7Jackpot erstellen"))
                return;

            final Player player = (Player) event.getWhoClicked();

            if(event.getSlot() == 13) {
                player.closeInventory();
                jackPotHandler.getCreatingJackpot().add(player);
                messageUtil.sendMessage(player, "Schreibe den einsatz Betrag in den Chat§8.§7 Nutze cancel um abzubrechen§8.");
            }

            return;
        }

        if(InventoryUtil.startsWithInventoryTitle(event.getInventory(), "§6§lJackpot §7von§e ")) {
            event.setCancelled(true);

            if(!InventoryUtil.startsWithInventoryTitle(event.getClickedInventory(), "§6§lJackpot §7von§e "))
                return;

            final Player player = (Player) event.getWhoClicked();

            if(event.getSlot() == 20) {

                userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
                    player.closeInventory();

                    if(!messageUtil.hasEnougthMoney(user, jackPotHandler.getJackPot().getEinsatz()))
                        return;

                    if(!jackPotHandler.getJackPot().joinPlayer(player))
                        return;

                    user.removeLong(DataType.MONEY, jackPotHandler.getJackPot().getEinsatz());
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 1);
                });
            }

            return;
        }

        if(InventoryUtil.isInventoryTitle(event.getClickedInventory(), "§7Auslosung ..."))
            event.setCancelled(true);
    }
}
