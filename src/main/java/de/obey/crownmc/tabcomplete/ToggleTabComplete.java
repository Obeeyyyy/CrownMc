package de.obey.crownmc.tabcomplete;

import de.obey.crownmc.handler.WarpHandler;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public final class ToggleTabComplete implements TabCompleter {

    private final List<String> keys = new ArrayList<>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if(keys.isEmpty()) {
            keys.add("pvp");
            keys.add("drops");
            keys.add("ep");
            keys.add("potions");
            keys.add("pickup");
            keys.add("pay");
            keys.add("nowl");
            keys.add("doublexp");
        }

        return keys;
    }
}
