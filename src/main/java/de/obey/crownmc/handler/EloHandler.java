package de.obey.crownmc.handler;
/*

    Author - Obey -> SkySlayer-v4
       03.11.2022 / 15:51

*/

import de.obey.crownmc.objects.pvp.EloRang;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class EloHandler {

    public EloRang getEloRangFromEloPoints(final long points) {

        EloRang foundRang = null;
        long foundPoints = 0;

        for (EloRang rang : EloRang.values()) {
            if(rang.getEloPoints() <= points && rang.getEloPoints() > foundPoints) {
                foundRang = rang;
                foundPoints = rang.getEloPoints();
            }
        }

        return foundRang;
    }


}
