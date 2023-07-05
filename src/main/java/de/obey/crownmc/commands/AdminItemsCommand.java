package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       20.10.2022 / 04:46

*/

import de.obey.crownmc.handler.KitHandler;
import de.obey.crownmc.handler.RangHandler;
import de.obey.crownmc.objects.pvp.Kit;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

import java.util.UUID;


@RequiredArgsConstructor
@NonNull
public final class AdminItemsCommand implements CommandExecutor {

    private final RangHandler rangHandler;
    private final KitHandler kitHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "*", true))
            return false;

        final Inventory inventory = Bukkit.createInventory(null, 9 * 6, "§4§lsuii");

        inventory.addItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setDisplayname("§8» §5§lEnderchest §8× §7Seite")
                .setTextur("Yjg4ZmVkNmU1YTUyNmRlYTMxNTZiZDJiNDY5YWExMjVjZWMyZjY1YTk3ZTYyYmUxYTE2YTIyNjMxOTRiNWZjMCJ9fX0=", UUID.randomUUID())
                .setLore("",
                        "§8▰§7▱  §5§lRecktsklick",
                        "  §8- §7Um den Gutschein einzulösen und",
                        "  §8- §7eine extra Seite zu erhalten§8.",
                        "")
                .build());

        inventory.addItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setDisplayname("§8» §5§lEnderchest §8× §7Zeile")
                .setTextur("OGMxMGU5ZDM2NDNjZWM2MWEzMDA3N2Q0MTgxNDM1YjZlY2ZlOGE4ODRlNGMzNzY0OTNiZTEzYWY4MmYzZTJiOCJ9fX0=", UUID.randomUUID())
                .setLore("",
                        "§8▰§7▱  §5§lRecktsklick",
                        "  §8- §7Um den Gutschein einzulösen und",
                        "  §8- §7eine extra Zeile zu erhalten§8.",
                        "")
                .build());

        inventory.addItem(new ItemBuilder(Material.PAPER)
                .setDisplayname("§8» §6§lCUSTOM §e§lPREFIX")
                .setLore("",
                        "§8▰§7▱  §6§lRechtsklick",
                        "  §8- §7Schreibe den gewünschten Prefix",
                        "  §8- §7in den Chat und bestätige ihn§8.",
                        "")
                .build());

        rangHandler.getGroupMap().values().forEach(rang -> {
            if (rang.getId() >= 20 && rang.getId() <= 90) {
                inventory.addItem(new ItemBuilder(Material.BOOK).setDisplayname("§a§lRANGGUTSCHEIN").setLore("",
                        "§8▰§7▱ §a§lRechtsklick",
                        "§8 - §7Löst den Gutschein ein§8.",
                        "",
                        "§8▰§7▱ §a§lRang",
                        "§8 -§7 " + rang.getShowprefix(),
                        ""
                ).build());
            }
        });

        inventory.addItem(new ItemBuilder(Material.SLIME_BALL).setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7I").setLore(
                "",
                "§8▰§7▱ §b§lRechtsklick",
                "§8 - §7Um das Set einzulösen§8.",
                ""
        ).build());

        inventory.addItem(new ItemBuilder(Material.SLIME_BALL).setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7II").setLore(
                "",
                "§8▰§7▱ §b§lRechtsklick",
                "§8 - §7Um das Set einzulösen§8.",
                ""
        ).build());

        inventory.addItem(new ItemBuilder(Material.SLIME_BALL).setDisplayname("§b§lPvP§8-§b§lSET§7 Stufe§8.§7III").setLore(
                "",
                "§8▰§7▱ §b§lRechtsklick",
                "§8 - §7Um das Set einzulösen§8.",
                ""
        ).build());

        inventory.addItem(new ItemBuilder(Material.BOOK)
                        .addEnchantment(Enchantment.DURABILITY, 1)
                        .addItemFlags(ItemFlag.HIDE_ENCHANTS)
                        .setDisplayname("§6§lCrown §7Rang §8(§f1 Woche§8)")
                        .setLore("",
                                "§8▰§7▱ §e§lRechtsklick",
                                "§8 - §7Um den §6Crown §7Rang für ",
                                "§8 - §71ne Woche zu erhalten§8.",
                                "")
                .build());

        inventory.addItem(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setDisplayname("§c§lStat Reset")
                        .setLore("",
                                "§c§lInformation",
                                "§8  -§7 Dieses Item erlaubt es dir deine KD auf 0 zu setzen§8.",
                                "")
                        .setTextur("M2VkMWFiYTczZjYzOWY0YmM0MmJkNDgxOTZjNzE1MTk3YmUyNzEyYzNiOTYyYzk3ZWJmOWU5ZWQ4ZWZhMDI1In19fQ==", UUID.randomUUID())
                .build());

        for (final Kit kit : kitHandler.getKitCache().values()) {
            inventory.addItem(new ItemBuilder(Material.IRON_BARDING)
                            .setDisplayname("§8» §7Kit Gutschein")
                            .setLore("",
                                    "§f§lInformation",
                                    "  §8- §7Kit§8:§f " + kit.getName().toUpperCase(),
                                    "")
                    .build());
        }

        inventory.addItem(new ItemBuilder(Material.SNOW_BALL)
                        .setDisplayname("§3§lSwitcher")
                        .setLore("",
                        "§3§lInformation",
                        "  §8- §7Dieses Item ermöglicht es dir die Position mit anderen zu tauschen§8.",
                                "")
                .build());

        player.openInventory(inventory);

        return false;
    }
}
