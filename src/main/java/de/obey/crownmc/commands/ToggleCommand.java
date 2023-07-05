package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       17.10.2022 / 17:15

*/

import de.obey.crownmc.util.Bools;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class ToggleCommand implements CommandExecutor {

    @NonNull
    private final MessageUtil messageUtil;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !PermissionUtil.hasPermission(sender, "toggle", true))
            return false;

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("pvp")) {
                if (Bools.pvp) {
                    Bools.pvp = false;
                    messageUtil.broadcast("PvP wurde §c§odeaktiviert§7.");
                    return false;
                }

                Bools.pvp = true;
                messageUtil.broadcast("PvP wurde §a§oaktiviert§7.");
                return false;
            }

            if (args[0].equalsIgnoreCase("drops")) {
                if (Bools.drop) {
                    Bools.drop = false;
                    messageUtil.broadcast("ItemDrops wurden §c§odeaktiviert§7.");
                    return false;
                }

                Bools.drop = true;
                messageUtil.broadcast("ItemDrops wurden §a§oaktiviert§7.");
                return false;
            }

            if (args[0].equalsIgnoreCase("pickup")) {
                if (Bools.pickup) {
                    Bools.pickup = false;
                    messageUtil.broadcast("ItemPickup wurde §c§odeaktiviert§7.");
                    return false;
                }

                Bools.pickup = true;
                messageUtil.broadcast("ItemPickup wurde §a§oaktiviert§7.");
                return false;
            }

            if (args[0].equalsIgnoreCase("ep")) {
                if (Bools.ep) {
                    Bools.ep = false;
                    messageUtil.broadcast("EnderPerlen wurde §c§odeaktiviert§7.");
                    return false;
                }

                Bools.ep = true;
                messageUtil.broadcast("EnderPerlen wurde §a§oaktiviert§7.");
                return false;
            }

            if (args[0].equalsIgnoreCase("potions")) {
                if (Bools.potions) {
                    Bools.potions = false;
                    messageUtil.broadcast("Potions wurde §c§odeaktiviert§7.");
                    return false;
                }

                Bools.potions = true;
                messageUtil.broadcast("Potions wurde §a§oaktiviert§7.");
                return false;
            }

            if (args[0].equalsIgnoreCase("pay")) {
                if (Bools.pay) {
                    Bools.pay = false;
                    messageUtil.broadcast("PayCommand wurde §c§odeaktiviert§7.");
                    return false;
                }

                Bools.pay = true;
                messageUtil.broadcast("PayCommand wurde §a§oaktiviert§7.");
                return false;
            }

            if (args[0].equalsIgnoreCase("nowl")) {
                if (Bools.nowl) {
                    Bools.nowl = false;
                    messageUtil.broadcast("NoWl Message wurde §c§odeaktiviert§7.");
                    return false;
                }

                Bools.nowl = true;
                messageUtil.broadcast("NoWl Message wurde §a§oaktiviert§7.");
                return false;
            }

            if (args[0].equalsIgnoreCase("doublexp")) {
                if (Bools.doubleXP) {
                    Bools.doubleXP = false;
                    messageUtil.broadcast("DoubleXP wurde §c§odeaktiviert§8.");
                    return false;
                }

                Bools.doubleXP = true;
                messageUtil.broadcast("DoubleXP wurde §a§oaktiviert§8.");
                return false;
            }
        }

        messageUtil.sendSyntax(sender, "/toggle <pvp, drops, ep, potions, pickup, pay, nowl, doublexp>");

        return false;
    }
}
