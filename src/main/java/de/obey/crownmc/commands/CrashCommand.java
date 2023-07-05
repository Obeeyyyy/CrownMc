package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       16.12.2022 / 22:19

*/

import de.obey.crownmc.handler.CrashHandler;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@RequiredArgsConstructor
@NonNull
public final class CrashCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final CrashHandler crashHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("leave")) {
                crashHandler.getCrash().leaveCrash(player);
                return false;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("join")) {

                long amount = 0L;

                try {
                    amount = Long.parseLong(args[1]);

                    if (amount <= 0) {
                        messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an die größer als 0 ist.");
                        return false;
                    }

                } catch (final NumberFormatException exception) {

                    amount = MathUtil.getLongFromStringwithSuffix(args[1]);

                    if (amount <= 0) {
                        messageUtil.sendMessage(sender, "Bitte gebe eine Zahl an, oder benutze folgende Abkürzungen. (k, m, mrd, b, brd, t)");
                        return false;
                    }
                }

                if (amount > 100000000) {
                    messageUtil.sendMessage(player, "Bitte gebe einen Betrag an der keiner als 100 Millionen ist§8.");
                    return false;
                }

                crashHandler.getCrash().joinCrash(player, amount);

                return false;
            }
        }

        messageUtil.sendSyntax(sender, "/crash join <amount>", "/crash leave");

        return false;
    }

    @EventHandler
    public void signChange(final SignChangeEvent event) {
        if(!PermissionUtil.hasPermission(event.getPlayer(), "admin", false))
            return;

        if(event.getLine(0).startsWith("crash")) {
            event.setLine(0, "§8▰§7▱ §a§lCrash §7▱§8▰");
            event.setLine(1, "Klicke hier um");
            event.setLine(2,"beizutreten oder");
            event.setLine(3,"zu verlassen§8.");
        }
    }

    @EventHandler
    public void signInteract(final PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(!event.getClickedBlock().getType().name().contains("SIGN"))
            return;

        final Sign sign = (Sign) event.getClickedBlock().getState();
        final Player player = event.getPlayer();

        if(sign.getLine(0).equalsIgnoreCase("§8▰§7▱ §a§lCrash §7▱§8▰")) {
            if (crashHandler.getJoiningCrash().contains(player)) {
                messageUtil.sendMessage(player, "Schreibe den §a§ogewünschten Betrag§7 in den Chat§8.");
                messageUtil.sendMessage(player, "Schreibe §c§ocancel§7 um den Vorgang abzubrechen§8.");
                return;
            }

            if(crashHandler.getCrash().getBets().containsKey(player.getUniqueId())) {
                crashHandler.getCrash().leaveCrash(player);
                return;
            }

            crashHandler.getJoiningCrash().add(player);
            messageUtil.sendMessage(player, "Schreibe den §a§ogewünschten Betrag§7 in den Chat§8.");
            messageUtil.sendMessage(player, "Schreibe §c§ocancel§7 um den Vorgang abzubrechen§8.");
            player.playSound(player.getLocation(), Sound.STEP_LADDER, 0.5f, 1);
        }
    }
}
