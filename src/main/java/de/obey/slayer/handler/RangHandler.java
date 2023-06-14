package de.obey.slayer.handler;
/*

    Author - Obey -> SkySlayer-v4
       14.10.2022 / 18:35

*/

import com.google.common.collect.Maps;
import de.obey.slayer.SlayerMain;
import de.obey.slayer.backend.Rang;
import de.obey.slayer.util.MessageUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.UserManager;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public final class RangHandler {

    @NonNull
    @Getter(AccessLevel.NONE)
    private final MessageUtil messageUtil;

    private final Map<String, Rang> groupMap = Maps.newConcurrentMap();

    public void loadRangs() {
        final File file = new File(SlayerMain.getInstance().getDataFolder().getPath() + "/Groups");

        if (!file.exists())
            file.mkdir();

        if (file.listFiles() != null) {
            for (final File rangFile : file.listFiles()) {
                groupMap.put(rangFile.getName().replace(".yml", ""), new Rang(rangFile));
            }
        }
    }

    public void createRang(final String name) {
        final File file = new File(SlayerMain.getInstance().getDataFolder().getPath() + "/Groups/" + name + ".yml");

        groupMap.put(name, new Rang(file));
    }

    public void deleteRang(final String name) {
        final File file = new File(SlayerMain.getInstance().getDataFolder().getPath() + "/Groups/" + name + ".yml");

        groupMap.remove(name);
        file.delete();
    }

    public void save() {
        groupMap.values().forEach(Rang::save);
    }

    public Rang getPlayerRang(final Player player) {
        return groupMap.get(LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId()).getPrimaryGroup());
    }

    public Rang getRangFromName(final String name) {
        return groupMap.get(name);
    }

    public Rang getRangFromShowPrefix(final String showprefix) {

        for (final Rang rang : groupMap.values()) {
            if (rang.getShowprefix().equalsIgnoreCase(showprefix))
                return rang;
        }

        return null;
    }

    public void setPlayerRang(final OfflinePlayer target, final String groupName, final CommandSender sender) {
        final LuckPerms luckPerms = LuckPermsProvider.get();
        final Group group = luckPerms.getGroupManager().getGroup(groupName);

        if (group == null) {
            messageUtil.sendMessage(sender, "Die Gruppe " + groupName + " existiert nicht.");
            return;
        }

        final UserManager userManager = luckPerms.getUserManager();

        userManager.modifyUser(target.getUniqueId(), user -> {
            user.data().clear(NodeType.INHERITANCE::matches);
            user.data().add(InheritanceNode.builder(group).build());

            userManager.saveUser(user);

            if (target.isOnline())
                messageUtil.sendMessage(target.getPlayer(), "Dein Rang wurde geupdated.");

            messageUtil.sendMessage(sender, target.getName() + " ist jetzt " + groupName + "§8.");
        });
    }

}
