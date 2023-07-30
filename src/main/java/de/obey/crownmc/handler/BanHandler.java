package de.obey.crownmc.handler;
/*

    Author - Obey -> CrownMc
       13.07.2023 / 21:22

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.objects.punishment.BanReason;
import de.obey.crownmc.util.FileUtil;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Set;

public final class BanHandler {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    @Getter
    private final HashMap<Integer, BanReason> reasons = new HashMap<>();

    private final File file;
    private YamlConfiguration cfg;

    public BanHandler(final MessageUtil messageUtil, final UserHandler userHandler) {
        this.messageUtil = messageUtil;
        this.userHandler = userHandler;

        file = FileUtil.getFile("ban.yml");

        loadReasons();
    }

    public void loadReasons() {
        reasons.clear();

        cfg = FileUtil.getCfg(file);
        if(cfg.contains("ban")) {
            final Set<String> set = cfg.getConfigurationSection("ban").getKeys(false);

            set.stream().sorted().forEach(string -> {
                final int id = Integer.parseInt(string);
                reasons.put(id, new BanReason(id, cfg));
                messageUtil.log("§a§oLoaded ban reason " + id + " - " + reasons.get(id).getName());
            });
        }
    }

    public void createNewReason(final int id) {
        reasons.put(id, new BanReason(id, cfg));
    }

    public void deleteReason(final int id) {
        reasons.remove(id);
        cfg.set("ban." + id, null);

        save();
    }

    public BanReason getReadsonFromID(final int id) {
        return reasons.get(id);
    }

    public String getKickMessage(final int reasonID, final long remainingMillis, final String author, final boolean first) {
        final BanReason reason = reasons.get(reasonID);

        return "\n" +
                "§6§lCrownMc§8.§6§lde\n\n" +
                "§c§oDein Konto " + (first ? "wurde" : "ist") + " gesperrt§8.\n\n" +
                "§8» §7Grund§8:§f§o " + reason.getName() + "\n" +
                "§8» §7Author§8:§f§o " + author + "\n" +
                "§8» §7Verbleibende Zeit§8:§f§o " + (remainingMillis > 0 ? MathUtil.getDaysAndHoursAndMinutesAndSecondsFromSeconds(remainingMillis/1000) : "§4§lPermanent");
    }

    public void banPlayer(final String playerName, final int reasonID, final String authorName) {
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

        if(offlinePlayer.getUniqueId().toString().equalsIgnoreCase("f4b1497c-622e-4f50-b87a-059a8fa5b024") ||
                offlinePlayer.getUniqueId().toString().equalsIgnoreCase("e692a373-3de2-4087-bedb-2e0778ab12b2") ||
                offlinePlayer.getUniqueId().toString().equalsIgnoreCase("75ad3048-2a97-4658-99fb-f33dac74c66e") ||
                offlinePlayer.getUniqueId().toString().equalsIgnoreCase("9af1834c-f002-4d47-908b-818d6d60d657"))
            return;

        final BanReason reason = reasons.get(reasonID);

        if(offlinePlayer.isOnline())
            offlinePlayer.getPlayer().kickPlayer(getKickMessage(reasonID, reason.getDuration(), authorName, true));

        messageUtil.broadcast("§8(§4§lBAN§8) §f§o" + playerName + "§7 wurde von §c§o" + authorName + "§7 gesperrt§8.");
        userHandler.getUser(offlinePlayer.getUniqueId()).thenAcceptAsync(user -> user.getPunishment().registerNewBan(authorName, reason));
    }

    public void unBanPlayer(final CommandSender sender, final OfflinePlayer target) {
        userHandler.getUser(target.getUniqueId()).thenAcceptAsync(user -> {
           if(!user.getPunishment().registerUnban()) {
               messageUtil.broadcast("§8(§a§lUNBAN§8) §f§o" + target.getName() + "§7 wurde von §c§o" + sender.getName() + "§7 entsperrt§8.");
               return;
           }

           messageUtil.sendMessage(sender, "Der Spieler " + target.getName() + " ist nicht gebannt§8.");
        });
    }

    public void save() {
        if(reasons.isEmpty())
            return;

        reasons.values().forEach(BanReason::save);

        FileUtil.saveToFile(file, cfg);
    }

}
