package de.obey.crownmc.tabcomplete;
/*

    Author - Obey -> SkySlayer-v4
       03.01.2023 / 21:01

*/

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public final class ConfigTabComplete implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        List<String> name = new ArrayList<>();

        name.add("killMoneyReward");
        name.add("killXPReward");
        name.add("killEloReward");
        name.add("deathMoneyLose");
        name.add("deathEloLose");
        name.add("voteparty");
        name.add("votes");
        name.add("bcdelay");
        name.add("baseEloKillstreak");
        name.add("baseXPKillstreak");
        name.add("baseMoneyKillstreak");
        name.add("levelUpMoney");
        name.add("netherPrice");
        name.add("endPrice");
        name.add("soulReward");
        name.add("netherLevel");
        name.add("endLevel");
        name.add("epCooldown");
        name.add("clanPrice");

        if (args.length > 0) {
            final String looking = args[args.length - 1];

            List<String> sorted = new ArrayList<>();

            for (String s : name) {
                if (s.toLowerCase().startsWith(looking.toLowerCase()))
                    sorted.add(s);
            }

            name = sorted;
        }

        return name;
    }
}
