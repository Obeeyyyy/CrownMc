package de.obey.crownmc.handler;
/*

    Author - Obey -> SkySlayer-v4
       15.10.2022 / 15:16

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.backend.Rang;
import de.obey.crownmc.backend.ServerConfig;
import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.backend.user.User;
import de.obey.crownmc.commands.AfkCommand;
import de.obey.crownmc.commands.VanishCommand;
import de.obey.crownmc.objects.pvp.Combat;
import de.obey.crownmc.util.Bools;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.luckperms.api.LuckPermsProvider;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@RequiredArgsConstructor
public final class ScoreboardHandler {

    @Getter
    private final Map<Scoreboard, Map<String, Team>> teams = new HashMap<>();
    private final ArrayList<String> colors = new ArrayList<>(Arrays.asList(
            "§1§l",
            "§1§l",
            "§9§l",
            "§9§l",
            "§2§l",
            "§2§l",
            "§a§l",
            "§a§l",
            "§4§l",
            "§4§l",
            "§c§l",
            "§c§l",
            "§6§l",
            "§6§l",
            "§e§l",
            "§e§l",
            "§7§l",
            "§7§l",
            "§f§l",
            "§f§l",
            "§5§l",
            "§5§l",
            "§d§l",
            "§d§l",
            "§3§l",
            "§3§l",
            "§b§l",
            "§b§l"
    ));
    private ExecutorService executorService;
    private UserHandler userHandler;
    private RangHandler rangHandler;
    private CombatHandler combatHandler;
    private ServerConfig serverConfig;
    private MessageUtil messageUtil;
    private EloHandler eloHandler;
    private int rainbowInt = 0;

    public void runUpdateTick() {
        Bukkit.getOnlinePlayers().forEach(this::setTablistName);
        rainbowInt++;

        if (rainbowInt > colors.size())
            rainbowInt = 0;
    }

    public void updateEverythingForEveryone() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    setTablistDesign(player);
                    setTablistName(player);
                    updateScoreboard(player);
                });
            }
        }.runTaskLater(CrownMain.getInstance(), 10);
    }

    public void setTablistDesign(final Player player) {
        if (userHandler == null) {
            userHandler = CrownMain.getInstance().getInitializer().getUserHandler();
            rangHandler = CrownMain.getInstance().getInitializer().getRangHandler();
            combatHandler = CrownMain.getInstance().getInitializer().getCombatHandler();
            executorService = CrownMain.getInstance().getInitializer().getExecutorService();
            serverConfig = CrownMain.getInstance().getInitializer().getServerConfig();
            messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();
            eloHandler = CrownMain.getInstance().getInitializer().getEloHandler();
        }

        final String header = "§6§lCrownMc§8§l.§6§lde\n\n" +
                "§7Registrierte Spieler§8: §f" + serverConfig.getPlayerCount() + "\n" +
                "§7Täglicher Spielerrekord§8: §f" + serverConfig.getDailyCount() + "\n" +
                "§7Online§8: §f" + (Bukkit.getOnlinePlayers().size() - VanishCommand.vanished.size()) + "\n";

        final String footer = "\n§6§lEvent§8: §f" + serverConfig.getEvent() +
                "\n\n§7Discord§8: §fdiscord.crownmc.de \n" +
                "§7 Wir wünschen viel Spaß beim Spielen§8!\n";

        final IChatBaseComponent head = IChatBaseComponent.ChatSerializer.a("{\"text\": \" " + header + "\"}");
        final IChatBaseComponent foot = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + footer + "\"}");

        final PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

        try {
            final Field hf = packet.getClass().getDeclaredField("a");
            hf.setAccessible(true);
            hf.set(packet, head);
            hf.setAccessible(false);

            final Field ff = packet.getClass().getDeclaredField("b");
            ff.setAccessible(true);
            ff.set(packet, foot);
            ff.setAccessible(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public void setTablistName(final Player player) {
        executorService.submit(() -> {
            if (userHandler == null) {
                userHandler = CrownMain.getInstance().getInitializer().getUserHandler();
                rangHandler = CrownMain.getInstance().getInitializer().getRangHandler();
                combatHandler = CrownMain.getInstance().getInitializer().getCombatHandler();
                executorService = CrownMain.getInstance().getInitializer().getExecutorService();
                serverConfig = CrownMain.getInstance().getInitializer().getServerConfig();
                messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();
                eloHandler = CrownMain.getInstance().getInitializer().getEloHandler();
            }

            final Rang rang = rangHandler.getPlayerRang(player);

            if (rang == null)
                return;

            String value = rang.getTabPrefix() + player.getName() + rang.getTabSuffix();

            if (VanishCommand.vanished.contains(player))
                value = value + " §8(§b§lV§8)";

            if (AfkCommand.afkList.contains(player))
                value = value + " §8× §f§lAFK";

            final User user = userHandler.getUserInstant(player.getUniqueId());

            if (user == null) {
                player.setPlayerListName(ChatColor.translateAlternateColorCodes('&', value));
                return;
            }

            if (user.getString(DataType.TMOTE).length() > 0)
                value = value + " " + ChatColor.translateAlternateColorCodes('&', user.getString(DataType.TMOTE));

            if (user.is(DataType.RAINBOWTAB) || Bools.rainbowtab) {
                int i = 0;

                final String[] splittedPrefix = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', rang.getTabPrefix().split(" ")[0])).split("");

                String rainbowPrefix = "";

                for (String s : splittedPrefix) {
                    rainbowPrefix = rainbowPrefix + colors.get(((rainbowInt + i) >= colors.size() ? (rainbowInt + i - colors.size()) : rainbowInt + i)) + s;
                    i++;

                    if (i >= colors.size() - 1)
                        i = 0;
                }

                final String[] splittedName = player.getName().split("");

                String rainbowName = "";

                for (String s : splittedName) {
                    rainbowName = rainbowName + colors.get(((rainbowInt + i) >= colors.size() ? (rainbowInt + i - colors.size()) : rainbowInt + i)) + s;
                    i++;

                    if (i >= colors.size() - 1)
                        i = 0;
                }

                value = value.replace(rang.getTabPrefix().split(" ")[0], rainbowPrefix);
                value = value.replace(player.getName(), rainbowName);
            }

            player.setPlayerListName(ChatColor.translateAlternateColorCodes('&', value));
        });
    }

    public void setScoreboard(final Player player) {
        if (player == null || !player.isOnline())
            return;

        if (userHandler == null) {
            userHandler = CrownMain.getInstance().getInitializer().getUserHandler();
            rangHandler = CrownMain.getInstance().getInitializer().getRangHandler();
            combatHandler = CrownMain.getInstance().getInitializer().getCombatHandler();
            executorService = CrownMain.getInstance().getInitializer().getExecutorService();
            serverConfig = CrownMain.getInstance().getInitializer().getServerConfig();
            messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();
            eloHandler = CrownMain.getInstance().getInitializer().getEloHandler();
        }

        // REMOVE Cached scoreboard if player has one
        if (player.getScoreboard() != null)
            teams.remove(player.getScoreboard());

        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        String type = "normal";

        if (combatHandler.isInCombat(player) != null)
            type = "combat";

        if (!userHandler.getUserInstant(player.getUniqueId()).is(DataType.SCOREBOARDSTATE))
            type = "invisible";

        if (!type.equalsIgnoreCase("invisible")) {

            Objective sidebar_normal = scoreboard.getObjective("sidebar_normal");
            if (sidebar_normal == null)
                sidebar_normal = scoreboard.registerNewObjective("sidebar_normal", "bbb");

            Objective sidebar_combat = scoreboard.getObjective("sidebar_combat");
            if (sidebar_combat == null)
                sidebar_combat = scoreboard.registerNewObjective("sidebar_combat", "bbb");

            /*

                    NORMAL SCOREBOARD

             */

            sidebar_normal.setDisplayName("§6§lCrownMc §8(§f" + (Bukkit.getOnlinePlayers().size() - VanishCommand.vanished.size()) + "§8)");

            sidebar_normal.getScore("§7           SkyPvP").setScore(14);
            sidebar_normal.getScore("§3").setScore(13);

            sidebar_normal.getScore("§6§l" + player.getName()).setScore(12);

            final Team money = scoreboard.registerNewTeam("money");
            money.setSuffix("...");
            money.addEntry("§8 ├ §7Money§8: §e");
            sidebar_normal.getScore("§8 ├ §7Money§8: §e").setScore(11);

            final Team stats = scoreboard.registerNewTeam("stats");
            stats.setSuffix("...");
            stats.addEntry("§8 ├ §7KDr§8: §a");
            sidebar_normal.getScore("§8 ├ §7KDr§8: §a").setScore(10);

            final Team rank = scoreboard.registerNewTeam("rank");
            rank.setSuffix("§7...");
            rank.addEntry("§8 ├ §7PvP-Rank§8: §f");
            sidebar_normal.getScore("§8 ├ §7PvP-Rank§8: §f").setScore(9);

            final Team votes = scoreboard.registerNewTeam("votes");
            votes.setSuffix("§7...");
            votes.addEntry("§8 ├ §7Votes§8: §2");
            sidebar_normal.getScore("§8 ├ §7Votes§8: §2").setScore(8);

            final Team playtime = scoreboard.registerNewTeam("playtime");
            playtime.setSuffix("§7...");
            playtime.addEntry("§8 └ §7Playtime§8: §f");
            sidebar_normal.getScore("§8 └ §7Playtime§8: §f").setScore(7);

            sidebar_normal.getScore("§2").setScore(6);

            sidebar_normal.getScore("§6§lInformation").setScore(5);

            if (PermissionUtil.hasPermission(player, "team", false)) {
                final Team vanish = scoreboard.registerNewTeam("vanish");
                vanish.setSuffix("...");
                vanish.addEntry("§8 ├ §7Vanish§8: §r");
                sidebar_normal.getScore("§8 ├ §7Vanish§8: §r").setScore(4);
            }

            final Team event = scoreboard.registerNewTeam("event");
            event.setSuffix("...");
            event.addEntry("§8 ├ §7Event§8: §r");
            sidebar_normal.getScore("§8 ├ §7Event§8: §r").setScore(3);

            final Team voteparty = scoreboard.registerNewTeam("voteparty");
            voteparty.setSuffix("...");
            voteparty.addEntry("§8 └ §7Voteparty§8: §a§o");
            sidebar_normal.getScore("§8 └ §7Voteparty§8: §a§o").setScore(2);

            sidebar_normal.getScore("§1").setScore(1);
            sidebar_normal.getScore("§7discord.crownmc.de").setScore(0);

            /*

                    COMBAT SCOREBOARD

             */

            sidebar_combat.setDisplayName("§c§lCOMBAT");
            sidebar_combat.getScore("§0").setScore(15);

            sidebar_combat.getScore("§f§lDEIN GEGNER").setScore(14);
            final Team gegner = scoreboard.registerNewTeam("gegener");
            gegner.setSuffix("");
            gegner.addEntry("§8 ┃ §c§o");
            sidebar_combat.getScore("§8 ┃ §c§o").setScore(13);

            final Team leben = scoreboard.registerNewTeam("leben");
            leben.setSuffix("");
            leben.addEntry("§8 ┃ §7§o");
            sidebar_combat.getScore("§8 ┃ §7§o").setScore(12);

            sidebar_combat.getScore("§3").setScore(11);

            sidebar_combat.getScore("§f§lDEINE ARMOR").setScore(10);
            final Team helm = scoreboard.registerNewTeam("helm");
            helm.setSuffix("");
            helm.addEntry("§8 ┃ §7Helm§8: ");
            sidebar_combat.getScore("§8 ┃ §7Helm§8: ").setScore(9);

            final Team brust = scoreboard.registerNewTeam("brust");
            brust.setSuffix("");
            brust.addEntry("§8 ┃ §7Brustplatte§8: ");
            sidebar_combat.getScore("§8 ┃ §7Brustplatte§8: ").setScore(8);

            final Team hose = scoreboard.registerNewTeam("hose");
            hose.setSuffix("");
            hose.addEntry("§8 ┃ §7Hose§8: ");
            sidebar_combat.getScore("§8 ┃ §7Hose§8: ").setScore(7);

            final Team schuhe = scoreboard.registerNewTeam("schuhe");
            schuhe.setSuffix("");
            schuhe.addEntry("§8 ┃ §7Schuhe§8: ");
            sidebar_combat.getScore("§8 ┃ §7Schuhe§8: ").setScore(6);

            sidebar_combat.getScore("§2").setScore(5);

            sidebar_combat.getScore("§f§lINFORMATIONEN").setScore(4);
            final Team dauer = scoreboard.registerNewTeam("dauer");
            dauer.setSuffix("0s");
            dauer.addEntry("§8 ┃ §7Dauer§8: §f§o");
            sidebar_combat.getScore("§8 ┃ §7Dauer§8: §f§o").setScore(3);

            final Team ende = scoreboard.registerNewTeam("ende");
            ende.setSuffix("10s");
            ende.addEntry("§8 ┃ §7Endet in§8: §f§o");
            sidebar_combat.getScore("§8 ┃ §7Endet in§8: §f§o").setScore(2);

            sidebar_combat.getScore("§1").setScore(1);

             /*

                    Setting Objective displayslot

             */

            if (type.equalsIgnoreCase("normal")) {
                sidebar_normal.setDisplaySlot(DisplaySlot.SIDEBAR);
                sidebar_combat.setDisplaySlot(null);

            } else if (type.equalsIgnoreCase("combat")) {
                sidebar_combat.setDisplaySlot(DisplaySlot.SIDEBAR);
                sidebar_normal.setDisplaySlot(null);
            }
        }

        /*
            Player Hearts
        */

        Objective hearts = scoreboard.getObjective("hearts");

        if(hearts == null) {
            hearts = scoreboard.registerNewObjective("health", "health");
            hearts.setDisplayName("§c§l❤");
        }

        hearts.setDisplaySlot(DisplaySlot.BELOW_NAME);

        player.setScoreboard(scoreboard);
        updateScoreboard(player);
    }

    public void updateScoreboard(final Player player) {

        if (player == null || !player.isOnline())
            return;

        final Scoreboard scoreboard = player.getScoreboard();

        if (scoreboard == null) {
            setScoreboard(player);
            return;
        }

        if (userHandler == null) {
            userHandler = CrownMain.getInstance().getInitializer().getUserHandler();
            rangHandler = CrownMain.getInstance().getInitializer().getRangHandler();
            combatHandler = CrownMain.getInstance().getInitializer().getCombatHandler();
            executorService = CrownMain.getInstance().getInitializer().getExecutorService();
            serverConfig = CrownMain.getInstance().getInitializer().getServerConfig();
            messageUtil = CrownMain.getInstance().getInitializer().getMessageUtil();
            eloHandler = CrownMain.getInstance().getInitializer().getEloHandler();
        }

        setTeams(scoreboard);

        final User user = userHandler.getUserInstant(player.getUniqueId());

        if (scoreboard.getObjective("sidebar_normal") == null) {
            if (!user.is(DataType.SCOREBOARDSTATE))
                return;

            setScoreboard(player);
            return;
        }

        executorService.submit(() -> {

            String type = "normal";

            if (combatHandler.isInCombat(player) != null)
                type = "combat";

            final DecimalFormat decimalFormat = new DecimalFormat("0", new DecimalFormatSymbols(Locale.ENGLISH));

            if (type.equalsIgnoreCase("normal")) {

                if (scoreboard.getObjective("sidebar_normal").getDisplaySlot() == null) {
                    scoreboard.getObjective("sidebar_normal").setDisplaySlot(DisplaySlot.SIDEBAR);
                    scoreboard.getObjective("sidebar_combat").setDisplaySlot(null);
                }

                scoreboard.getObjective("sidebar_normal").setDisplayName("§6§lCrownMc §8(§f" + (Bukkit.getOnlinePlayers().size() - VanishCommand.vanished.size()) + "§8)");

                final Team money = scoreboard.getTeam("money");
                final long moneyAmount = user.getLong(DataType.MONEY);
                String coinString = "";

                if (moneyAmount >= 1000000) {
                    coinString = MathUtil.replaceLongWithSuffix(moneyAmount);
                } else {
                    coinString = messageUtil.formatLong(moneyAmount);
                }

                money.setSuffix(coinString + "§6§o$");

                final Team stats = scoreboard.getTeam("stats");
                stats.setSuffix(user.getInt(DataType.KILLS) + "§8 × §c" + user.getInt(DataType.DEATHS));

                final Team pvprank = scoreboard.getTeam("rank");
                pvprank.setSuffix(eloHandler.getEloRangFromEloPoints(user.getInt(DataType.ELOPOINTS)));

                final Team votes = scoreboard.getTeam("votes");
                votes.setSuffix(messageUtil.formatLong(user.getInt(DataType.VOTES)));

                final Team playtime = scoreboard.getTeam("playtime");
                final String value = MathUtil.getDaysAnHoursFromSeconds(user.getLong(DataType.PLAYTIME));
                playtime.setSuffix((value.length() > 0 ? value : "§8(§c§ox§8)"));

                final Team voteparty = scoreboard.getTeam("voteparty");
                voteparty.setSuffix(serverConfig.getVotes() + "§8/§2§l" + serverConfig.getVoteparty());

                final Team event = scoreboard.getTeam("event");
                event.setSuffix(serverConfig.getEvent());

                if (PermissionUtil.hasPermission(player, "team", false)) {
                    final Team vanish = scoreboard.getTeam("vanish");
                    vanish.setSuffix(VanishCommand.vanished.contains(player) ? "§aJa" : "§cNein");
                }

            } else if (type.equalsIgnoreCase("combat")) {

                if (scoreboard.getObjective("sidebar_combat").getDisplaySlot() == null) {
                    scoreboard.getObjective("sidebar_combat").setDisplaySlot(DisplaySlot.SIDEBAR);
                    scoreboard.getObjective("sidebar_normal").setDisplaySlot(null);
                }

                final Combat combat = combatHandler.isInCombat(player);
                final Player opponent = combat.getOpponent();

                final Team gegner = scoreboard.getTeam("gegener");
                gegner.setSuffix(opponent.getName());

                final Team leben = scoreboard.getTeam("leben");
                leben.setSuffix(decimalFormat.format(combat.getOpponent().getHealth()) + "§4§l❤");

                final Team helm = scoreboard.getTeam("helm");
                helm.setSuffix(messageUtil.getArmorDurability(player.getInventory().getHelmet()));

                final Team brust = scoreboard.getTeam("brust");
                brust.setSuffix(messageUtil.getArmorDurability(player.getInventory().getChestplate()));

                final Team hose = scoreboard.getTeam("hose");
                hose.setSuffix(messageUtil.getArmorDurability(player.getInventory().getLeggings()));

                final Team schuhe = scoreboard.getTeam("schuhe");
                schuhe.setSuffix(messageUtil.getArmorDurability(player.getInventory().getBoots()));

                final Team dauer = scoreboard.getTeam("dauer");
                dauer.setSuffix(combat.getDurationString());

                final Team ende = scoreboard.getTeam("ende");
                ende.setSuffix(combat.getCooldown() + "s");
            }
        });
    }

    private void setTeams(final Scoreboard scoreboard) {
        if (!teams.containsKey(scoreboard)) {
            final Map<String, Team> sip = new HashMap<>();

            for (Rang rang : rangHandler.getGroupMap().values()) {
                sip.put(rang.getName(), getTeam(scoreboard, rang.getSort() + rang.getName(),
                        ChatColor.translateAlternateColorCodes('&', rang.getNtPrefix()),
                        ChatColor.translateAlternateColorCodes('&', rang.getNtSuffix())));
            }

            teams.put(scoreboard, sip);
        }

        if (Bukkit.getOnlinePlayers().isEmpty())
            return;

        for (final Player all : Bukkit.getOnlinePlayers()) {
            final String rang = LuckPermsProvider.get().getUserManager().getUser(all.getUniqueId()).getPrimaryGroup();

            if (!teams.get(scoreboard).containsKey(rang))
                return;

            if (!teams.get(scoreboard).get(rang).hasPlayer(all))
                teams.get(scoreboard).get(rang).addPlayer(all);
        }
    }

    private Team getTeam(org.bukkit.scoreboard.Scoreboard board, String Team, String prefix, String suffix) {
        Team team = board.getTeam(Team);

        if (team == null)
            team = board.registerNewTeam(Team);

        team.setPrefix(prefix);
        team.setSuffix(suffix);

        return team;
    }
}
