package de.obey.crownmc.objects.pvp;
/*

    Author - Obey -> CrownMc
       11.07.2023 / 03:32
       
    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import lombok.Getter;

public enum EloRang {

    GRANDMASTER(100000, "§4§lGrandmaster"),
    MASTER(75000, "§c§lMaster"),
    DIAMANT3(55000, "§3§lDIAMANT III"),
    DIAMANT2(45000, "§3§lDIAMANT II"),
    DIAMANT1(38000, "§3§lDIAMANT I"),
    PLATIN3(32500, "§b§lPLATIN III"),
    PLATIN2(27500, "§b§lPLATIN II"),
    PLATIN1(22500, "§b§lPLATIN I"),
    GOLD3(19000, "§6§lGOLD III"),
    GOLD2(16000, "§6§lGOLD II"),
    GOLD1(13000, "§6§lGOLD I"),

    SILBER3(10000, "§f§lSILBER III"),
    SILBER2(7500, "§f§lSILBER II"),
    SILBER1(5000, "§f§lSILBER I"),

    BRONZE3(3000, "§e§lBRONZE III"),
    BRONZE2(1500, "§e§lBRONZE II"),
    BRONZE1(0, "§e§lBRONZE I");

    @Getter
    private final long eloPoints;

    @Getter
    private final String prefix;

    EloRang(long eloPoints, String prefix) {
        this.eloPoints = eloPoints;
        this.prefix = prefix;
    }
}
