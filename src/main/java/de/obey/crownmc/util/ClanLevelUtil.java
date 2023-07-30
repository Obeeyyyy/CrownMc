package de.obey.crownmc.util;
/*

    Author - Obey -> CrownMc
       20.06.2023 / 01:04

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.objects.Clan;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class ClanLevelUtil {

    private MessageUtil messageUtil;

    public void checkForLevelUp(final Clan clan) {
        final int xp = clan.getXp();
        int level = clan.getLevel();
        int xpForNextLevel = getXPForNextLevel(level + 1);

        while(xp >= xpForNextLevel) {
            level++;
            levelUP(clan, xp - xpForNextLevel, level);
            xpForNextLevel = getXPForNextLevel(level + 1);
        }
    }

    public int getXPForNextLevel(final int level) {
        return (int) (500 * Math.pow(level*6, 2) - (500 * level));
    }

    public void levelUP(final Clan clan, final int extraXp, final int nextLevel) {
        if(messageUtil == null)
            messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();

        clan.setLevel(nextLevel);
        clan.setXp(extraXp);
    }

}
