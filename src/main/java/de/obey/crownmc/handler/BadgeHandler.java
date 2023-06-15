package de.obey.crownmc.handler;
/*

    Author - Obey -> SkySlayer-v4
       20.11.2022 / 14:53

*/

import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.objects.Badge;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public final class BadgeHandler {

    @NonNull
    private final ServerConfig serverConfig;

    @Getter
    private final Map<String, Badge> badgeMap = new HashMap<>();

    public void createNewBadge(final String name) {
        final Badge badge = new Badge(name, serverConfig.getCfg());
        badgeMap.put(name, badge);

        final LocalDateTime date = LocalDateTime.now();

        badge.setCreationDate(date.getDayOfMonth() + "." + date.getMonthValue() + "." + date.getYear() + " " + date.getHour() + ":" + date.getMinute());
    }

    public void deleteBadge(final String name) {
        badgeMap.remove(name);
        serverConfig.getCfg().set("badges." + name, null);
    }

    public void loadBadges() {
        if (!serverConfig.getCfg().contains("badges"))
            return;

        final Set<String> badgeSet = serverConfig.getCfg().getConfigurationSection("badges").getKeys(false);

        badgeSet.forEach(name -> badgeMap.put(name, new Badge(name, serverConfig.getCfg())));

        if(!badgeSet.contains("ks10"))
            createNewBadge("ks10");

        if(!badgeSet.contains("ks25"))
            createNewBadge("ks25");

        if(!badgeSet.contains("ks50"))
            createNewBadge("ks50");

        if(!badgeSet.contains("ks100"))
            createNewBadge("ks100");

        if(!badgeSet.contains("ks200"))
            createNewBadge("ks200");
    }

    public void save() {
        if (badgeMap.isEmpty())
            return;

        badgeMap.values().forEach(badge -> badge.save(serverConfig.getCfg()));

        serverConfig.save();
    }

    public Badge getBadgeFromName(final String name) {
        return badgeMap.get(name);
    }

    public Badge getBadgeFromPrefix(final String prefix) {
        for (final Badge badge : badgeMap.values()) {
            if (badge.getPrefix().equalsIgnoreCase(prefix))
                return badge;
        }

        return null;
    }

}
