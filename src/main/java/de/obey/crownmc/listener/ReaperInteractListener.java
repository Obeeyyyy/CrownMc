package de.obey.crownmc.listener;
/*

    Author - Obey -> SkySlayer-v4
       03.11.2022 / 17:06

*/

import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
@NonNull
public final class ReaperInteractListener implements Listener {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;
    private final ServerConfig serverConfig;

    @EventHandler
    public void on(final PlayerInteractAtEntityEvent event) {
        final Player player = event.getPlayer();

        if (!(event.getRightClicked() instanceof ArmorStand))
            return;

        final Entity entity = event.getRightClicked();

        if (entity == null || entity.getCustomName() == null)
            return;

        if (entity.isCustomNameVisible())
            event.setCancelled(true);

        if (!entity.getCustomName().equalsIgnoreCase("§5§lReaper"))
            return;

        if (InventoryUtil.isItemInHandStartsWith(player, "§8»┃ §7Kopf von§e§o ")) {

            final String headOwner = player.getItemInHand().getItemMeta().getDisplayName().split(" ")[3];
            final long amount = getAmountFromHeadItem(player.getItemInHand());

            InventoryUtil.removeItemInHand(player, 1);

            messageUtil.sendMessage(player, "Du hast den Kopf von §e§o" + headOwner + "§7 abgegeben und §f§o" + messageUtil.formatLong(amount) + "§6§l$§7 bekommen§8.");
            userHandler.getUserInstant(player.getUniqueId()).addLong(DataType.MONEY, amount);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 1);

            return;
        }

        if(InventoryUtil.isItemInHandStartsWith(player, "§8( §2§l☯ §8)")) {

            InventoryUtil.removeItemInHand(player, 1);

            userHandler.getUserInstant(player.getUniqueId()).addLong(DataType.MONEY, serverConfig.getSoulReward());
            player.playSound(player.getLocation(), Sound.ENDERMAN_SCREAM, 0.2f, 1);
            messageUtil.sendMessage(player, "§8(§5§lReaper§8) §e§o" + messageUtil.formatLong(serverConfig.getSoulReward()) + "§6§l$ §ffür die Seele§8.");

            return;
        }

        messageUtil.sendMessage(player, "§8(§5§lReaper§8) §fDu hast nichts für mich§8,§f komm später wieder§8.");
        player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
    }


    private long getAmountFromHeadItem(final ItemStack item) {
        final String loreLine = item.getItemMeta().getLore().get(2);

        return Long.parseLong(loreLine.split(" ")[3].replace(",", "").replace(".", "").replace("§e§o$", ""));
    }
}
