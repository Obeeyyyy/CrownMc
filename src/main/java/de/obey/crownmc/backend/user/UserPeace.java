package de.obey.crownmc.backend.user;
/*

    Author - Obey -> CrownMc
       05.07.2023 / 18:38

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.backend.enums.DataType;
import lombok.Getter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public final class UserPeace {

    @Getter
    private List<String> peaceList;

    private User user;

    public UserPeace(final User user) {
        this.user = user;
        peaceList = (List<String>) user.getList(DataType.PEACELIST);
    }

    public boolean hasPeaceWith(final Player target) {
        return peaceList.contains(target.getUniqueId().toString());
    }

    public boolean hasPeaceWith(final OfflinePlayer target) {
        return peaceList.contains(target.getUniqueId().toString());
    }

    public void save() {
        user.setList(DataType.PEACELIST, peaceList);
    }
}
