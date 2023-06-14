package de.obey.slayer.handler;
/*

    Author - Obey -> SkySlayer-v4
       03.11.2022 / 15:51

*/

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class EloHandler {

    public String getEloRangFromEloPoints(final int points) {

        if (points >= 20000) return "§4§lMASTER";

        if (points >= 5000) return "§f§lSILBER III";

        if (points >= 4000) return "§f§lSILBER II";

        if (points >= 3000) return "§f§lSILBER I";

        if (points >= 2000) return "§e§lBRONZE III";

        if (points >= 1000) return "§e§lBRONZE II";

        return "§e§lBRONZE I";
    }


}
