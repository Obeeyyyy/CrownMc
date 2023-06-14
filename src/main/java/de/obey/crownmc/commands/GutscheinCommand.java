package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       25.10.2022 / 16:23

*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

@RequiredArgsConstructor
@NonNull
public final class GutscheinCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 1) {

            long amount = 0L;

            try {
                amount = Long.parseLong(args[0]);

                if (amount <= 0) {
                    messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an die größer als 0 ist.");
                    return false;
                }

            } catch (final NumberFormatException exception) {

                amount = MathUtil.getLongFromStringwithSuffix(args[0]);

                if (amount <= 0) {
                    messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an, oder benutze folgende Abkürzungen. (k, m, mrd, b, brd, t)");
                    return false;
                }
            }

            final User user = userHandler.getUserInstant(player.getUniqueId());

            if (!messageUtil.hasEnougthMoney(user, amount))
                return false;

            user.removeLong(DataType.MONEY, amount);
            messageUtil.sendMessage(sender, "Du hast einen §8(§e§o" + messageUtil.formatLong(amount) + "§6$§8)§7 Gutschein erstellt.");
            InventoryUtil.addItem(player, new ItemBuilder(Material.DOUBLE_PLANT, 1)
                    .setDisplayname("§8» §7Gutschein")
                    .setLore("",
                            "§8▰§7▱  §6§lBetrag",
                            "  §8-§f§o " + messageUtil.formatLong(amount) + "§6§l$",
                            "",
                            "§8▰§7▱  §6§lRecktsklick",
                            "  §8- §7Um den Gutschein einzulösen§8.").build());

            return false;
        }

        messageUtil.sendSyntax(player, "/gutschein <zahl>");

        return false;
    }

    @EventHandler
    public void on(final PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        final Player player = event.getPlayer();

        if (!InventoryUtil.isItemInHandWithDisplayname(player, "§8» §7Gutschein"))
            return;

        event.setCancelled(true);

        final int multiplier = player.getItemInHand().getAmount();
        final List<String> lore = player.getItemInHand().getItemMeta().getLore();
        final long amount = Long.parseLong(lore.get(2).split(" ")[3].replace("§6§l$", "").replace(",", "").replace(".", ""));

        InventoryUtil.removeItemInHand(player, multiplier);

        userHandler.getUserInstant(player.getUniqueId()).addLong(DataType.MONEY, amount * multiplier);
        messageUtil.sendMessage(player, "Du hast §e§o" + messageUtil.formatLong(amount * multiplier) + "§6$§7 eingelöst§8.");
        player.playSound(player.getLocation(), Sound.BURP, 1, 0.5f);
    }
}
