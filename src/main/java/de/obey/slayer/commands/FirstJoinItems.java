package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       19.10.2022 / 19:22

*/

import de.obey.slayer.backend.ServerConfig;
import de.obey.slayer.util.FileUtil;
import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
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
public final class FirstJoinItems implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final ServerConfig serverConfig;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "*", true))
            return false;

        final Inventory inventory = Bukkit.createInventory(null, 9 * 5, "Firstjoin");
        final YamlConfiguration cfg = serverConfig.getCfg();

        final ArrayList<ItemStack> contents = cfg.contains("firstjoin") ? (ArrayList<ItemStack>) cfg.getList("firstjoin") : new ArrayList<>();

        if (contents.size() > 0)
            contents.forEach(inventory::addItem);

        player.openInventory(inventory);

        return false;
    }

    @EventHandler
    public void on(final InventoryCloseEvent event) {
        if (event.getInventory().getTitle() != null && event.getInventory().getTitle().equalsIgnoreCase("Firstjoin")) {

            final YamlConfiguration cfg = serverConfig.getCfg();
            final ArrayList<ItemStack> items = new ArrayList<>();

            for (ItemStack content : event.getInventory().getContents()) {
                if (content != null && content.getType() != Material.AIR)
                    items.add(content);
            }

            cfg.set("firstjoin", items);

            FileUtil.saveToFile(serverConfig.getConfigFile(), cfg);
            messageUtil.sendMessage(event.getPlayer(), "FirstJoin Items gespeichert.");
        }
    }
}
