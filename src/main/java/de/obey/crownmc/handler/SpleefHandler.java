package de.obey.crownmc.handler;
/*

    Author - Obey -> CrownMc
       12.08.2023 / 12:06

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.objects.events.SpleefRound;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

@RequiredArgsConstructor @NonNull
public final class SpleefHandler {

    @Getter @Setter
    private SpleefRound spleefRound;

    public boolean start(final Location location) {
        if(spleefRound != null) {
            return false;
        }

        spleefRound = new SpleefRound(location);

        return true;
    }

}
