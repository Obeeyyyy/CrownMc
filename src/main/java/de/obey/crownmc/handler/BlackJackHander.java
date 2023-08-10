package de.obey.crownmc.handler;
/*

    Author - Obey -> CrownMc
       10.08.2023 / 06:00

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.objects.gambling.RouletteTable;
import de.obey.crownmc.objects.gambling.blackjack.BlackJackTable;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public final class BlackJackHander {


    @NonNull
    private final LocationHandler locationHandler;
    @NonNull
    private final MessageUtil messageUtil;
    @NonNull
    private final UserHandler userHandler;

    @Getter
    private final HashMap<Integer, BlackJackTable> tables = new HashMap<>();

    @Getter
    private final String prefix = "§8( §f§lBlackJack §8)§7 ";

    public void loadTables() {
        for (int i = 1; i < 5; i++) {
            final Location temp = locationHandler.getLocation("blackjack-" + i);

            if(temp == null)
                break;

            tables.put(i, new BlackJackTable(i, temp.clone(), this));
        }
    }

    public void pay(final UUID uuid, final long amount) {
        userHandler.getUser(uuid).thenAcceptAsync(user -> user.addLong(DataType.MONEY, amount));
    }

    public void shutdown() {
        if(tables.isEmpty())
            return;

        tables.values().forEach(BlackJackTable::shutdown);
    }

}
