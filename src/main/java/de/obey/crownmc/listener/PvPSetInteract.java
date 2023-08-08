package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       28.12.2022 / 20:45

*/

import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@NonNull
public final class PvPSetInteract implements Listener {

    @EventHandler
    public void onInteract(final PlayerInteractEvent event) {
        if (!InventoryUtil.hasItemInHand(event.getPlayer(), false))
            return;

        final Player player = event.getPlayer();

        if (player.getItemInHand().getType() != Material.SLIME_BALL)
            return;

        if (!InventoryUtil.isItemInHandStartsWith(player, "§b§lPvP§8-§b§lSET§7 "))
            return;

        if (InventoryUtil.isItemInHandWithDisplayname(player, "§b§lPvP§8-§b§lSET§7 Stufe§8.§7I")) {
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.HORSE_ARMOR, 1, 1);
            InventoryUtil.removeItemInHand(player, 1);

            InventoryUtil.addItem(player, new ItemBuilder(Material.STONE_SWORD)
                    .setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7I")
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .build());

            InventoryUtil.addItem(player, new ItemBuilder(Material.DIAMOND_HELMET)
                    .setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7I")
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .build());

            InventoryUtil.addItem(player, new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                    .setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7I")
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .build());

            InventoryUtil.addItem(player, new ItemBuilder(Material.DIAMOND_LEGGINGS)
                    .setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7I")
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .build());

            InventoryUtil.addItem(player, new ItemBuilder(Material.DIAMOND_BOOTS)
                    .setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7I")
                    .addEnchantment(Enchantment.DURABILITY, 1)
                    .build());

            InventoryUtil.addItem(player, new ItemBuilder(Material.GOLDEN_APPLE, 1).build());
            InventoryUtil.addItem(player, new ItemBuilder(Material.ENDER_PEARL, 3).build());
            InventoryUtil.addItem(player, new ItemBuilder(Material.BREAD, 16).build());

            return;
        }

        if (InventoryUtil.isItemInHandWithDisplayname(player, "§b§lPvP§8-§b§lSET§7 Stufe§8.§7II")) {
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.HORSE_ARMOR, 1, 1);
            InventoryUtil.removeItemInHand(player, 1);

            InventoryUtil.addItem(player, new ItemBuilder(Material.STONE_SWORD)
                    .setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7II")
                    .addEnchantment(Enchantment.DURABILITY, 2)
                    .addEnchantment(Enchantment.DAMAGE_ALL, 1)
                    .build());

            InventoryUtil.addItem(player, new ItemBuilder(Material.DIAMOND_HELMET)
                    .setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7II")
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                    .addEnchantment(Enchantment.DURABILITY, 2)
                    .build());

            InventoryUtil.addItem(player, new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                    .setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7II")
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                    .addEnchantment(Enchantment.DURABILITY, 2)
                    .build());

            InventoryUtil.addItem(player, new ItemBuilder(Material.DIAMOND_LEGGINGS)
                    .setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7II")
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                    .addEnchantment(Enchantment.DURABILITY, 2)
                    .build());

            InventoryUtil.addItem(player, new ItemBuilder(Material.DIAMOND_BOOTS)
                    .setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7II")
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
                    .addEnchantment(Enchantment.DURABILITY, 2)
                    .build());

            InventoryUtil.addItem(player, new ItemBuilder(Material.GOLDEN_APPLE, 2).build());
            InventoryUtil.addItem(player, new ItemBuilder(Material.ENDER_PEARL, 5).build());
            InventoryUtil.addItem(player, new ItemBuilder(Material.BREAD, 16).build());

            return;
        }

        if (InventoryUtil.isItemInHandWithDisplayname(player, "§b§lPvP§8-§b§lSET§7 Stufe§8.§7III")) {
            event.setCancelled(true);
            player.playSound(player.getLocation(), Sound.HORSE_ARMOR, 1, 1);
            InventoryUtil.removeItemInHand(player, 1);

            InventoryUtil.addItem(player, new ItemBuilder(Material.STONE_SWORD)
                    .setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7III")
                    .addEnchantment(Enchantment.DAMAGE_ALL, 2)
                    .addEnchantment(Enchantment.DURABILITY, 3)
                    .build());

            InventoryUtil.addItem(player, new ItemBuilder(Material.BOW)
                    .setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7III")
                    .addEnchantment(Enchantment.ARROW_DAMAGE, 1)
                    .addEnchantment(Enchantment.DURABILITY, 2)
                    .build());

            InventoryUtil.addItem(player, new ItemStack(Material.ARROW, 32));

            InventoryUtil.addItem(player, new ItemBuilder(Material.DIAMOND_HELMET)
                    .setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7III")
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .addEnchantment(Enchantment.DURABILITY, 3)
                    .build());

            InventoryUtil.addItem(player, new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                    .setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7III")
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .addEnchantment(Enchantment.DURABILITY, 3)
                    .build());

            InventoryUtil.addItem(player, new ItemBuilder(Material.DIAMOND_LEGGINGS)
                    .setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7III")
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .addEnchantment(Enchantment.DURABILITY, 3)
                    .build());

            InventoryUtil.addItem(player, new ItemBuilder(Material.DIAMOND_BOOTS)
                    .setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7III")
                    .addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                    .addEnchantment(Enchantment.DURABILITY, 3)
                    .build());

            InventoryUtil.addItem(player, new ItemBuilder(Material.GOLDEN_APPLE, 3).build());
            InventoryUtil.addItem(player, new ItemBuilder(Material.ENDER_PEARL, 8).build());
            InventoryUtil.addItem(player, new ItemBuilder(Material.BREAD, 32).build());
        }
    }

}
