package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       14.01.2023 / 21:33

*/

import de.obey.crownmc.handler.DailyPotHandler;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor @NonNull
public final class DailyPotCommand implements CommandExecutor, Listener {

    private final MessageUtil messageUtil;
    private final DailyPotHandler dailyPotHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("join")) {
                dailyPotHandler.join(player);
                return false;
            }

            if(!PermissionUtil.hasPermission(player, "dailypot.edit", true))
                return false;

            if(args[0].equalsIgnoreCase("open")) {
                dailyPotHandler.open(player);
                return false;
            }
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("setpayin")) {
                if(!PermissionUtil.hasPermission(player, "dailypot.edit", true))
                    return false;

                try {

                    final long amount = Long.parseLong(args[1]);

                    dailyPotHandler.setPayinAmount(amount);
                    messageUtil.sendMessage(player, "Der Payin Betrag für den Dailypot wurde auf §e" + messageUtil.formatLong(amount) + " §7gesetzt§8.");
                }catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }

            if(args[0].equalsIgnoreCase("setopen")) {
                if(!PermissionUtil.hasPermission(player, "dailypot.edit", true))
                    return false;

                try {

                    final long amount = Long.parseLong(args[1]);

                    dailyPotHandler.setEndMillis(System.currentTimeMillis() + amount);
                    messageUtil.sendMessage(player, "Endmillis wurde auf §e" + (System.currentTimeMillis() + amount) + " §7gesetzt§8.");

                }catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }

            if(args[0].equalsIgnoreCase("setpot")) {
                if(!PermissionUtil.hasPermission(player, "dailypot.edit", true))
                    return false;

                try {
                    final long amount = Long.parseLong(args[1]);

                    dailyPotHandler.setMoneyinPot(amount);
                    messageUtil.sendMessage(player, "Pot Inhalt wurde auf §e" + messageUtil.formatLong(amount) + " §7gesetzt§8.");

                }catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }

            if(args[0].equalsIgnoreCase("addpot")) {
                if(!PermissionUtil.hasPermission(player, "dailypot.edit", true))
                    return false;

                try {
                    final long amount = Long.parseLong(args[1]);

                    dailyPotHandler.setMoneyinPot(dailyPotHandler.getMoneyinPot() + amount);
                    messageUtil.sendMessage(player, "Pot Inhalt wurde auf §e" + messageUtil.formatLong(dailyPotHandler.getMoneyinPot()) + " §7gesetzt§8.");

                }catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(player, "Bitte gebe eine Zahl an§8.");
                }

                return false;
            }
        }

        if(!PermissionUtil.hasPermission(player, "dailypot.edit", false)) {
            messageUtil.sendSyntax(sender, "/dailypot join");
            return false;
        }

        messageUtil.sendSyntax(sender, "/dailypot join" ,
                "/dailypot open",
                "/dailypot setpayin <amount>",
                "/dailypot setopen <+millis>",
                "/dailypot setpot <amount>",
                "/dailypot addpot <amount>"
        );

        return false;
    }

    @EventHandler
    public void on(final SignChangeEvent event) {
        if(!PermissionUtil.hasPermission(event.getPlayer(), "admin", false))
            return;

        if(event.getLine(0).startsWith("dp")) {
            event.setLine(0, "§8▰§7▱ §9DailyPot §7▱§8▰");
            event.setLine(2, "Klicke hier um");
            event.setLine(3,"beizutreten§8.");
        }
    }

    @EventHandler
    public void on(final PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(!event.getClickedBlock().getType().name().contains("SIGN"))
            return;

        final Sign sign = (Sign) event.getClickedBlock().getState();

        if(sign.getLine(0).equalsIgnoreCase("§8▰§7▱ §9DailyPot §7▱§8▰"))
            dailyPotHandler.join(event.getPlayer());
    }
}
