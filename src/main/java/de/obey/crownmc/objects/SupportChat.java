package de.obey.crownmc.objects;
/*

    Author - Obey -> SkySlayer-v4
       25.10.2022 / 14:40

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.commands.SupportCommand;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public final class SupportChat {

    private final MessageUtil messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();

    @Getter
    final Player owner;
    @Getter
    final String grund;

    @Getter
    int state = 0; // 0 open- 1 wird bearbeitet

    @Getter
    private final ArrayList<Player> member = new ArrayList<>();

    public SupportChat(final Player player, final String grund) {
        owner = player;
        this.grund = grund;
        member.add(player);

        player.sendMessage("");
        player.sendMessage("");
        player.sendMessage(prefix + "Deine Supportanfrage wurde erstellt, sobald ein Teammitglied Zeit hat wird sie angenommen.");
        player.sendMessage("");
        player.sendMessage("");

        messageUtil.sendMessageToTeamMembers("§8» §7" + player.getName() + " hat eine Supportanfrage erstellt. Grund§8: §f§o" + grund);
        messageUtil.sendHoverTextCommandToTeamMembers("§a§oKlicke hier um sie anzunehmen", "/support join " + player.getName());
    }

    public void join(final Player teammitglied) {
        member.add(teammitglied);
        state = 1;

        sendMessageToMemebers("");
        sendMessageToMemebers(teammitglied.getName() + " hat den Chat betreten§8.");
        sendMessageToMemebers("");
    }

    public void leave(final Player teammitglied) {
        sendMessageToMemebers("");
        sendMessageToMemebers(teammitglied.getName() + " hat den Chat verlassen§8.");
        sendMessageToMemebers("");

        member.remove(teammitglied);
        SupportCommand.openChats.remove(teammitglied.getUniqueId());
    }

    private final String prefix = "§8[§9§lSUPPORTCHAT§8] §7";

    public void sendMessageToMemebers(final String message) {
        member.forEach(member -> {
            member.sendMessage(prefix + message);
            member.playSound(member.getLocation(), Sound.CLICK, 0.5f, 10);
        });
    }

    public void close(final Player player) {
        sendMessageToMemebers(player.getName() + " hat den Chat geschlossen§8.");
        member.forEach(member -> SupportCommand.openChats.remove(member.getUniqueId()));
    }

}
