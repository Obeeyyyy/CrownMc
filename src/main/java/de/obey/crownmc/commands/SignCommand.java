package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       09.12.2022 / 12:13

*/

import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@NonNull
public final class SignCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "sign", true))
            return false;

        if (!InventoryUtil.hasItemInHand(player))
            return false;

        if (isSigned(player.getItemInHand())) {
            messageUtil.sendMessage(player, "Das Item in deiner Hand ist bereits signiert§8.");
            player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 1);
            return false;
        }

        String message = "";

        if (args.length > 0) {
            for (String arg : args)
                message = message + " " + arg;
        }

        sign(player, message);

        messageUtil.sendMessage(player, "Das Item wurde erfolgreich signiert§8.");
        player.playSound(player.getLocation(), Sound.ANVIL_USE, 0.5f, 1);

        return false;
    }

    private void sign(final Player player, final String message) {
        final ItemStack item = player.getItemInHand();
        final ItemMeta meta = item.getItemMeta();

        final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        final LocalDateTime date = LocalDateTime.now();

        lore.add("");
        lore.add("§8§m-------------------------------");
        lore.add("");
        lore.add("§8▰§7▱ §7Dieser Gegenstand wurde signiert§8.");
        lore.add("§8   - §7Signiert von§8: §e§o" + player.getName());
        lore.add("§8   - §7Signiert am§8: §e§o" + date.getDayOfMonth() + "." + date.getMonthValue() + "." + date.getYear() + " " + date.getHour() + ":" + date.getMinute());

        if (!message.equalsIgnoreCase(""))
            lore.add("§8   - §7Nachricht§8:§e§o" + ChatColor.translateAlternateColorCodes('&', message));

        lore.add("");
        lore.add("§8§m-------------------------------");
        lore.add("");

        meta.setLore(lore);
        item.setItemMeta(meta);
        player.setItemInHand(item);
    }

    private boolean isSigned(final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

        if (lore.size() < 8)
            return false;

        return lore.get(lore.size() - 2).equalsIgnoreCase("§8§m-------------------------------");
    }
}
