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
import lombok.experimental.UtilityClass;

@UtilityClass
public final class LevelUtil {

    private MessageUtil messageUtil;

    public void checkForLevelUp(final User user) {
        final long xp = user.getLong(DataType.XP);
        long level = user.getLong(DataType.LEVEL);
        long xpForNextLevel = getXPForNextLevel(level + 1);

        while(xp >= xpForNextLevel) {
            level++;
            levelUP(user, xp - xpForNextLevel, level);
            xpForNextLevel = getXPForNextLevel(level + 1);
        }
    }

    public long getXPForNextLevel(final long level) {
        return (long) (500 * Math.pow(level, 2) - (500 * level));
    }

    public void levelUP(final User user, final long extraXp, final long nextLevel) {
        if(messageUtil == null)
            messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();

        final long moneyReward = CrownMain.getInstance().getInitializer().getServerConfig().getLevelUpMoney() * nextLevel;

        user.addLong(DataType.LEVEL, 1);
        user.setLong(DataType.XP, extraXp);
        user.addLong(DataType.MONEY, moneyReward);

        if(!user.getOfflinePlayer().isOnline())
            return;

        messageUtil.sendMessage(user.getOfflinePlayer().getPlayer(), "Du bist ein Level aufgestiegen §8! §a§o" + (nextLevel - 1) + " §f§o> §2§o§l" + nextLevel);
        messageUtil.sendMessage(user.getOfflinePlayer().getPlayer(), "§a+§e§o" + messageUtil.formatLong(moneyReward) + "§6§l$");
    }

}
