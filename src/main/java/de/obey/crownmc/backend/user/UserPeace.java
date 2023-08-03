package de.obey.crownmc.backend.user;
/*

    Author - Obey -> CrownMc
       05.07.2023 / 18:38

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public final class UserPeace {

    @Getter
    private List<String> peaceList;

    private User user;

    public UserPeace(final User user) {
        this.user = user;
        peaceList = user.getCfg().contains("peacelist") ? user.getCfg().getStringList("peacelist") : new ArrayList<>();
    }

    public boolean hasPeaceWith(final Player target) {
        return peaceList.contains(target.getUniqueId().toString());
    }

    public boolean hasPeaceWith(final OfflinePlayer target) {
        return peaceList.contains(target.getUniqueId().toString());
    }

    public void makePeaceWith(final Player target) {

        if(!peaceList.contains(target.getUniqueId().toString()))
            peaceList.add(target.getUniqueId().toString());

    }

    public void save() {
        user.getCfg().set("peacelist", peaceList);
    }
}
