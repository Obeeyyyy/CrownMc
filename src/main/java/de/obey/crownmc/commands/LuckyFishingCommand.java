package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       15.10.2022 / 14:13

*/

import de.obey.crownmc.handler.LuckyFishingHandler;
import de.obey.crownmc.handler.WorldProtectionHandler;
import de.obey.crownmc.objects.luckyfishing.RewardLevel;
import de.obey.crownmc.objects.luckyfishing.RodLevel;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class LuckyFishingCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final LuckyFishingHandler luckyFishingHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "fly", true))
            return false;

        if (args.length == 0) {
            messageUtil.sendSyntax(sender, "/lf setrewards <rewardlevel>", "/lf getrod <rodlevel>", "/lf rewardlevels", "/lf rodlevels");
            return false;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("rewardlevels")) {
                messageUtil.sendMessage(player, "§7Liste der RewardLevel§8:");
                for (RewardLevel rewardLevel : RewardLevel.values()) {
                    messageUtil.sendMessage(player, "§8- " + rewardLevel.getDisplayName() + "§8 (§f" + rewardLevel.name().toLowerCase() + "§8)");
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("rodlevels")) {
                messageUtil.sendMessage(player, "§7Liste der RodLevel§8:");
                for (RodLevel rewardLevel : RodLevel.values()) {
                    messageUtil.sendMessage(player, "§8- " + rewardLevel.getDisplayName() + "§8 (§f" + rewardLevel.name().toLowerCase() + "§8)");
                }
                return true;
            }
            messageUtil.sendSyntax(sender, "/lf setrewards <rewardlevel>", "/lf getrod <rodlevel>", "/lf rewardlevels", "/lf rodlevels");
            return false;
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("getrod")) {
                try {
                    RodLevel rodLevel = RodLevel.valueOf(args[1]);
                    player.getInventory().addItem(luckyFishingHandler.getRod(rodLevel));
                    messageUtil.sendMessage(player, "§7Du hast die Rod mit Level §f" + rodLevel.getDisplayName() + "§7 erhalten§8.");
                    return true;
                } catch (IllegalArgumentException e) {
                    messageUtil.sendMessage(player, "Gebe ein richtiges RodLevel an§8.");
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("setrewards")) {
                try {
                    RewardLevel rewardLevel = RewardLevel.valueOf(args[1]);
                    luckyFishingHandler.editFishingRewards(player, rewardLevel);
                    return true;
                } catch (IllegalArgumentException e) {
                    messageUtil.sendMessage(player, "Gebe ein richtiges RodLevel an§8.");
                    return false;
                }
            }
            messageUtil.sendSyntax(sender, "/lf setrewards <rewardlevel>", "/lf getrod <rodlevel>", "/lf rewardlevels", "/lf rodlevels");
            return false;
        }
        messageUtil.sendSyntax(sender, "/lf setrewards <rewardlevel>", "/lf getrod <rodlevel>", "/lf rewardlevels", "/lf rodlevels");
        return false;
    }
}
