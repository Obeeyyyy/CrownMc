package de.obey.slayer.handler;
/*

    Author - Obey -> SkySlayer-v4
       04.11.2022 / 14:40

*/

import de.obey.slayer.util.MathUtil;
import de.obey.slayer.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public final class KillFarmHandler {

    @NonNull
    private final MessageUtil messageUtil;

    private final Map<Player, Long> lastDeath = new HashMap<>();
    private final Map<Player, Player> killers = new HashMap<>();
    private final Map<Player, Integer> deathAmount = new HashMap<>();
    private final ArrayList<UUID> blocked = new ArrayList<>();

    public void check(final Player player, Player killer) {
        if (lastDeath.containsKey(player)) {
            if (System.currentTimeMillis() - lastDeath.get(player) <= 60000) {

                if (!deathAmount.containsKey(player)) {
                    deathAmount.put(player, 1);
                } else {
                    deathAmount.put(player, deathAmount.get(player) + 1);
                }

                killers.put(player, killer);

                if (deathAmount.containsKey(player) && deathAmount.get(player) >= 2) {
                    blocked.add(player.getUniqueId());

                    messageUtil.sendMessageToTeamMembers("§4§lKillfarm-Warn §8§l× §c" + player.getName() + " §7Tode§8: §c§ox§f§o" + deathAmount.get(player));
                    messageUtil.sendMessageToTeamMembers("    §8> §7Zuletzt getötet von §c§o" + killers.get(player).getName());
                    messageUtil.sendMessageToTeamMembers("    §8> §c§lZeit §7zwischen den §c§lKills§8: §f" + MathUtil.getMinutesAndSecondsFromSeconds((System.currentTimeMillis() - lastDeath.get(player)) / 1000));
                }
            } else {
                if (System.currentTimeMillis() - lastDeath.get(player) >= 120000) {
                    if (deathAmount.containsKey(player)) {
                        deathAmount.remove(player);
                        blocked.remove(player.getUniqueId());
                    }
                }
            }
        }

        lastDeath.put(player, System.currentTimeMillis());
    }

    public boolean isBlocked(final Player player) {
        return blocked.contains(player.getUniqueId());
    }

}
