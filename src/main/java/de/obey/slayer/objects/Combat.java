package de.obey.slayer.objects;
/*

    Author - Obey -> SkySlayer-v4
       24.10.2022 / 14:58

*/

import de.obey.slayer.Initializer;
import de.obey.slayer.SlayerMain;
import de.obey.slayer.handler.CombatHandler;
import de.obey.slayer.util.MathUtil;
import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public final class Combat {

    private final Initializer initializer = SlayerMain.getInstance().getInitializer();
    private final CombatHandler combatHandler = initializer.getCombatHandler();

    @Getter
    private String durationString = "0s";

    private final Player player;

    @Getter
    private Player opponent;

    private final long startMillis = System.currentTimeMillis();

    @Getter
    private int cooldown = 11;

    private final BukkitTask runnable;

    public Combat(final Player player) {
        this.player = player;

        combatHandler.putInCombatCache(player, this);
        initializer.getScoreboardHandler().updateScoreboard(player);

        if (player.getGameMode() == GameMode.SURVIVAL) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }

        runnable = new BukkitRunnable() {

            int tick = 0;

            @Override
            public void run() {
                if (tick == 2) {
                    cooldown--;
                    durationString = MathUtil.getMinutesAndSecondsFromSeconds((System.currentTimeMillis() - startMillis) / 1000);
                    tick = 0;
                }

                if (durationString.equalsIgnoreCase(""))
                    durationString = "0s";

                initializer.getScoreboardHandler().updateScoreboard(player);

                if (cooldown <= 0)
                    end();

                tick++;
            }
        }.runTaskTimer(SlayerMain.getInstance(), 10, 10);
    }

    public void end() {
        if (runnable != null)
            runnable.cancel();

        combatHandler.endCombat(player);
        initializer.getScoreboardHandler().updateScoreboard(player);
    }

    public void hit(Player player) {
        cooldown = 11;
        opponent = player;
    }

    public void logout() {

        for (ItemStack content : player.getInventory().getContents()) {
            if (content != null && content.getType() != Material.AIR) {
                player.getWorld().dropItem(player.getLocation(), content.clone());
            }
        }

        for (ItemStack content : player.getInventory().getArmorContents()) {
            if (content != null && content.getType() != Material.AIR) {
                player.getWorld().dropItem(player.getLocation(), content.clone());
            }
        }

        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        combatHandler.putInLoggerCache(player);
        initializer.getMessageUtil().sendMessageToTeamMembers("§6§lCOMBATLOG §8> §e§o" + player.getName() + "§7 hat sich im Kampf ausgeloggt§8.");
    }

}
