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
        final int level = user.getInt(DataType.LEVEL);
        final int xp = user.getInt(DataType.XP);

        final int xpForNextLevel = getXPForNextLevel(level + 1);

        if(xp >= xpForNextLevel) {
            levelUP(user, xp - xpForNextLevel, level + 1);
        }
    }

    public int getXPForNextLevel(final int level) {
        return (int) (500 * Math.pow(level, 2) - (500 * level));
    }

    public void levelUP(final User user, final int extraXp, final int nextLevel) {
        if(messageUtil == null)
            messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();

        final int moneyReward = CrownMain.getInstance().getInitializer().getServerConfig().getLevelUpMoney() * nextLevel;

        user.addInt(DataType.LEVEL, 1);
        user.setInt(DataType.XP, extraXp);
        user.addLong(DataType.MONEY, moneyReward);

        if(!user.getOfflinePlayer().isOnline())
            return;

        messageUtil.sendMessage(user.getOfflinePlayer().getPlayer(), "Du bist ein Level aufgestiegen §8! §a§o" + (nextLevel - 1) + " §f§o> §2§o§l" + nextLevel);
        messageUtil.sendMessage(user.getOfflinePlayer().getPlayer(), "§a+§e§o" + messageUtil.formatLong(moneyReward) + "§6§l$");
    }

}
