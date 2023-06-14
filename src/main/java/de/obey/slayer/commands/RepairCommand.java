package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       26.10.2022 / 22:35

*/

import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public final class RepairCommand implements CommandExecutor {

    @NonNull
    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "repair", true))
            return false;

        final boolean all = !player.getWorld().getName().equalsIgnoreCase("pvp");

        if (all) {
            int count = 0;

            for (final ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    if (!item.getType().isBlock() && item.getType() != Material.INK_SACK
                            && item.getType() != Material.GOLDEN_APPLE && item.getType() != Material.AIR
                            && item.getType() != Material.POTION && item.getType() != Material.SKULL_ITEM
                            && item.getType() != Material.MONSTER_EGG && item.getType() != Material.EXP_BOTTLE) {
                        item.setDurability((short) 0);
                        count++;
                    }
                }
            }

            if (player.getInventory().getHelmet() != null) {
                player.getInventory().getHelmet().setDurability((short) 0);
                count++;
            }
            if (player.getInventory().getChestplate() != null) {
                player.getInventory().getChestplate().setDurability((short) 0);
                count++;
            }
            if (player.getInventory().getBoots() != null) {
                player.getInventory().getBoots().setDurability((short) 0);
                count++;
            }
            if (player.getInventory().getLeggings() != null) {
                player.getInventory().getLeggings().setDurability((short) 0);
                count++;
            }

            if (count <= 0) {
                player.playSound(player.getLocation(), Sound.EXPLODE, 0.5f, 5);
                messageUtil.sendMessage(sender, "Keine Items konnten repariert werden§8.");
            } else {
                player.playSound(player.getLocation(), Sound.ANVIL_USE, 1, 0.5f);
                messageUtil.sendMessage(sender, "Alle Items in deinem Inventar wurde repariert§8.");
            }

            player.updateInventory();

            return false;
        }

        final ItemStack item = player.getItemInHand();

        if (item == null || item.getType() == Material.AIR) {
            messageUtil.sendMessage(sender, "Du musst ein Item in der Hand halten§8.");
            return false;
        }

        final String type = item.getType().name();
        if (item.getType().isBlock() || item.getType() == Material.INK_SACK
                || item.getType() == Material.GOLDEN_APPLE || item.getType() == Material.AIR
                || item.getType() == Material.POTION || item.getType() == Material.SKULL_ITEM
                || item.getType() == Material.MONSTER_EGG || item.getType() == Material.EXP_BOTTLE) {
            messageUtil.sendMessage(sender, "Dieses Item kann nicht repariert werden§8.");
            return false;
        }

        item.setDurability((short) 0);
        player.playSound(player.getLocation(), Sound.ANVIL_USE, 1, 0.5f);
        messageUtil.sendMessage(sender, "Das Item wurde repariert§8.");

        return false;
    }
}
