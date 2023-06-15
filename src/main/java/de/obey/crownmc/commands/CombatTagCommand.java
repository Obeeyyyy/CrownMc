package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       25.10.2022 / 23:14

*/

import de.obey.crownmc.Initializer;
import de.obey.crownmc.objects.pvp.Combat;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class CombatTagCommand implements CommandExecutor {

    @NonNull
    private final Initializer initializer;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;
        final Combat combat = initializer.getCombatHandler().isInCombat(player);

        if (combat != null) {
            initializer.getMessageUtil().sendMessage(sender, "§c§oDu bist im Kampf§8.");
            initializer.getMessageUtil().sendMessage(sender, "Du kannst dich in §a§o" + combat.getCooldown() + "s§7 ausloggen§8.");
            return false;
        }

        initializer.getMessageUtil().sendMessage(sender, "§a§oDu bist nicht im Kampf§8.");

        return false;
    }
}
