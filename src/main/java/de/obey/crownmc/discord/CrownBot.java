package de.obey.crownmc.discord;
/*

    Author - Obey -> CrownMc
       05.07.2023 / 03:06

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/


import de.obey.crownmc.discord.commands.DiscordStatsCommand;
import de.obey.crownmc.handler.UserHandler;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@RequiredArgsConstructor
public final class CrownBot {

    @NonNull
    private final UserHandler userHandler;

    @NonNull
    private final MessageUtil messageUtil;

    private JDA jda;

    @Getter
    private final String guildID = "1116849146062721104";

    @Getter
    private Guild guild;

    public void setup(){

        final String token = "MTEyNDAzOTYxNjQ3NTk2NzUxMA.GHl5zn.4MCgs7A13tfrsZzQZzSQfctz49n0n_1YeUQo78";
        final JDABuilder jdaBuilder = JDABuilder.createDefault(token);

        jdaBuilder.setActivity(Activity.playing("SkyPvP | Citybuild"));
        jdaBuilder.setStatus(OnlineStatus.DO_NOT_DISTURB);

        try {
            jda = jdaBuilder.build().awaitReady();
            guild = jda.getGuildById(guildID);

            loadCommands();
            loadListener();
        } catch (final InterruptedException exception) {
            exception.printStackTrace();
        }
    }

    private void loadListener() {
        jda.addEventListener(new DiscordStatsCommand(userHandler, messageUtil));
    }

    private void loadCommands() {
        System.out.println("1");

        if(guild == null)
            return;

        System.out.println("2");

        guild.upsertCommand("stats", "Zeigt die Stats eines Spielers.")
                .addOption(OptionType.STRING, "name", "Der Spielername.", true)
                .queue();

        System.out.println("3");

        jda.updateCommands().queue();
    }

}
