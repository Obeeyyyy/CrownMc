package de.obey.crownmc.objects;

import com.google.common.collect.Maps;
import com.mysql.jdbc.TimeUtil;
import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.handler.GoalHandler;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Goal {

    final MessageUtil messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();
    final UserHandler userHandler = CrownMain.getInstance().getInitializer().getUserHandler();
    final GoalHandler goalHandler = CrownMain.getInstance().getInitializer().getGoalHandler();

    final Player creator;
    final Map<Player, Long> participants = Maps.newConcurrentMap(); //pro prozent, 1 item;

    final long goal;
    final long startTime;

    BukkitTask runnable;

    public long getCurrentAmount() {
        long amount = 0L;
        for (long amounts : participants.values()) amount += amounts;

        return amount;
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map) {
        return map.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    public Goal(final Player creator, final long goal) {
        this.creator = creator;
        this.goal = goal;
        this.startTime = System.currentTimeMillis();

        messageUtil.broadcast(creator.getName() + "§7 hat ein §a§lGoal§7 gestartet§8, §7nutze §8/§7Goal um es zu knacken.");
        messageUtil.broadcast("§7Ziel§8: §a" + messageUtil.formatLong(goal) + "§2§o$");
    }

    public void endGoal() {
        long remaining = (goal - getCurrentAmount());
        if (remaining >= 0) {
            long tookMillis = (System.currentTimeMillis() - startTime);
            Map<Player, Long> temp = sortMapByValue(participants);
            Map<Player, Integer> sortedMap = Maps.newConcurrentMap();
            int i = 0;
            for (Player player : temp.keySet()) sortedMap.put(player, ++i);
            messageUtil.broadcast("§7Das §a§lGoal§7 wurde erfolgreich geknackt§8! (§a" + messageUtil.formatLong(goal) + "§2§o$§8)");
            messageUtil.broadcast("§7Jeder§8,§7 der eingezahlt hat§8, §7bekommt eine Kleinigkeit§8!");
            messageUtil.broadcast("§7Es hat §2§o" + MathUtil.getMinutesAndSecondsFromSeconds(tookMillis / 1000) + "§7 gedauert§8.");
            i = 0;
            for (Player player : sortedMap.keySet()) {
                i++;
                if (i <= 3) {
                    //5 items
                    messageUtil.sendMessage(player, "§7Du hast folgende Items erhalten§8:");
                    continue;
                }
                if (i <= 15) {
                    //3 items
                    messageUtil.sendMessage(player, "§7Du hast folgende Items erhalten§8:");
                    continue;
                }
                //1 item
                messageUtil.sendMessage(player, "§7Du hast folgendes Item erhalten§8:");
            }
            return;
        }
        messageUtil.broadcast("§7Das §a§lGoal§7 wurde leider §c§onicht geknackt§8! (§a" + remaining + "§2§o$ §7haben gefehlt§8)");
        messageUtil.broadcast("§7Jeder§8,§7 der eingezahlt hat§8, §7bekommt §a10§2%§7 wieder§8!");
        participants.forEach((player, aLong) -> CrownMain.getInstance().getInitializer().getUserHandler().getUserInstant(player.getUniqueId()).addLong(DataType.MONEY, (aLong / 100)));
    }

    public boolean joinPlayer(final User player, long einsatz) {
        if (!messageUtil.hasEnougthMoney(player, einsatz))
            return false;

        long remaining = goal - getCurrentAmount();
        long extra = einsatz - remaining;
        if (extra < 0) extra = 0;
        einsatz -= extra;
        participants.put(player.getPlayer(), participants.getOrDefault(player.getPlayer(), 0L) + einsatz);
        player.removeLong(DataType.MONEY, einsatz);

        if (getCurrentAmount() >= goal) {
            endGoal();
            return true;
        }

        if (einsatz >= 250) {
            messageUtil.broadcast(player.getPlayer().getName() + "§7 hat in das §a§lGoal§7 gezahlt§8! (§a" + messageUtil.formatLong(getCurrentAmount()) + "§8/§a" + messageUtil.formatLong(goal) + "§2§o$§8)");
            return true;
        }

        return false;
    }

}
