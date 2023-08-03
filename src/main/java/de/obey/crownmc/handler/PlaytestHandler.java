package de.obey.crownmc.handler;
/*

    Author - Obey -> CrownMc
       02.08.2023 / 19:02

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.backend.user.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.UUID;

@RequiredArgsConstructor @NonNull
public final class PlaytestHandler {

    private final ServerConfig serverConfig;

    private ArrayList<UUID> uuids = new ArrayList<>();

    public void reward(final User user) {
        user.getBadges().addBadge("tester");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "rang " + user.getOfflinePlayer().getName() + " warrior");
    }

    public void loadTesters() {
       final YamlConfiguration cfg = serverConfig.getCfg();

       if(cfg.contains("tester")) {
           final ArrayList<String> temp = (ArrayList<String>) cfg.get("tester");

           for (final String s : temp)
               uuids.add(UUID.fromString(s));
       }
    }

    public void addTester(final UUID uuid) {
        if(!uuids.contains(uuid)) {
            uuids.add(uuid);

            final YamlConfiguration cfg = serverConfig.getCfg();
            final ArrayList<String> temp = new ArrayList<>();

            for (UUID uuid1 : uuids) {
                temp.add(uuid1.toString());
            }

            cfg.set("tester", temp);

            serverConfig.save();
        }
    }

    public boolean isTester(final UUID uuid) {
        return uuids.contains(uuid);
    }

}
