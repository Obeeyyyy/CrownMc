// Made by Richard


package de.obey.slayer.objects;

import de.obey.slayer.SlayerMain;
import de.obey.slayer.util.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Giveaway {

    private static Giveaway currentGiveAway;

    private final String creatorName;
    private final ItemStack reward;
    private final AnimationType animationType;
    private final ShowSlot showSlot;
    private final Target target;
    private Player winner;


    public Giveaway(String creatorName, ItemStack reward, AnimationType animationType, ShowSlot showSlot, Target target) {
        this.creatorName = creatorName;
        this.reward = reward.clone();
        this.animationType = animationType;
        this.showSlot = showSlot;
        this.target = target;
    }

    public void scheduleRun() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (currentGiveAway == null) {
                    currentGiveAway = Giveaway.this;
                    start();
                    cancel();
                }
            }
        }.runTaskTimer(SlayerMain.getInstance(), 0, 20);
    }

    private void start() {
        List<Player> possibleWinners = new ArrayList<>();
        switch (target) {
            case ONLY_TEAM:
                possibleWinners.addAll(Bukkit.getOnlinePlayers().stream().filter(player -> PermissionUtil.hasPermission(player, "team", false)).collect(Collectors.toList()));
                break;
            case ONLY_USER:
                possibleWinners.addAll(Bukkit.getOnlinePlayers().stream().filter(player -> !PermissionUtil.hasPermission(player, "team", false)).collect(Collectors.toList()));
                break;
            case ALL:
                possibleWinners.addAll(Bukkit.getOnlinePlayers());
        }
        if (possibleWinners.isEmpty()) {
            return;
        }
        winner = possibleWinners.get(new Random().nextInt(possibleWinners.size()));
        showReward();
        currentGiveAway = this;
    }

    private void showReward() {
        if (animationType == AnimationType.INSTANT) {

            if (showSlot != ShowSlot.ONLY_CHAT) {
                showTitle(100);
            }
            if (showSlot != ShowSlot.ONLY_TITLE) {
                showChatMessage(true);
            }
            applyReward();
            return;
        }

        if (animationType == AnimationType.NAME_ANIMATION) {
            new BukkitRunnable() {
                int stage = 1;
                int countdown = 3;

                @Override
                public void run() {
                    if (countdown != 0) {
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            player.sendMessage("§7Verlosung startet in§8: §e" + countdown);
                        });
                        countdown--;
                        return;
                    }

                    if (stage == 1) {
                        if (showSlot != ShowSlot.ONLY_TITLE) {
                            showChatMessage(false);
                        }
                    }
                    if (showSlot != ShowSlot.ONLY_CHAT) {
                        showTitle(stage);
                    }
                    stage++;
                    if (stage > winner.getDisplayName().length()) {
                        showTitle(100);
                        showChatMessage(true);
                        applyReward();
                        currentGiveAway = null;
                        this.cancel();
                    }
                }
            }.runTaskTimer(SlayerMain.getInstance(), 20, 20);
        }
    }

    private void showTitle(int stage) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle("§e§l" + winner.getDisplayName().substring(0, Math.min(stage, winner.getDisplayName().length())) + "§k" + (winner.getDisplayName().length() > stage ? winner.getDisplayName().substring(stage) : ""), "§7Gewinn§8: §e" + rewardText());
        });
    }

    private void showChatMessage(boolean endResult) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            for (int i = 0; i < 250; i++) {
                player.sendMessage("");
            }
            player.sendMessage("§7Verloser§8: §e");
            player.sendMessage("§e§l" + creatorName);
            player.sendMessage("");
            if (endResult) {
                player.sendMessage("§7Gewinner§8: §e");
                player.sendMessage("§e§l" + winner.getDisplayName());
                player.sendMessage("");
            }
            player.sendMessage("§7Gewinn§8: §e");
            player.sendMessage(rewardText());
            player.sendMessage("");
        });
    }

    private void applyReward() {
        winner.getInventory().addItem(reward);
        winner.updateInventory();
    }

    private String rewardText() {
        return "§4" + reward.getAmount() + "§8x §b" + (reward.getItemMeta().hasDisplayName() ? reward.getItemMeta().getDisplayName() : reward.getType().name().replace('_', ' '));
    }

    public enum AnimationType {

        INSTANT,
        NAME_ANIMATION;

    }

    public enum ShowSlot {

        ONLY_TITLE,
        ONLY_CHAT,
        ALL;

    }

    public enum Target {

        ONLY_USER,
        ONLY_TEAM,
        ALL;

    }

}
