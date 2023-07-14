package de.obey.crownmc.backend.enums;
/*

    Author - Obey -> SkySlayer-v4
       08.11.2022 / 23:13

*/

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@NonNull
@RequiredArgsConstructor
public enum DataType {

    ID("id", StoreType.MYSQL, 0L),
    MONEY("money", StoreType.MYSQL, 2500L),
    CROWNS("crowns", StoreType.MYSQL, 0L),
    PLAYTIME("playtime", StoreType.MYSQL, 0L),
    PLAYTIMESAVED("playtimesaved", StoreType.CONFIG, 0L),
    BOUNTY("bounty", StoreType.MYSQL, 0L),
    KILLS("kills", StoreType.MYSQL, 0L),
    DEATHS("deaths", StoreType.MYSQL, 0L),
    ELOPOINTS("elopoints", StoreType.MYSQL, 3000L),
    VOTES("votes", StoreType.MYSQL, 0L),
    KILLSTREAK("killstreak", StoreType.MYSQL, 0L),
    KILLSTREAKRECORD("killstreakrecord", StoreType.MYSQL, 0L),
    XP("xp", StoreType.MYSQL, 0L),
    LEVEL("level", StoreType.MYSQL, 1L),
    DESTROYEDBLOCKS("destroyedBlocks", StoreType.MYSQL, 0L),
    DESTROYEDEVENTBLOCKS("destroyedEventBlocks", StoreType.MYSQL, 0L),
    VOTESTREAK("votestreak", StoreType.CONFIG, 0L),
    LOGINSTREAK("loginstreak", StoreType.CONFIG, 1L),
    LOGINSTREAKUPDATED("loginstreakupdated", StoreType.CONFIG, 0L),
    LOGINLASTREWARD("loginlastreward", StoreType.CONFIG, 0L),
    FIRSTJOINDATE("firstjoindate", StoreType.CONFIG, null),
    TMOTE("tmote", StoreType.CONFIG, ""),
    JOINMESSAGE("joinmessage", StoreType.CONFIG, ""),
    LEAVEMESSAGE("leavemessage", StoreType.CONFIG, ""),
    LASTSEEN("lastseen", StoreType.CONFIG, null),
    JOINED("joined", StoreType.CONFIG, null),
    MSGSTATE("msgstate", StoreType.CONFIG, true),
    TPASTATE("tpastate", StoreType.CONFIG, true),
    TRADEREQUESTS("traderequests", StoreType.CONFIG, true),
    PEACEREQUESTS("peacerequests", StoreType.CONFIG, true),
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
    IGNORES("ignores", StoreType.CONFIG, null),
    LASTLUCKYSPIN("lastluckyspin", StoreType.CONFIG, 0L),
    LASTVOTE("lastvote", StoreType.CONFIG, 0L);

    private final String savedAs;
    private final StoreType storeType;
    private final Object defaultValue;

    public static DataType getTypeFromName(final String savedAs) {
        for (final DataType value : DataType.values()) {
            if (value.getSavedAs().equalsIgnoreCase(savedAs))
                return value;
        }

        return null;
    }

}
