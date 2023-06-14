package de.obey.crownmc.util;
/*

    Author - Obey -> SkySlayer-v4
       31.12.2022 / 14:36

*/

import com.google.gson.JsonObject;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

@UtilityClass
public final class LabyUtil {

    public void sendCurrentPlayingGamemode(final Player player, final String message) {
        JsonObject object = new JsonObject();
        object.addProperty("show_gamemode", true); // Gamemode visible for everyone
        object.addProperty("gamemode_name", message); // Name of the current playing gamemode

        // Send to LabyMod using the API
        LabyModProtocol.sendLabyModMessage(player, "server_gamemode", object);
    }

    public void sendServerBanner(final Player player) {
        final JsonObject object = new JsonObject();
        object.addProperty("url", "https://i.imgur.com/ipJhAxR.png"); // Url of the image
        LabyModProtocol.sendLabyModMessage(player, "server_banner", object);
    }

}
