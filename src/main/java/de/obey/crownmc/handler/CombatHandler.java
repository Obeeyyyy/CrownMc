package de.obey.crownmc.handler;
/*

    Author - Obey -> SkySlayer-v4
       24.10.2022 / 14:57

*/

import de.obey.crownmc.objects.Combat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public final class CombatHandler {

    @Getter
    private final Map<Player, Combat> playerCombat = new HashMap<>();
    private final ArrayList<String> combatLoggers = new ArrayList<>();

    public Combat isInCombat(final Player player) {
        return playerCombat.get(player);
    }

    public boolean isCombatLogged(final Player player) {
        return combatLoggers.contains(player.getUniqueId().toString());
    }

    public void putInCombatCache(final Player player, final Combat combat) {
        playerCombat.put(player, combat);
    }

    public void putInLoggerCache(final Player player) {
        combatLoggers.add(player.getUniqueId().toString());
    }

    public void endCombat(final Player player) {
        playerCombat.remove(player);
    }

    public void removeFromLoggers(final String uuidString) {
        combatLoggers.remove(uuidString);
    }

}
