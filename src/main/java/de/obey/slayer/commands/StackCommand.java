package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       14.10.2022 / 17:46

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

        while (i < contents.length) {
            final ItemStack current = contents[i];

            if (current != null && current.getType() != null && current.getType() != Material.AIR) {
                final int maxStack = current.getType() == Material.ENDER_PEARL ? 64 : (current.getType() == Material.POTION ? 3 : current.getMaxStackSize());

                if (current.getAmount() < maxStack && !noStack.contains(current.getType())) {
                    int needed = maxStack - current.getAmount();
                    int i2 = i + 1;
                    while (i2 < contents.length) {
                        if (contents[i2] != null && contents[i2].getType() != Material.AIR && contents[i2].getAmount() > 0 && current.getType() == contents[i2].getType()) {
                            final ItemStack nextCurrent = contents[i2].clone();
                            if (current.getDurability() == nextCurrent.getDurability() && (current.getItemMeta() == null && nextCurrent.getItemMeta() == null || current.getItemMeta() != null && current.getItemMeta().equals((Object) nextCurrent.getItemMeta()))) {
                                if (nextCurrent.getAmount() > needed) {
                                    current.setAmount(maxStack);
                                    contents[i2].setAmount(nextCurrent.getAmount() - needed);
                                    ++changed;
                                } else {
                                    contents[i2].setType(Material.AIR);
                                    current.setAmount(current.getAmount() + nextCurrent.getAmount());
                                    needed = maxStack - current.getAmount();
                                    ++changed;
                                }
                            }
                        }
                        ++i2;
                    }
                }
            }
            ++i;
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
