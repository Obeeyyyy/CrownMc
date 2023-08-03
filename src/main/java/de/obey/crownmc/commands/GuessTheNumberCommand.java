// Made by Richard


package de.obey.crownmc.commands;

import com.google.common.primitives.Ints;
import de.obey.crownmc.CrownMain;
import de.obey.crownmc.util.InventoryUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

public class GuessTheNumberCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private ItemStack itemStack;
    private Integer score;
    private int taskId;
    private boolean alreadyStarted;

    public GuessTheNumberCommand(MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "guessthenumber", true))
            return false;

        if (args.length != 2) {
            messageUtil.sendSyntax(sender, "/guessthenumber <Lösung> <höchstmögliche Zahl>");
            return true;
        }

        if (alreadyStarted) {
            messageUtil.sendMessage(sender, "Es läuft bereits ein GuessTheNumber Event§8.");
            messageUtil.sendMessage(sender, "Die zu erratene Zahl ist §e§o" + this.score + "§8.");
        }

        Integer solution = Ints.tryParse(args[0]);
        if (solution == null || solution < 1) {
            messageUtil.sendMessage(sender, "Bitte gib eine Lösungszahl an, die größer oder gleich 1 ist.");
            return false;
        }

        Integer numberCap = Ints.tryParse(args[1]);
        if (numberCap == null || numberCap < 2) {
            messageUtil.sendMessage(sender, "Bitte gib als höchstmögliche Zahl an, die größer oder gleich 2 ist.");
            return false;
        }

        score = solution;
        alreadyStarted = true;

        messageUtil.broadcastNoPrefix("");
        messageUtil.broadcast(player.getName() + " Hat ein Event gestartet§8. §7Errate die §eZahl§7 von 1 §8- §7" + numberCap + "§8.");

        if (InventoryUtil.hasItemInHand(player, false)) {
            itemStack = player.getItemInHand().clone();
            messageUtil.broadcast("Der Gewinner erhält§8: §fx§e" + itemStack.getAmount() + " §f" + (itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : itemStack.getType().name().replace('_', ' ')));
        }

        messageUtil.broadcastNoPrefix("");

        taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(CrownMain.getInstance(), () -> {
            messageUtil.broadcastNoPrefix("");
            messageUtil.broadcast("Niemand hat die Zahl §8'§e" + score + "§8'§7 erraten§8.");
            messageUtil.broadcastNoPrefix("");
            score = null;
            itemStack = null;
            taskId = 0;
            alreadyStarted = false;
        }, 60 * 20);
        return false;
    }


    @EventHandler
    public void onChat(final AsyncPlayerChatEvent event) {
        if (score == null) return;
        if (!event.getMessage().equals(score.toString())) return;
        messageUtil.broadcastNoPrefix("");
        messageUtil.broadcastNoPrefix("");
        messageUtil.broadcast(event.getPlayer().getName() + " hat die Zahl §8'§e" + score + "§8'§7 erraten§8.");
        messageUtil.broadcastNoPrefix("");
        messageUtil.broadcastNoPrefix("");
        alreadyStarted = false;
        score = null;
        Bukkit.getScheduler().cancelTask(taskId);

        if (itemStack != null) {
            InventoryUtil.addItem(event.getPlayer(), itemStack);
            itemStack = null;
        }
    }
}