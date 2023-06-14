package de.obey.crownmc.backend.user;
/*

    Author - Obey -> SkySlayer-v4
       02.01.2023 / 21:18

*/

import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class UserBank {

    private final User user;
    private final YamlConfiguration cfg;

    private final MessageUtil messageUtil;

    @Getter
    private long balance = 0;

    @Getter
    private final ArrayList<UUID> members = new ArrayList<>();

    @Getter
    private final ArrayList<UUID> trusted = new ArrayList<>();

    public UserBank(final User user) {
        this.user = user;
        messageUtil = user.getInitializer().getMessageUtil();
        cfg = user.getCfg();

        loadData();
    }

    private void loadData() {
        if(cfg.contains("balance"))
            balance = cfg.getLong("balance");

        if(cfg.contains("member")) {
            cfg.getStringList("member").forEach(uuid -> members.add(UUID.fromString(uuid)));
        }

        if(cfg.contains("trusted")) {
            cfg.getStringList("trusted").forEach(uuid -> members.add(UUID.fromString(uuid)));
        }

        if(members.isEmpty())
            members.add(user.getOfflinePlayer().getUniqueId());
    }

    public boolean isMember(final OfflinePlayer player) {
        return members.contains(player.getUniqueId());
    }

    public boolean isTrusted(final OfflinePlayer player) {
        return trusted.contains(player.getUniqueId());
    }

    public void addMember(final OfflinePlayer newMember) {
        members.add(newMember.getUniqueId());

        sendMessageToMembers(newMember.getName() + " hat jetzt Zugriff auf das Konto§8.");
    }

    public void removeMember(final OfflinePlayer newMember) {
        members.remove(newMember.getUniqueId());

        sendMessageToMembers(newMember.getName() + " hat jetzt keinen Zugriff auf das Konto§8.");
    }

    public void addTrusted(final OfflinePlayer newMember) {

        if(!members.contains(newMember.getUniqueId()))
            members.add(newMember.getUniqueId());

        trusted.add(newMember.getUniqueId());

        sendMessageToMembers(newMember.getName() + " wurde getrusted§8.");
    }

    public void removeTrusted(final OfflinePlayer newMember) {
        trusted.remove(newMember.getUniqueId());

        sendMessageToMembers(newMember.getName() + "ist jetzt nicht mehr trusted§8.");
    }

    public boolean withdraw(final Player player, final Long amount) {
        if(balance < amount) {
            player.sendMessage("§c§oAuf dem Konto ist nicht genug Geld§8. (§4-§c" + NumberFormat.getInstance().format(amount - balance) + "§8)");
            return false;
        }

        balance -= amount;
        sendMessageToMembers(player.getName() + " hat §4-§e" + NumberFormat.getInstance().format(amount) + "§6§l$§7 abgehoben§8.");

        return true;
    }

    public void deposit(final Player player, final Long amount) {
        balance += amount;

        sendMessageToMembers(player.getName() + " hat §a+§e" + NumberFormat.getInstance().format(amount) + "§6§l$§7 eingezahlt§8.");
    }

    public void save() {
        cfg.set("balance", balance);

        List<String> list = new ArrayList<>();
        members.forEach(uuid -> list.add(uuid.toString()));
        cfg.set("member", list);

        List<String> list2 = new ArrayList<>();
        trusted.forEach(uuid -> list2.add(uuid.toString()));
        cfg.set("trusted", list2);
    }

    private void sendMessageToMembers(final String message) {
        if(members.isEmpty())
            return;

        members.forEach(uuid -> {
            final Player player = Bukkit.getPlayer(uuid);

            if(player != null && player.isOnline()) {
                messageUtil.sendMessage(player, "Konto von§8:§e " + user.getOfflinePlayer().getName());
                messageUtil.sendMessage(player, message);
            }
        });
    }

}
