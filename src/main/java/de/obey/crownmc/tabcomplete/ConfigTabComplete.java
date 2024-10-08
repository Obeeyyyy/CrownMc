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

        if (args.length > 1 && args[0].equalsIgnoreCase("set")) {
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
            name.add("hardcorePrice");
            name.add("soulReward");
            name.add("netherLevel");
            name.add("endLevel");
            name.add("hardcoreLevel");
            name.add("epCooldown");
            name.add("clanPrice");
            final String looking = args[args.length - 1];

            List<String> sorted = new ArrayList<>();

            for (String s : name) {
                if (s.toLowerCase().startsWith(looking.toLowerCase()))
                    sorted.add(s);
            }

            name = sorted;

        } else if(args.length <= 1) {
            name.add("info");
            name.add("reload");
            name.add("set");
            name.add("add");
            name.add("remove");
        }

        return name;
    }
}
