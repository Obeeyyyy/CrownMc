package de.obey.crownmc.util;
/*

    Author - EntixOG -> SkySlayer-v4
       04.11.2022 / 16:31

*/

import lombok.Getter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

@Getter
public final class MessageBuilder {

    private final ArrayList<TextComponent> components = new ArrayList<>();

    public MessageBuilder() {}

    public MessageBuilder add(final String text) {
        components.add(new TextComponent(text));
        return this;
    }

    public MessageBuilder addClickableCommand(final String text, final String hover, final String command) {
        final TextComponent temp = new TextComponent(text);
        temp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        temp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        components.add(temp);
        return this;
    }

    public MessageBuilder addHoverShowText(final String text, final String hover) {
        final TextComponent temp = new TextComponent(text);
        temp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        components.add(temp);
        return this;
    }

    public String getText() {
        String line = "";
        for (TextComponent component : components) {
            line = line + " " + component.getText();
        }
        return line;
    }

    public void broadcast() {

        if(components.isEmpty())
            return;

        final TextComponent[] temp = new TextComponent[components.size()];

        for (int i = 0; i < components.size(); i++)
            temp[i] = components.get(i);

        Bukkit.getOnlinePlayers().forEach(player -> player.spigot().sendMessage(temp));
    }

    public void send(Player player) {
        if(components.isEmpty())
            return;

        final TextComponent[] temp = new TextComponent[components.size()];

        for (int i = 0; i < components.size(); i++)
            temp[i] = components.get(i);

        player.spigot().sendMessage(temp);
    }
}