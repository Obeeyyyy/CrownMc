package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       26.10.2022 / 22:42

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.handler.GoalHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.objects.Goal;
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

@RequiredArgsConstructor
@NonNull
public final class GoalCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;
    private final GoalHandler goalHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {

            if (!(sender instanceof Player)) {
                messageUtil.sendSyntax(sender, "/goal start <goal>", "/goal end", "/goal pay <einsatz>");
                return false;
            }

            messageUtil.sendSyntax(sender, "/goal start <goal>", "/goal end", "/goal pay <einsatz>");
            return false;
        }

        final Player player = (Player) sender;

        if (args.length == 1) {

            if (args[0].equalsIgnoreCase("end")) {
                if (!PermissionUtil.hasPermission(player, "goal", true))
                    return false;

                goalHandler.endGoal(goalHandler.getGoal());
                messageUtil.sendMessage(sender, "§7Goal wurde beendet§8.");
                return true;
            }
            messageUtil.sendSyntax(sender, "/goal start <goal>", "/goal end", "/goal pay <einsatz>");
            return false;
        }

        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("start")) {
                if (!PermissionUtil.hasPermission(player, "goal", true))
                    return false;

                long goalAmount = MathUtil.getLongFromStringwithSuffix(args[1]);

                goalHandler.startNewGoal(new Goal(player, goalAmount));
                messageUtil.sendMessage(sender, "§7Goal wurde gestartet§8.");
                return true;
            }

            if (args[0].equalsIgnoreCase("pay")) {

                long einsatz = MathUtil.getLongFromStringwithSuffix(args[1]);

                if (goalHandler.getGoal().joinPlayer(userHandler.getUserInstant(player.getUniqueId()), einsatz)) {
                    messageUtil.sendMessage(sender, "§7Du hast in das Goal eingezahlt§8.");
                    if (!(goalHandler.getGoal().getCurrentAmount() >= goalHandler.getGoal().getGoal()))
                        messageUtil.sendMessage(sender, "§7Es fehlen noch §a" + messageUtil.formatLong(goalHandler.getGoal().getGoal() - goalHandler.getGoal().getCurrentAmount()) + "§2§o$§8.");
                    return true;
                }
                messageUtil.sendMessage(sender, "§7Dazu hast du nicht genug Geld§8.");
                return true;
            }
            messageUtil.sendSyntax(sender, "/goal start <goal>", "/goal end", "/goal pay <einsatz>");
            return false;

        }

        return false;
    }
}
