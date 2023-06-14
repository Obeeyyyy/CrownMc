package de.obey.slayer.backend.user;
/*

    Author - Obey -> SkySlayer-v4
       24.11.2022 / 13:29

*/

import de.obey.slayer.objects.Badge;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.time.LocalDateTime;

@Getter
public final class UserBadge {

    private final Badge badge;
    private final String receivedDate;

    public UserBadge(final Badge badge, final YamlConfiguration cfg) {
        this.badge = badge;

        if (cfg.contains("badges." + badge.getName() + ".receiveddate")) {
            receivedDate = ChatColor.translateAlternateColorCodes('&', cfg.getString("badges." + badge.getName() + ".receiveddate"));
        } else {
            final LocalDateTime date = LocalDateTime.now();
            receivedDate = date.getDayOfMonth() + "." + date.getMonthValue() + "." + date.getYear() + " " + date.getHour() + ":" + date.getMinute();
        }
    }
}
