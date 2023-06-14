package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       25.10.2022 / 23:38

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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@RequiredArgsConstructor
@NonNull
public final class EnchantCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "enchant", true))
            return false;

        if (args.length == 2) {

            if (player.getItemInHand().getType() == Material.AIR) {
                messageUtil.sendMessage(sender, "Du musst ein Item in der Hand halten§8.");
                return false;
            }

            try {

                final int level = Integer.parseInt(args[1]);

                ItemStack item = player.getItemInHand();
                ItemMeta itemMeta = item.getItemMeta();

                if (args[0].equalsIgnoreCase("schärfe") || args[0].equalsIgnoreCase("sharp")) {

                    itemMeta.addEnchant(Enchantment.DAMAGE_ALL, level, true);
                    item.setItemMeta(itemMeta);
                    player.setItemInHand(item);
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 5, 5);

                    return false;
                } else if (args[0].equalsIgnoreCase("haltbarkeit") || args[0].equalsIgnoreCase("durability")) {
                    itemMeta.addEnchant(Enchantment.DURABILITY, level, true);
                    item.setItemMeta(itemMeta);
                    player.setItemInHand(item);
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 5, 5);

                    return false;
                } else if (args[0].equalsIgnoreCase("verbrennung")) {
                    itemMeta.addEnchant(Enchantment.FIRE_ASPECT, level, true);
                    item.setItemMeta(itemMeta);
                    player.setItemInHand(item);
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 5, 5);

                    return false;
                } else if (args[0].equalsIgnoreCase("rückstoß") || args[0].equalsIgnoreCase("knock")) {
                    itemMeta.addEnchant(Enchantment.KNOCKBACK, level, true);
                    item.setItemMeta(itemMeta);
                    player.setItemInHand(item);
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 5, 5);

                    return false;
                } else if (args[0].equalsIgnoreCase("schutz") || args[0].equalsIgnoreCase("prot")) {
                    itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, level, true);
                    item.setItemMeta(itemMeta);
                    player.setItemInHand(item);
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 5, 5);

                    return false;
                } else if (args[0].equalsIgnoreCase("feuerschutz")) {
                    itemMeta.addEnchant(Enchantment.PROTECTION_FIRE, level, true);
                    item.setItemMeta(itemMeta);
                    player.setItemInHand(item);
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 5, 5);

                    return false;
                } else if (args[0].equalsIgnoreCase("dornen")) {
                    itemMeta.addEnchant(Enchantment.THORNS, level, true);
                    item.setItemMeta(itemMeta);
                    player.setItemInHand(item);
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 5, 5);

                    return false;
                } else if (args[0].equalsIgnoreCase("stärke")) {
                    itemMeta.addEnchant(Enchantment.ARROW_DAMAGE, level, true);
                    item.setItemMeta(itemMeta);
                    player.setItemInHand(item);
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 5, 5);

                    return false;
                } else if (args[0].equalsIgnoreCase("schlag")) {
                    itemMeta.addEnchant(Enchantment.ARROW_KNOCKBACK, level, true);
                    item.setItemMeta(itemMeta);
                    player.setItemInHand(item);
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 5, 5);

                    return false;
                } else if (args[0].equalsIgnoreCase("flamme")) {
                    itemMeta.addEnchant(Enchantment.ARROW_FIRE, level, true);
                    item.setItemMeta(itemMeta);
                    player.setItemInHand(item);
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 5, 5);

                    return false;
                } else if (args[0].equalsIgnoreCase("plünderung")) {
                    itemMeta.addEnchant(Enchantment.LOOT_BONUS_MOBS, level, true);
                    item.setItemMeta(itemMeta);
                    player.setItemInHand(item);
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 5, 5);

                    return false;
                } else if (args[0].equalsIgnoreCase("effi")) {
                    itemMeta.addEnchant(Enchantment.DIG_SPEED, level, true);
                    item.setItemMeta(itemMeta);
                    player.setItemInHand(item);
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 5, 5);

                    return false;
                } else if (args[0].equalsIgnoreCase("unendlichkeit") || args[0].equalsIgnoreCase("infinity")) {
                    itemMeta.addEnchant(Enchantment.ARROW_INFINITE, level, true);
                    item.setItemMeta(itemMeta);
                    player.setItemInHand(item);
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 5, 5);

                    return false;

                } else if (args[0].equalsIgnoreCase("glück")) {
                    itemMeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, level, true);
                    item.setItemMeta(itemMeta);
                    player.setItemInHand(item);
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 5, 5);

                    return false;

                } else if (args[0].equalsIgnoreCase("glow")) {
                    itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                    item.setItemMeta(itemMeta);
                    player.setItemInHand(item);
                    player.playSound(player.getLocation(), Sound.ANVIL_USE, 5, 5);

                    return false;
                }

            }catch (final NumberFormatException exception) {
                messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                return false;
            }

        }

        messageUtil.sendMessage(player, "/enchant §8<§7schärfe§c,§7haltbarkeit§c,§7verbrennung§c,§7unendlichkeit§c,§7rückstoß§c,§7effi§c,§7schutz§c,§7feuerschutz§c,§7plünderung§c,§7dornen§c,§7schlag§c,§7stärke§c,§7flamme§c,§7glow§8> <§7l§cv§7l§8>");

        return false;
    }
}
