// Made by Richard


package de.obey.crownmc.listener;

import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.LocationHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.InventoryUtil;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class BlockCounterListener implements Listener {
    private final ServerConfig serverConfig;
    private final UserHandler userHandler;
    private final LocationHandler locationHandler;
    private final ArrayList<Location> destroyedLocation = new ArrayList<>();

    public BlockCounterListener(ServerConfig serverConfig, UserHandler userHandler, LocationHandler locationHandler) {
        this.serverConfig = serverConfig;
        this.userHandler = userHandler;
        this.locationHandler = locationHandler;
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent event) {
        serverConfig.setPlaceCounter(1 + serverConfig.getPlaceCounter());
    }

    @EventHandler
    public void onBlockDestroy(BlockBreakEvent event) {
        serverConfig.setBreakCounter(1 + serverConfig.getBreakCounter());
        Player player = event.getPlayer();
        Block block = event.getBlock();
        userHandler.getUserInstant(player.getUniqueId()).addLong(DataType.DESTROYEDBLOCKS, 1);

        final Location farmwelt = locationHandler.getLocation("farmwelt");

        if (InventoryUtil.hasItemInHand(player, false)) {
            ItemStack itemStack = player.getItemInHand();
            if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
                return;
            }
        }

        if (farmwelt != null && player.getWorld() == farmwelt.getWorld()) {
            if (serverConfig.getDestroyEventGoal().equalsIgnoreCase(block.getType().name())) {

                if (!destroyedLocation.contains(block.getLocation())) {
                    destroyedLocation.add(block.getLocation());
                    userHandler.getUserInstant(player.getUniqueId()).addLong(DataType.DESTROYEDEVENTBLOCKS, 1);
                }
            }
        }
    }
}
