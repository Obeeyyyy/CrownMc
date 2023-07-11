package de.obey.crownmc.listener;
/*

    Author - Obey -> CrownMc
       05.07.2023 / 02:20

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

@RequiredArgsConstructor
public final class FreeSignListener implements Listener {

    @NonNull
    private final MessageUtil messageUtil;

    private final HashMap<Sign, Long> signCooldowns = new HashMap<>();

    @EventHandler
    public void on(final SignChangeEvent event) {
        if (!event.getLine(0).equalsIgnoreCase("free"))
            return;

        final Player player = event.getPlayer();

        if (!PermissionUtil.hasPermission(player, "admin", true))
            return;

        event.setLine(0, "§9§lFree Item");
        event.setLine(3, "Klick mich.");
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (!(event.getClickedBlock().getState() instanceof Sign))
            return;

        final Sign sign = (Sign) event.getClickedBlock().getState();

        if (!sign.getLine(0).equalsIgnoreCase("§9§lFree Item"))
            return;

        final String[] itemData = sign.getLine(1).split(":");

        final Material material = Material.matchMaterial(itemData[0]);

        if (material == null) {
            messageUtil.sendMessage(player, "§c§oDieses Schild wurde nicht richtig eingestellt, bitte kontaktiere ein Teammitglied§8.");
            return;
        }

        if(signCooldowns.containsKey(sign)) {
            final long millis = signCooldowns.get(sign);
            if(millis > System.currentTimeMillis()) {
                player.playSound(player.getLocation(), Sound.EXPLODE, 0.2f, 1);
                messageUtil.sendMessage(player, "Du kannst dieses Schild in " + MathUtil.getHoursAndMinutesAndSecondsFromSeconds((millis - System.currentTimeMillis()) / 1000) + "wieder nutzen§8.");
                return;
            }
        }

        // this is in seconds
        final long cooldown = sign.getLine(2).equalsIgnoreCase("") ? 0 : Long.parseLong(sign.getLine(2));
        signCooldowns.put(sign, System.currentTimeMillis() + (cooldown*1000));

        final int subId = itemData.length > 1 ? Integer.parseInt(itemData[1]) : 0;
        final int amount = itemData.length < 2 ? 1 : Integer.parseInt(itemData[2]);

        final ItemStack item = new ItemStack(material, amount, (short) subId);
        InventoryUtil.addItem(player, item);
        player.playSound(player.getLocation(), Sound.CHICKEN_EGG_POP, 0.5f, 1);
    }
}