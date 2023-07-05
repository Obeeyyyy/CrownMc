package de.obey.crownmc.listener;
/*

    Author - Obey -> CrownMc
       05.07.2023 / 02:15

    You are NOT allowed to use this code in any form
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.handler.KitHandler;
import de.obey.crownmc.objects.pvp.Kit;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor @NonNull
public final class KitGutscheinInteract implements Listener {

    private final MessageUtil messageUtil;
    private final KitHandler kitHandler;

    @EventHandler
    public void on(final PlayerInteractEvent event) {
        if(!InventoryUtil.isItemInHandWithDisplayname(event.getPlayer(), "§8» §7Kit Gutschein"))
            return;

        final Player player = event.getPlayer();
        event.setCancelled(true);


        final String kitName = event.getItem().getItemMeta().getLore().get(2).split(" ")[4].toLowerCase();
        final Kit kit = kitHandler.getKitByName(kitName);

        if(kit == null) {
            messageUtil.sendMessage(player, "Das Kit §8'§f§o" + kitName + "§8'§7 existiert nicht§8.");
            player.playSound(player.getLocation(), Sound.EXPLODE, 0.2f, 1);
            return;
        }

        InventoryUtil.removeItemInHand(player, 1);
        kitHandler.equipKit(player, kitHandler.getKitByName(kitName));
    }
}
