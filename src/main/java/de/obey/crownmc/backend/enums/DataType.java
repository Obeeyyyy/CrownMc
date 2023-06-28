package de.obey.crownmc.backend.enums;
/*

    Author - Obey -> SkySlayer-v4
       08.11.2022 / 23:13

*/

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;

@Getter
@NonNull
@RequiredArgsConstructor
public enum DataType {

    ID("id", StoreType.MYSQL, 0),
    MONEY("money", StoreType.MYSQL, 2500L),
    CROWNS("crowns", StoreType.MYSQL, 0),
    PLAYTIME("playtime", StoreType.MYSQL, 1L),
    PLAYTIMESAVED("playtimesaved", StoreType.CONFIG, 0L),
    BOUNTY("bounty", StoreType.MYSQL, 0L),
    KILLS("kills", StoreType.MYSQL, 0),
    DEATHS("deaths", StoreType.MYSQL, 0),
    ELOPOINTS("elopoints", StoreType.MYSQL, 3000),
    VOTES("votes", StoreType.MYSQL, 0),
    KILLSTREAK("killstreak", StoreType.MYSQL, 0),
    KILLSTREAKRECORD("killstreakrecord", StoreType.MYSQL, 0),
    XP("xp", StoreType.MYSQL, 0),
    LEVEL("level", StoreType.MYSQL, 1),
    DESTROYEDBLOCKS("destroyedBlocks", StoreType.MYSQL, 0L),
    DESTROYEDEVENTBLOCKS("destroyedEventBlocks", StoreType.MYSQL, 0L),
    VOTESTREAK("votestreak", StoreType.CONFIG, 0),
    LOGINSTREAK("loginstreak", StoreType.CONFIG, 1),
    LOGINSTREAKUPDATED("loginstreakupdated", StoreType.CONFIG, 0),
    FIRSTJOINDATE("firstjoindate", StoreType.CONFIG, null),
    TMOTE("tmote", StoreType.CONFIG, ""),
    JOINMESSAGE("joinmessage", StoreType.CONFIG, ""),
    LEAVEMESSAGE("leavemessage", StoreType.CONFIG, ""),
    LASTSEEN("lastseen", StoreType.CONFIG, null),
    JOINED("joined", StoreType.CONFIG, null),
    MSGSTATE("msgstate", StoreType.CONFIG, true),
    TPASTATE("tpastate", StoreType.CONFIG, true),
    SCOREBOARDSTATE("scoreboardstate", StoreType.CONFIG, true),
    KILLHOLOSTATE("killholostate", StoreType.CONFIG, true),
    SPAWNTELEPORT("spawnteleport", StoreType.CONFIG, true),
    CHATLINESSTATE("chatlinesstate", StoreType.CONFIG, false),
    COMMANDWATCHSTATE("commandwathcstate", StoreType.CONFIG, false),
    MSGSPYSTATE("msgspystate", StoreType.CONFIG, false),
    AUTOVANISHSTATE("autovanishstate", StoreType.CONFIG, false),
    RESPAWNKIT("respawnkitstate", StoreType.CONFIG, true),
    REGISTERED("registered", StoreType.CONFIG, true),
    RAINBOWTAB("rainbowtab", StoreType.CONFIG, false),
    IGNORES("ignores", StoreType.CONFIG, new ArrayList<>()),
    LASTLUCKYSPIN("lastluckyspin", StoreType.CONFIG, 6000L);


    private final String savedAs;
    private final StoreType storeType;
    private final Object defaultValue;

    public static DataType getTypeFromSavedAs(final String savedAs) {
        for (final DataType value : DataType.values()) {
            if (value.getStoreType() != StoreType.CONFIG)
                continue;

            if (value.getSavedAs().equalsIgnoreCase(savedAs))
                return value;
        }

        return null;
    }

    public static DataType getTypeFromName(final String savedAs) {
        for (final DataType value : DataType.values()) {
            if (value.getSavedAs().equalsIgnoreCase(savedAs))
                return value;
        }

        return null;
    }

}
