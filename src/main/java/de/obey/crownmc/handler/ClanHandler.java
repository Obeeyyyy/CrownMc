package de.obey.crownmc.handler;

import com.google.common.collect.Maps;
import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.Backend;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.objects.Clan;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/*

    Author - Obey -> CrownMc
       18.06.2023 / 15:38

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

@RequiredArgsConstructor @NonNull
public final class ClanHandler {

    private final MessageUtil messageUtil;
    private final Backend backend;
    private final ExecutorService executorService;

    @Getter
    private final Map<String, Clan> clanCache = Maps.newConcurrentMap();
    private int intervalTicked = 0;
    public void runInterval() {
        intervalTicked++;

        if (intervalTicked == 180) { // runs every 90 seconds
            intervalTicked = 0;

            clanCache.values().forEach(clan -> saveData(clan));
        }
    }

    public boolean exists(final String clanName) {
        return new File(CrownMain.getInstance().getDataFolder() + "/clanFiles/" + clanName + ".yml").exists();
    }

    public CompletableFuture<Clan> loadData(final String clanName) {
        return CompletableFuture.supplyAsync(() -> {
            if(clanCache.containsKey(clanName))
                return clanCache.get(clanName);

            if(!exists(clanName))
                return null;

            final Clan clan = new Clan(clanName);

            final ResultSet results = backend.getResultSet("SELECT * FROM clans WHERE name='" + clanName + "'");

            if (results == null) {
                messageUtil.warn("§c§o failed to load mysql clan " + clanName);
                return clan;
            }

            return clan;
        });
    }

    public void saveData(final Clan clan) {

    }

}
