package de.obey.crownmc.handler;
/*

    Author - Obey -> CrownMc
       30.06.2023 / 00:51

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.objects.gambling.JackPot;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MathUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

public final class JackPotHandler {

    @Getter @Setter
    private JackPot jackPot;

    @Getter
    private final ArrayList<Player> creatingJackpot;

    public JackPotHandler() {
        creatingJackpot = new ArrayList<>();
    }

    public void endJackpot() {
        jackPot = null;
    }

    public boolean isCreatingJackpot(final Player player, final String message) {
        if(creatingJackpot.contains(player)) {

            if(message.equalsIgnoreCase("cancel")) {
                creatingJackpot.remove(player);
                CrownMain.getInstance().getInitializer().getMessageUtil().sendMessage(player, "§c§oVorgang abgebrochen§8.");
                return true;
            }

            long amount = 0;

            try {
                amount = Long.parseLong(message);
            } catch (final NumberFormatException exception) {
                amount = MathUtil.getLongFromStringwithSuffix(message);
            }

            if(amount < 100) {
                CrownMain.getInstance().getInitializer().getMessageUtil().sendMessage(player, "Bitte gebe eine Zahl an die größer als 99 ist§8.");
                return true;
            }

            jackPot = new JackPot(player, amount);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 1);
            creatingJackpot.remove(player);

            return true;
        }
        return false;
    }

    public void openCreationGui(final Player player) {
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 1);

        final Inventory inventory = Bukkit.createInventory(null, 9*3, "§7Jackpot erstellen");

        InventoryUtil.fill(inventory, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 8).setDisplayname("§7-§8/§7-").build());
        InventoryUtil.fillSideRows(inventory, new ItemBuilder(Material.IRON_FENCE).setDisplayname("§7-§8/§7-").build());

        inventory.setItem(13, new ItemBuilder(Material.ITEM_FRAME)
                        .setDisplayname("§8» §7Einsatz festlegen")
                .build());

        player.openInventory(inventory);
    }

    public void openJackpotGui(final Player player) {
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 0.5f, 1);
        jackPot.updateInventory();
        player.openInventory(jackPot.getInventory());
    }

    public void shutdown() {
        if(jackPot == null)
            return;

        jackPot.getRunnable().cancel();
        jackPot.getInventory().clear();
        for (Player player : jackPot.getTeilnehmer()) {
            CrownMain.getInstance().getInitializer().getUserHandler().getUser(player.getUniqueId()).thenAcceptAsync(user -> {
               user.addLong(DataType.MONEY, jackPot.getEinsatz());
            });
        }

        jackPot = null;
    }

}
