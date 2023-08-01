package de.obey.crownmc.commands;
/*

    Author - Obey -> CrownMc
       01.08.2023 / 04:41

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.backend.Rang;
import de.obey.crownmc.handler.RangHandler;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.stream.Collectors;

@RequiredArgsConstructor @NonNull
public final class RangCheckCommand implements CommandExecutor {

    private final MessageUtil messageUtil;
    private final RangHandler rangHandler;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player))
            return false;

        final Player player = (Player) sender;
        final User user = LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId());

        if(user.getNodes(NodeType.INHERITANCE)
                .stream()
                .filter(Node::hasExpiry)
                .filter(node -> !node.hasExpired())
                .collect(Collectors.toList()).size() == 0) {

            messageUtil.sendMessage(player, "Dein aktueller Rang ist Permanent§8.");
            return false;
        }

        for (InheritanceNode inheritanceNode : user.getNodes(NodeType.INHERITANCE)
                .stream()
                .filter(Node::hasExpiry)
                .filter(node -> !node.hasExpired())
                .collect(Collectors.toList())) {

            final Rang rang = rangHandler.getRangFromName(inheritanceNode.getGroupName());

            final long now = Instant.now().toEpochMilli();
            final long expire = inheritanceNode.getExpiry().toEpochMilli();

            messageUtil.sendMessage(player, "Dein aktueller Rang§8:§r " + rang.getShowprefix());
            messageUtil.sendMessage(player, "Verbleibende Zeit§8: §f§o" + MathUtil.getDaysAndHoursAndMinutesAndSecondsFromSeconds((expire - now) / 1000
            ));

            break;
        }
        
        

        return false;
    }



}
