package de.obey.crownmc.commands;

import de.obey.crownmc.handler.NpcHandler;
import de.obey.crownmc.objects.CNPC;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor @NonNull
public class NpcCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final NpcHandler npcHandler;

    private final HashMap<UUID, CNPC> selected = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        if(!PermissionUtil.hasPermission(sender, "admin", true))
            return false;

        final Player player = (Player) sender;

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("select")) {

                if(!npcHandler.getNpcs().containsKey(args[1])) {
                    messageUtil.sendMessage(sender, "Es existiert kein NPC mit dem Namen " + args[1] + "§8.");
                    return false;
                }

                selected.put(player.getUniqueId(), npcHandler.getNpcs().get(args[1]));
                messageUtil.sendMessage(sender, args[1] + " ausgewählt§8");

                return false;
            }

            if(args[0].equalsIgnoreCase("create")) {

                if(!npcHandler.createNewNPC(args[1], player.getLocation())) {
                    messageUtil.sendMessage(sender, "Es exisiert bereits ein NPC mit dem Namen " + args[1] + "§8.");
                    return false;
                }

                messageUtil.sendMessage(sender,args[1] + " wurde erstellt§8.");

                return false;
            }

            if(args[0].equalsIgnoreCase("delete")) {

                if(!npcHandler.deleteNPC(args[1])) {
                    messageUtil.sendMessage(sender, "Es exisiert kein NPC mit dem Namen " + args[1] + "§8.");
                    return false;
                }

                messageUtil.sendMessage(sender,args[1] + " wurde gelöscht§8.");

                return false;
            }
        }

        messageUtil.sendSyntax(sender,
                "/npc select <name>",
                "/npc create <name>",
                "/npc delete <name>",
                "/npc sp <name>",
                "/npc helm <name>",
                "/npc brust <name>",
                "/npc hose <name>",
                "/npc boots <name>",
                "",
                "",
                ""
        );

        return false;
    }
}
