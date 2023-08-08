package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       26.10.2022 / 17:57

*/

import de.obey.crownmc.objects.gambling.Giveaway;
import de.obey.crownmc.util.InventoryUtil;
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
public final class VerlosungCommand implements CommandExecutor {

    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if (!PermissionUtil.hasPermission(player, "verlosung", true))
            return false;

        if (args.length != 0) {
            messageUtil.sendSyntax(sender, "/verlosung");
            return true;
        }

        if (!InventoryUtil.hasItemInHand(player))
            return false;

        new Giveaway(player.getDisplayName(), player.getItemInHand(), Giveaway.AnimationType.NAME_ANIMATION, Giveaway.ShowSlot.ALL, Giveaway.Target.ONLY_USER).scheduleRun();

        return false;
    }
}
