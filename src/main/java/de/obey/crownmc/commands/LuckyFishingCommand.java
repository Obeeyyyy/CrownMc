package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       15.10.2022 / 14:13

*/

import de.obey.crownmc.handler.LuckyFishingHandler;
import de.obey.crownmc.objects.luckyfishing.RewardLevel;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

        if (!PermissionUtil.hasPermission(player, "admin", true))
            return false;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                messageUtil.sendMessage(player, "§7Liste der RewardLevel§8:");
                for (RewardLevel rewardLevel : RewardLevel.values()) {
                    messageUtil.sendMessage(player, "§8- " + rewardLevel.getDisplayName() + "§8 (§f" + rewardLevel.name().toLowerCase() + "§8)");
                }
                return false;
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                try {
                    final RewardLevel rewardLevel = RewardLevel.getOrDefault(args[1], RewardLevel.COMMON);
                    luckyFishingHandler.editFishingRewards(player, rewardLevel);
                    return false;
                } catch (IllegalArgumentException e) {
                    messageUtil.sendMessage(player, "Gebe ein richtiges Reward Level an§8.");
                    return false;
                }
            }
        }
        messageUtil.sendSyntax(sender,
                "/lf set <rewardlevel>",
                "/lf list");
        return false;
    }
}
