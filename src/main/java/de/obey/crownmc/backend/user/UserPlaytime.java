package de.obey.crownmc.backend.user;
/*

    Author - Obey -> SkySlayer-v4
       04.12.2022 / 16:57

*/

import de.obey.crownmc.backend.enums.DataType;

public final class UserPlaytime {

    private final User user;

    public UserPlaytime(final User user) {
        this.user = user;
    }

    public void onJoin() {
        user.setLong(DataType.PLAYTIMESAVED, System.currentTimeMillis());
    }

    private boolean isAFK = false;
    public void startAFK() {
        updatePlaytime();

        isAFK = true;
    }

    public void endAFK() {
        isAFK = false;

        onJoin();
    }

    public void updatePlaytime() {

        if(isAFK)
            return;

        if (user.getOfflinePlayer().isOnline()) {
            if (user.getLong(DataType.PLAYTIMESAVED) == 0) {
                user.setLong(DataType.PLAYTIMESAVED, System.currentTimeMillis());
                return;
            }

            final long pt = (System.currentTimeMillis() - user.getLong(DataType.PLAYTIMESAVED)) / 1000;

            if(pt < 1)
                return;

            user.addLong(DataType.PLAYTIME, pt);
            user.setLong(DataType.PLAYTIMESAVED, System.currentTimeMillis());
        }
    }

    public long getCurrentPlaytime() {
        updatePlaytime();
        return user.getLong(DataType.PLAYTIME);
    }

}
