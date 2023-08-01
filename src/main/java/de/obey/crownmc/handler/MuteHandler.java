package de.obey.crownmc.handler;
/*

    Author - Obey -> CrownMc
       13.07.2023 / 21:22

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.objects.punishment.MuteReason;
import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

public final class MuteHandler {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    @Getter
    private final HashMap<Integer, MuteReason> reasons = new HashMap<>();

    private final File file;
    private YamlConfiguration cfg;

    public MuteHandler(final MessageUtil messageUtil, final UserHandler userHandler) {
        this.messageUtil = messageUtil;
        this.userHandler = userHandler;

        file = FileUtil.getFile("mute.yml");

        loadReasons();
    }

    public void loadReasons() {
        reasons.clear();

        cfg = FileUtil.getCfg(file);
        if(cfg.contains("mute")) {
            final Set<String> set = cfg.getConfigurationSection("mute").getKeys(false);

            set.stream().sorted().forEach(string -> {
                final int id = Integer.parseInt(string);
                reasons.put(id, new MuteReason(id, cfg));
                messageUtil.log("§a§oLoaded mute reason " + id + " - " + reasons.get(id).getName());
            });
        }
    }

    public void createNewReason(final int id) {
        reasons.put(id, new MuteReason(id, cfg));
    }

    public void deleteReason(final int id) {
        reasons.remove(id);
        cfg.set("mute." + id, null);

        save();
    }

    public MuteReason getReadsonFromID(final int id) {
        return reasons.get(id);
    }

    public boolean mutePlayer(final String playerName, final int reasonID, final String authorName) {
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

        if(offlinePlayer.getUniqueId().toString().equalsIgnoreCase("f4b1497c-622e-4f50-b87a-059a8fa5b024") ||
                offlinePlayer.getUniqueId().toString().equalsIgnoreCase("e692a373-3de2-4087-bedb-2e0778ab12b2") ||
                offlinePlayer.getUniqueId().toString().equalsIgnoreCase("75ad3048-2a97-4658-99fb-f33dac74c66e") ||
                offlinePlayer.getUniqueId().toString().equalsIgnoreCase("9af1834c-f002-4d47-908b-818d6d60d657"))
            return false;

        final MuteReason reason = reasons.get(reasonID);

        messageUtil.broadcast("§8(§4§lMUTE§8) §f§o" + playerName + "§7 wurde von §c§o" + authorName + "§7 gemutet§8.");
        userHandler.getUser(offlinePlayer.getUniqueId()).thenAcceptAsync(user -> user.getPunishment().registerNewMute(authorName, reason));

        return true;
    }

    public void unMutePlayer(final CommandSender sender, final OfflinePlayer target) {
        userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
            if(user.getPunishment().registerUnmute()) {
                messageUtil.broadcast("§8(§a§lUNMUTE§8) §f§o" + target.getName() + "§7 wurde von §c§o" + sender.getName() + "§7 entmutet§8.");
                return;
            }

            messageUtil.sendMessage(sender, "Der Spieler " + target.getName() + " ist nicht gemutet§8.");
        });
    }

    public void save() {
        if(reasons.isEmpty())
            return;

        reasons.values().forEach(MuteReason::save);

        FileUtil.saveToFile(file, cfg);
    }

}
