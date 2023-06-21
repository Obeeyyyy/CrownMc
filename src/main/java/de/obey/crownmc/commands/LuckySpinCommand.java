package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       15.06.2023 / 02:32

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.LuckySpinHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public final class LuckySpinCommand implements CommandExecutor, Listener {

    @NonNull
    private final MessageUtil messageUtil;

    @NonNull
    private final UserHandler userHandler;

    @NonNull
    private final LuckySpinHandler luckySpinHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        if(!PermissionUtil.hasPermission(sender, "admin", true))
            return false;

        final Player player = (Player) sender;

        if(args.length == 1) {
            if (args[0].equalsIgnoreCase("items")) {

                final Inventory inventory = Bukkit.createInventory(null, 9*7, "setluckyitems");

                if(luckySpinHandler.getItems().size() > 0) {
                    final AtomicInteger slot = new AtomicInteger();
                    luckySpinHandler.getItems().forEach(item -> inventory.setItem(slot.getAndIncrement(), item));
                }

                player.openInventory(inventory);

                return false;
            }
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("reset")) {

                if(!messageUtil.hasPlayedBefore(sender, args[1]))
                    return false;

                userHandler.getUser(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).thenAcceptAsync(user -> {
                   user.setLong(DataType.LASTLUCKYSPIN, System.currentTimeMillis() - 1000*60*60*25);
                   messageUtil.sendMessage(player, user.getOfflinePlayer().getName() + " kann das Rad jetzt wieder drehen§8.");
                });

                return false;
            }
        }

        messageUtil.sendSyntax(sender, "/luckyspin items", "/luckyspin reset <spieler>");

        return false;
    }

    @EventHandler
    public void on(final InventoryCloseEvent event) {
        if(!InventoryUtil.isInventoryTitle(event.getInventory(), "setluckyitems"))
            return;

        final ArrayList<ItemStack> temp = new ArrayList<>();

        for (ItemStack content : event.getInventory().getContents()) {
            if(content != null && content.getType() != Material.AIR)
                temp.add(content);
        }

        luckySpinHandler.setItems(temp);
        messageUtil.sendMessage(event.getPlayer(), "Items gesetzt§8.");
    }

    @EventHandler
    public void signChange(final SignChangeEvent event) {
        if(!PermissionUtil.hasPermission(event.getPlayer(), "admin", false))
            return;

        if(event.getLine(0).startsWith("lw")) {
            event.setLine(0, "§8✤ §d§lLucky Wheel §8✤");
            event.setLine(2, "Klicke hier um");
            event.setLine(3,"zu spielen§8.");
        }
    }

    @EventHandler
    public void signInteract(final PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(!event.getClickedBlock().getType().name().contains("SIGN"))
            return;

        final Sign sign = (Sign) event.getClickedBlock().getState();

        if(sign.getLine(0).equalsIgnoreCase("§8✤ §d§lLucky Wheel §8✤"))
            luckySpinHandler.spin(event.getPlayer());
    }
}
