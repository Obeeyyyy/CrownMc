package de.obey.crownmc.handler;

import com.google.common.collect.Maps;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.UUID;

/*

    Author - Obey -> CrownMc
       18.06.2023 / 15:38

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

@RequiredArgsConstructor
public final class ClanHandler {

    @NonNull
    private final MessageUtil messageUtil;
    @Getter
    private final Map<UUID, User> userCache = Maps.newConcurrentMap();

}
