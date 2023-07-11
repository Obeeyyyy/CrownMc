package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       27.12.2022 / 19:24

*/

import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@RequiredArgsConstructor
@NonNull
public final class VoteCommand implements CommandExecutor, Listener {

    private final ServerConfig serverConfig;
    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(args.length == 0) {

            player.sendMessage("§8§l§m---------------------------------------------");
            player.sendMessage("");
            player.sendMessage(" §8» ┃ §a§l Vote 1 §8:§f§o https://minecraft-server.eu/vote/index/22B8F/" + player.getName());
            player.sendMessage(" §8» ┃ §a§l Vote 2 §8:§f§o http://vote2.crownmc.de");
            player.sendMessage(" §8» ┃ §a§l Vote 3 §8:§f§o http://vote3.crownmc.de");
            player.sendMessage("");
            player.sendMessage("§8§l§m---------------------------------------------");

            return false;
        }

        if(!PermissionUtil.hasPermission(player, "admin", true))
            return false;

        if(args.length == 1) {

            if(args[0].equalsIgnoreCase("items")) {
                final Inventory inventory = Bukkit.createInventory(null, 9 * 5, "Voteitems");
                final YamlConfiguration cfg = serverConfig.getCfg();

                final ArrayList<ItemStack> contents = cfg.contains("voteitems") ? (ArrayList<ItemStack>) cfg.getList("voteitems") : new ArrayList<>();

                if (contents.size() > 0)
                    contents.forEach(inventory::addItem);

                player.openInventory(inventory);

                return false;
            }
        }

        messageUtil.sendSyntax(sender, "/vote items");

        return false;
    }

    @EventHandler
    public void on(final InventoryCloseEvent event) {
        if (event.getInventory().getTitle() != null && event.getInventory().getTitle().equalsIgnoreCase("Voteitems")) {

            final YamlConfiguration cfg = serverConfig.getCfg();
            final ArrayList<ItemStack> items = new ArrayList<>();

            for (ItemStack content : event.getInventory().getContents()) {
                if (content != null && content.getType() != Material.AIR)
                    items.add(content);
            }

            cfg.set("voteitems", items);

            FileUtil.saveToFile(serverConfig.getConfigFile(), cfg);
            messageUtil.sendMessage(event.getPlayer(), "Vote Items gespeichert.");
        }
    }
}
