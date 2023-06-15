package de.obey.crownmc.handler;
/*

    Author - Obey -> CrownMc
       15.06.2023 / 02:10

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.objects.gambling.LuckySpin;
import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;


public final class LuckySpinHandler {

    private final LocationHandler locationHandler;
    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    private final YamlConfiguration cfg;

    @Getter
    private ArrayList<ItemStack> items = new ArrayList<>();

    @Getter
    private LuckySpin luckySpin;

    public LuckySpinHandler(final LocationHandler locationHandler, final MessageUtil messageUtil, final UserHandler userHandler) {
        this.locationHandler = locationHandler;
        this.messageUtil = messageUtil;
        this.userHandler = userHandler;

        cfg = FileUtil.getCfg(FileUtil.getFile("luckyspin.yml"));

        if(cfg.contains("items")) {
            items = (ArrayList<ItemStack>) cfg.get("items");
        } else {
            items.add(new ItemStack(Material.STICK));
        }

        setup();
    }

    public void spin(final Player player) {
        if(luckySpin == null)
            return;

        if(luckySpin.getState() == 1) {
            messageUtil.sendMessage(player, "Bitte warte bis das Rad steht§8.");
            return;
        }

        userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {
            final long remaining = System.currentTimeMillis() - user.getLong(DataType.LASTLUCKYSPIN);

            if(remaining <= 86400000) {
                messageUtil.sendMessage(player, "Du musst noch " + MathUtil.getHoursAndMinutesAndSecondsFromSeconds((86400000-remaining)/1000) + "warten§8.");
                return;
            }

            user.setLong(DataType.LASTLUCKYSPIN, System.currentTimeMillis());
            messageUtil.sendMessage(player, "SPINS");
            // start spin.
        });
    }

    public void shutdown() {
        luckySpin.killAllStands();
    }

    public void setup() {
        if(luckySpin == null) {
            if (locationHandler.getLocation("luckyspin") != null)
                luckySpin = new LuckySpin(this, locationHandler.getLocation("luckyspin"));

            return;
        }

        luckySpin.setLocation(locationHandler.getLocation("luckyspin"));
        luckySpin.setup();
    }

    public ItemStack getRandomItem() {
        return items.get(new Random().nextInt(items.size() - 1));
    }

    public void setItems(final ArrayList<ItemStack> list) {
        items.clear();
        items = list;

        luckySpin.setup();
    }

}
