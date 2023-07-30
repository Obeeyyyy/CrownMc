package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       27.07.2023 / 03:42

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public final class RangInfoCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;
        final Inventory inventory = Bukkit.createInventory(null, 9*5, "§6§lCrownMc§7 Ranginfo");

        inventory.setItem(4, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setTextur("NTE4OWYzNDdmNDI0NTBjZDJhMmU5YjhhNTM5ODgwN2QyOGM3ZjQyNTRiZDk5YThhNDk5Y2U1NDM1MzIwOTU1In19fQ==", UUID.randomUUID())
                        .setDisplayname("§6§lCrown §7Rang")
                        .setLore("",
                                "§7Mit diesem Rang erhälst du folgendes§8:",
                                "",
                                "§f§lExtras",
                                "§8  -§7 Chatlines in /Settings",
                                "§8  -§7 Zugriff DailyKit 4",
                                "§8  -§7 10 Plots",
                                "",
                                "§f§lCommands",
                                "§8  -§7 /feed <spieler>",
                                "§8  -§7 /fly",
                                "§8  -§7 /rename",
                                "§8  -§7 /repair",
                                "§8  -§7 /sign",
                                "§8  -§7 /rainbowtab",
                                "")
                .build());


        inventory.setItem(13, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setTextur("NmMxODNmMzYwMWY2NzI5M2E1MWUyNTc3ZGJlYTA1YTQ2MDJlZmE0MzNjYzM1NGU2M2ZjZjdmYmUwMjY0ODZiYyJ9fX0=", UUID.randomUUID())
                        .setDisplayname("§e§lKing §7Rang")
                        .setLore("",
                                "§7Mit diesem Rang erhälst du folgendes§8:",
                                "",
                                "§f§lExtras",
                                "§8  -§7 Farbe im Chat",
                                "§8  -§7 7 Plots",
                                "§8  -§7 VIP Bereich im Casino",
                                "",
                                "§f§lCommands",
                                "§8  -§7 /heal",
                                "§8  -§7 /payall",
                                "§8  -§7 /time",
                                "§8  -§7 /tpahere",
                                "§8  -§7 /joinmessage",
                                "§8  -§7 /leavemessage",
                                "§8  -§7 /skull",
                                "")
                .build());

        inventory.setItem(22, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("YTNlOWY0ZGJhZGRlMGY3MjdjNTgwM2Q3NWQ4YmIzNzhmYjlmY2I0YjYwZDMzYmVjMTkwOTJhM2EyZTdiMDdhOSJ9fX0=", UUID.randomUUID())
                .setDisplayname("§a§lKnight §7Rang")
                .setLore("",
                        "§7Mit diesem Rang erhälst du folgendes§8:",
                        "",
                        "§f§lExtras",
                        "§8  -§7 Farbe auf Schildern",
                        "§8  -§7 Zugriff DailyKit 3",
                        "§8  -§7 3 Plots",

                        "",
                        "§f§lCommands",
                        "§8  -§7 /jackpot",
                        "§8  -§7 /clear",
                        "§8  -§7 /hat",
                        "§8  -§7 /pcc",
                        "§8  -§7 /night",
                        "§8  -§7 /werbung",
                        "")
                .build());

        inventory.setItem(31, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("OTc1YjdhYzlmMGM3MTIzMDNjZDNiNjU0ZTY0NmNlMWM0YmYyNDNhYjM0OGE2YTI1MzcwZjI2MDNlNzlhNjJhMCJ9fX0=", UUID.randomUUID())
                .setDisplayname("§3§lGuard §7Rang")
                .setLore("",
                        "§7Mit diesem Rang erhälst du folgendes§8:",
                        "",
                        "§f§lExtras",
                        "§8  -§7 Openall in den Settings",
                        "§8  -§7 Zugriff DailyKit 2",
                        "§8  -§7 2 Plots",

                        "",
                        "§f§lCommands",
                        "§8  -§7 /bodysee",
                        "§8  -§7 /invsee",
                        "§8  -§7 /day",
                        "")
                .build());

        inventory.setItem(40, new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                .setTextur("YTE5ZDY0NjEyYmE4ZDFjMDJlZTI3MGQ4NDUxOWFkMGNkNzMxNzViYzQ1ZTdkZGEzZjYzOTY4NmIyY2U2NDU5NiJ9fX0=", UUID.randomUUID())
                .setDisplayname("§7Warrior §7Rang")
                .setLore("",
                        "§7Mit diesem Rang erhälst du folgendes§8:",
                        "",
                        "§f§lCommands",
                        "§8  -§7 /enderchest",
                        "§8  -§7 /workbench",
                        "§8  -§7 /feed",
                        "")
                .build());

        player.openInventory(inventory);

        return false;
    }

    @EventHandler
    public void on(final InventoryClickEvent event) {
        if(InventoryUtil.isInventoryTitle(event.getInventory(), "§6§lCrownMc§7 Ranginfo"))
            event.setCancelled(true);
    }
}
