package de.obey.crownmc.discord.commands;
/*

    Author - Obey -> CrownMc
       05.07.2023 / 03:45

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@RequiredArgsConstructor @NonNull
public final class DiscordStatsCommand extends ListenerAdapter {

    private final UserHandler userHandler;
    private final MessageUtil messageUtil;

    @Override
    public void onSlashCommandInteraction(@NonNull SlashCommandInteractionEvent event) {
        if(!event.getName().equalsIgnoreCase("stats"))
            return;

        event.deferReply().queue();

        final OptionMapping option = event.getOption("name");

        if(option == null) {
            event.getHook().sendMessage("Bitte gebe einen Spielernamen an.").setEphemeral(true).queue();
            return;
        }

        final String playerName = option.getAsString();

        if(!messageUtil.hasPlayedBefore(playerName)) {
            event.getHook().sendMessage("Der Spieler '" + playerName + "' war noch nie auf CrownMc.de").setEphemeral(true).queue();
            return;
        }

        event.getHook().sendMessage("Daten werden geladen ...").setEphemeral(true).queue();

        final OfflinePlayer player = Bukkit.getOfflinePlayer(playerName);

        userHandler.getUser(player.getUniqueId()).thenAcceptAsync(user -> {

            final long kills = user.getLong(DataType.KILLS),
                    deaths = user.getLong(DataType.DEATHS),
                    money = user.getLong(DataType.MONEY),
                    elo = user.getLong(DataType.ELOPOINTS)
                            ;

            final EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(Color.yellow)
                    .setAuthor("⌈ " + playerName.toUpperCase() + " ⌋", "https://minotar.net/avatar/" + playerName, "https://minotar.net/avatar/" + playerName)
                    .addField("Kills / Tode", "➥ " + kills + " ⁄ " + deaths + " ≈ " + formatKD(kills, deaths), false)
                    .addField("Balance", "➥ " + messageUtil.formatLong(money) + "$", false)
                    .addField("Elo", "➥ " + messageUtil.formatLong(elo), false)
                    .addField("Spielzeit", "➥ " + user.getPlaytime().getCurrentPlaytime(), false);

            event.getChannel().sendMessageEmbeds(embedBuilder.build()).queue();

        });

    }

    private String formatKD(final long kills, final long deaths) {
        final double kd = kills > 0 ? (deaths > 0 ? ((float)(kills / deaths)) + 0.0D : kills) : 0;
        final DecimalFormat format = new DecimalFormat("0.0#", new DecimalFormatSymbols(Locale.ENGLISH));
        return format.format(kd);
    }
}
