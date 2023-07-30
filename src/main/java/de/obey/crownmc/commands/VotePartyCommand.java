package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       26.06.2023 / 00:42

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.handler.VotePartyHandler;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Giant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@RequiredArgsConstructor @NonNull
public final class VotePartyCommand implements CommandExecutor, Listener {

    @NonNull
    private final MessageUtil messageUtil;
    private final VotePartyHandler votePartyHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(!PermissionUtil.hasPermission(sender, "admin", true))
            return false;

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("items")) {
                final Inventory inventory = Bukkit.createInventory(null, 9*7, "Voteparty Items");
                final ArrayList<ItemStack> items = votePartyHandler.getItems();

                if(!items.isEmpty()) {
                    for (ItemStack item : items) {
                        if(item.getType() != Material.AIR) {
                            inventory.addItem(item);
                        }
                    }
                }

                player.openInventory(inventory);

                return false;
            }

            if(args[0].equalsIgnoreCase("reloadloc")) {
                votePartyHandler.loadLocations();
                messageUtil.sendMessage(sender, votePartyHandler.getLocations().size() + " Locations geladen§8.");
                return false;
            }

            if (args[0].equalsIgnoreCase("start")) {
                votePartyHandler.startVoteParty();
                return false;
            }
        }

        messageUtil.sendSyntax(sender,"/voteparty items",
                "/voteparty reloadloc", "/voteparty start");

        return false;
    }

    @EventHandler
    public void on(final InventoryCloseEvent event) {
        if (event.getInventory().getTitle() != null && event.getInventory().getTitle().equalsIgnoreCase("Voteparty Items")) {
            final ArrayList<ItemStack> items = new ArrayList<>();

            for (ItemStack content : event.getInventory().getContents()) {
                if (content != null && content.getType() != Material.AIR)
                    items.add(content);
            }

            votePartyHandler.setItems(items);
            messageUtil.sendMessage(event.getPlayer(), "VoteParty Items gespeichert.");
        }
    }

    @EventHandler
    public void on(final EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Giant))
            return;

        event.getEntity().setCustomName(((Giant) event.getEntity()).getHealth() + "§c§l❤");
    }

    @EventHandler
    public void on(final EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Giant))
            return;

        if(!(event.getDamager() instanceof Player))
            return;

        messageUtil.sendActionBar((Player) event.getDamager(), "Noch§8: §f§o" + ((Giant) event.getEntity()).getHealth() + "§c§l❤");
        votePartyHandler.getParties().get(votePartyHandler.getParties().size() - 1).damageBoss((Player) event.getDamager(), event.getDamage());
    }

    @EventHandler
    public void on(final EntityDeathEvent event) {
        if(!(event.getEntity() instanceof Giant))
            return;

        if(!event.getEntity().isCustomNameVisible())
            return;

        votePartyHandler.getParties().get(votePartyHandler.getParties().size() - 1).end();
    }
}
