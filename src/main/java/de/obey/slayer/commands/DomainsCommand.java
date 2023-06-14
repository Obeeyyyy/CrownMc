package de.obey.slayer.commands;
/*

    Author - Obey -> SkySlayer-v4
       22.10.2022 / 01:38

*/

import de.obey.slayer.backend.ServerConfig;
import de.obey.slayer.util.MessageUtil;
import de.obey.slayer.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@NonNull
public final class DomainsCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final ServerConfig serverConfig;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player && !PermissionUtil.hasPermission((Player) sender, "domains", true))
            return false;

        if (args.length == 0) {
            messageUtil.sendMessage(sender, "Informationen über Domains.");

            serverConfig.getDomainJoins().keySet().forEach(domain -> {
                sender.sendMessage("§8┃> §o§e" + domain.replace(",", ".") + " §8× §e" + serverConfig.getDomainJoins().get(domain) + " Connected.");
            });

            return false;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reset")) {
                serverConfig.getDomainJoins().clear();
                serverConfig.save();
                messageUtil.sendMessage(sender, "Statistiken wurden zurückgesetzt.");
                return false;
            }
        }

        return false;
    }
}
