package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       17.01.2023 / 09:48

*/

import de.obey.crownmc.handler.BanHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.objects.punishment.BanReason;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor @NonNull
public final class BanCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final BanHandler banHandler;

    private final HashMap<String, Integer> banCounter = new HashMap<>();
    private final ArrayList<String> blocked = new ArrayList<>();

    private int ticks = 0;
    public void check() {
        if(ticks >= 60*10) {
            ticks = 0;
            banCounter.clear();
        }

        ticks++;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(command.getName().equalsIgnoreCase("ban")) {

            if(!PermissionUtil.hasPermission(sender, "team", true))
                return false;

            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("reasons")) {

                    if(banHandler.getReasons().isEmpty()) {
                        messageUtil.sendMessage(sender, "Es existieren noch keine ban Gründe§8.");
                        return false;
                    }

                    messageUtil.sendMessage(sender, "Es existieren §8(§f" + banHandler.getReasons().size() + "§8) §7Gründe§8:");

                    for (int i : banHandler.getReasons().keySet()) {
                        final BanReason reason = banHandler.getReadsonFromID(i);
                        sender.sendMessage("§8  - §7ID: §4§l" + i);
                        sender.sendMessage("§8     -> §7Name§8: §f" + reason.getName());
                        sender.sendMessage("§8     -> §7Duration§8: §f" + reason.getDuration() + "§8 (§f§o " + MathUtil.getDaysAndHoursAndMinutesAndSecondsFromSeconds(reason.getDuration()/1000) + " §8)");
                        sender.sendMessage("");
                    }

                    return false;
                }
            }

            if(args.length == 2) {

                if (blocked.contains(sender.getName())) {
                    messageUtil.sendMessage(sender, "Du hast zu viele Spieler gebannt :C machst du etwa was böses !?s");
                    messageUtil.sendMessageToTeamMembers("§8(§4§l!!!§8) §f§o" + sender.getName() + " hat viele Spieler gebannt!");
                    return false;
                }

                if(banCounter.containsKey(sender.getName()) && banCounter.get(sender.getName()) >= 3) {
                    blocked.add(sender.getName());
                    return false;
                }

                if(!messageUtil.hasPlayedBefore(sender, args[0]))
                    return false;

                try {
                    final int reasonID = Integer.parseInt(args[1]);

                    if (!banHandler.getReasons().containsKey(reasonID)) {
                        messageUtil.sendMessage(sender, "Ungültige grundID§8.");
                        return false;
                    }

                    final BanReason reason = banHandler.getReadsonFromID(reasonID);

                    if(!sender.hasPermission(reason.getPermission())) {
                        messageUtil.sendMessage(sender, "Du hast keine Rechte für diesen Grund§8.");
                        return false;
                    }

                    if(!banHandler.banPlayer(args[0], reasonID, sender.getName())) {
                        messageUtil.sendMessage(sender, args[0] + " kann nicht gebannt werden§8.");
                        return false;
                    }

                    if(!PermissionUtil.hasPermission(sender, "admin", false))
                        banCounter.put(sender.getName(), banCounter.containsKey(sender.getName()) ? banCounter.get(sender.getName()) + 1 : 1);

                } catch (final NumberFormatException exception) {
                    messageUtil.sendMessage(sender, "Ungültige grundID§8.");
                }
                return false;
            }

            messageUtil.sendSyntax(sender, "/ban <spieler> <grundID>",
                    "/ban reasons");
        }

        if(command.getName().equalsIgnoreCase("unban")) {

            if (sender instanceof Player && !PermissionUtil.hasPermission(sender, "unban", true))
                return false;

            if(args.length == 1) {

                if(!messageUtil.hasPlayedBefore(sender, args[0]))
                    return false;

                final OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

                banHandler.unBanPlayer(sender, target);

                return false;
            }

            messageUtil.sendSyntax(sender, "/unban <spieler>");
        }

        return false;
    }
}
