package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       14.10.2022 / 17:46

*/

import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@RequiredArgsConstructor
public final class StackCommand implements CommandExecutor {

    @NonNull
    private final MessageUtil messageUtil;

    private final ArrayList<Material> noStack = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (noStack.size() == 0) {
            noStack.add(Material.DIAMOND_SWORD);
            noStack.add(Material.BOW);
            noStack.add(Material.SKULL_ITEM);
            noStack.add(Material.FISHING_ROD);
        }

        if (!PermissionUtil.hasPermission(player, "stack", true))
            return false;

        final ItemStack[] contents = player.getInventory().getContents();

        int changed = 0;
        int i = 0;

        while(i < contents.length) {
            final ItemStack item = contents[i];

            if (item != null && !noStack.contains(item.getType())) {
                int maxStack = item.getType() == Material.POTION ? 64 : item.getMaxStackSize();

                int needed = maxStack - item.getAmount();
                int i2 = i + 1;

                while (i2 < contents.length) {
                    final ItemStack itemSearch = contents[i2];

                    if (itemSearch != null && itemSearch.getType() != Material.AIR && itemSearch.getAmount() < maxStack
                            && itemSearch.getType() == item.getType()
                            && (itemSearch.getItemMeta() == null && item.getItemMeta() == null || itemSearch.getItemMeta() != null && itemSearch.getItemMeta().equals(item.getItemMeta())
                    )) {

                        if (itemSearch.getType() == Material.POTION) {
                            if (itemSearch.getDurability() != item.getDurability()) {
                                i2++;
                                continue;
                            }
                        }

                        if (itemSearch.getAmount() > needed) {
                            item.setAmount(maxStack);
                            itemSearch.setAmount(itemSearch.getAmount() - needed);
                            changed++;
                            i2++;
                            continue;
                        }

                        contents[i2] = null;
                        item.setAmount(item.getAmount() + itemSearch.getAmount());
                        needed -= itemSearch.getAmount();
                        changed++;
                    }
                    i2++;
                }
            }
            i++;
        }

        if (changed > 0) {
            player.getInventory().setContents(contents);
            player.updateInventory();
            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 5, 5);
            messageUtil.sendMessage(sender, "Es wurden " + changed + " Items gestackt§8.");
            return false;
        }

        messageUtil.sendMessage(sender, "Es konnten keine Items gestackt werden§8.");

        return false;
    }
}
