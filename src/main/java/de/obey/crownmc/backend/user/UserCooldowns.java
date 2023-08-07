package de.obey.crownmc.backend.user;
/*

    Author - Obey -> SkySlayer-v4
       17.11.2022 / 15:42

*/

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class UserCooldowns {

    private final YamlConfiguration cfg;

    @Getter
    private final Map<String, Long> cooldowns = new HashMap<>();

    public UserCooldowns(final User user) {
        cfg = user.getCfg();
    }

    public void load() {
        if (!cfg.contains("cooldowns"))
            return;

        final Set<String> temp = cfg.getConfigurationSection("cooldowns").getKeys(false);

        if (temp.isEmpty())
            return;

        temp.forEach(cd -> cooldowns.put(cd, cfg.getLong("cooldowns." + cd)));
    }

    public void save() {
        if (cooldowns.isEmpty())
            return;

        cooldowns.keySet().forEach(cd -> cfg.set("cooldowns." + cd, cooldowns.get(cd)));
    }

    public void clearCooldowns() {
        cooldowns.clear();
    }

    public void setCooldown(final String what, final Long until) {
        cooldowns.put(what, until);
    }

    public boolean isReady(final String what) {
        if (!cooldowns.containsKey(what))
            return true;

        return System.currentTimeMillis() >= cooldowns.get(what);
    }

    public long getRemainingMillis(final String what) {
        if (isReady(what))
            return 0;

        return cooldowns.get(what) - System.currentTimeMillis();
    }

}
