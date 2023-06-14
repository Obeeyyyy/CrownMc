package de.obey.slayer.backend.user;
/*

    Author - Obey -> SkySlayer-v4
       04.12.2022 / 16:57

*/

import de.obey.slayer.backend.enums.DataType;

public final class UserPlaytime {

    private final User user;

    public UserPlaytime(final User user) {
        this.user = user;
    }

    public void onJoin() {
        user.setLong(DataType.PLAYTIMESAVED, System.currentTimeMillis());
    }

    public void updatePlaytime() {
        if (user.getOfflinePlayer().isOnline()) {
            if (user.getLong(DataType.PLAYTIMESAVED) == 0) {
                user.setLong(DataType.PLAYTIMESAVED, System.currentTimeMillis());
                return;
            }

            user.addLong(DataType.PLAYTIME, (System.currentTimeMillis() - user.getLong(DataType.PLAYTIMESAVED)) / 1000);
            user.setLong(DataType.PLAYTIMESAVED, System.currentTimeMillis());
        }
    }

    public long getCurrentPlaytime() {
        updatePlaytime();
        return user.getLong(DataType.PLAYTIME);
    }

}
