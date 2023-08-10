package de.obey.crownmc.objects.gambling.blackjack;
/*

    Author - Obey -> CrownMc
       10.08.2023 / 06:03

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.handler.BlackJackHander;
import de.obey.crownmc.handler.RouletteHandler;
import de.obey.crownmc.objects.CNPC;
import de.obey.crownmc.util.ArmorStandBuilder;
import de.obey.crownmc.util.ItemBuilder;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class BlackJackTable {

    private final MessageUtil messageUtil;
    private final BlackJackHander blackJackHander;
    private final CardDeck deck = new CardDeck();

    @Getter
    private final int tableID;

    @Setter
    private Location location;

    private CNPC dealer;

    private final String identifier = "§r§r";

    @Getter
    private final HashMap<UUID, Long> betAmounts = new HashMap<>();

    @Getter
    private int state = 0; // 0 = waiting, 1 = stating soon, 2 = progress, 3 = done

    private BukkitTask runnable;

    public BlackJackTable(final int tableID, final Location location, final BlackJackHander blackJackHander) {
        this.tableID = tableID;
        this.location = location;
        this.blackJackHander = blackJackHander;
        messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();

        if(location == null) {
            messageUtil.warn("Cant find Location  blackjack" + tableID);
            return;
        }

        spawnStands();

        messageUtil.log("Created Blackjacktable " + tableID);
    }

    private final HashMap<Integer, ArmorStand> stands = new HashMap<>();
    private void spawnStands() {
        final ArmorStand dealer = location.getWorld().spawn(location, ArmorStand.class);

        dealer.setCustomName(identifier + "§f§lBlackJack§7 Dealer");
        dealer.setCustomNameVisible(true);
        dealer.setGravity(false);
        dealer.setHelmet(new ItemBuilder(Material.SKULL_ITEM, 1, (byte) 3)
                        .setSkullOwner("Obeeyyyy")
                .build());
        dealer.setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE)
                        .setColor(DyeColor.WHITE)
                .build());
        dealer.setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS)
                .setColor(DyeColor.WHITE)
                .build());
        dealer.setBoots(new ItemBuilder(Material.LEATHER_BOOTS)
                .setColor(DyeColor.WHITE)
                .build());

        stands.put(0, dealer);
    }

    public void killStands() {
        if (location == null)
            return;

        if (location.getWorld().getEntities().isEmpty())
            return;

        for (final Entity entity : location.getWorld().getEntities()) {
            if (entity instanceof ArmorStand) {
                if (entity.getCustomName() == null)
                    continue;

                if (entity.getCustomName().startsWith(identifier))
                    entity.remove();
            }
        }
    }

    public void shutdown() {
        killStands();

        state = 3;

        if(runnable != null)
            runnable.cancel();

        if(betAmounts.isEmpty())
            return;

        for (final UUID uuid : betAmounts.keySet())
            blackJackHander.pay(uuid, betAmounts.get(uuid));
    }


}
