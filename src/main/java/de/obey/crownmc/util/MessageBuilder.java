package de.obey.crownmc.util;
/*

    Author - EntixOG -> SkySlayer-v4
       04.11.2022 / 16:31

*/

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class MessageBuilder {

    TextComponent textComponent;

    public MessageBuilder() {
        this.textComponent = new TextComponent();
    }

    public MessageBuilder(String message) {
        this.textComponent = new TextComponent(message);
    }

    public MessageBuilder addText(String text) {
        this.textComponent.addExtra(text);
        return this;
    }

    public MessageBuilder addSpace() {
        this.textComponent.addExtra(" ");
        return this;
    }

    public MessageBuilder addLine() {
        this.textComponent.addExtra("\n");
        return this;
    }

    public MessageBuilder addClickable(ClickEvent.Action action, String value) {
        this.textComponent.setClickEvent(new ClickEvent(action, value));
        return this;
    }

    public MessageBuilder addClickable(String text, ClickEvent.Action action, String value) {
        TextComponent secondComponent = new TextComponent(text);
        secondComponent.setClickEvent(new ClickEvent(action, value));
        this.textComponent.addExtra(secondComponent);
        return this;
    }

    public TextComponent addClickable(String text, String hover, ClickEvent.Action action, String command) {
        TextComponent textComponent = new TextComponent(text);
        textComponent.setClickEvent(new ClickEvent(action, "/" + command));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hover).create()));
        return textComponent;
    }

    public MessageBuilder addHover(String text) {
        ComponentBuilder componentBuilder = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', text));
        this.textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, componentBuilder.create()));
        return this;
    }

    public MessageBuilder addHover(String text, HoverEvent.Action action) {
        ComponentBuilder componentBuilder = new ComponentBuilder(ChatColor.translateAlternateColorCodes('&', text));
        this.textComponent.setHoverEvent(new HoverEvent(action, componentBuilder.create()));
        return this;
    }


    public void broadcast() {
        Bukkit.getOnlinePlayers().forEach(player -> player.spigot().sendMessage(this.textComponent));
    }

    public void send(Player player) {
        player.spigot().sendMessage(this.textComponent);
    }
}