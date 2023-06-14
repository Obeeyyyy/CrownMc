package de.obey.crownmc.handler;
/*

    Author - EntixOG -> SkySlayer-v4
       31.11.2022 / 15:03

*/

import de.obey.crownmc.CrownMain;
import de.obey.crownmc.objects.ChatFilter;
import de.obey.crownmc.util.Bools;
import de.obey.crownmc.util.MessageUtil;
import de.obey.crownmc.util.PermissionUtil;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public final class ChatFilterHandler {

    private final MessageUtil messageUtil;

    private final List<String> wordFilterList = new ArrayList<>();
    private final List<String> defaultPattern = new ArrayList<>();
    private final HashMap<UUID, ChatFilter> chatFilterCache = new HashMap<>();

    public ChatFilterHandler(final MessageUtil messageUtil) {
        this.messageUtil = messageUtil;
        load();
    }

    private void load() {
        File file = new File(CrownMain.getInstance().getDataFolder(), "wordFilter.yml");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (final IOException exception) {
                exception.printStackTrace();
            }

            addDefaultPattern();
        }

        final YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);

        wordFilterList.addAll(yamlConfiguration.getStringList("blockedWords"));
        defaultPattern.addAll(yamlConfiguration.getStringList("pattern"));
    }

    public void save() {
        final File file = new File(CrownMain.getInstance().getDataFolder(), "wordFilter.yml");

        if (!file.exists())
            return;

        final YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        yamlConfiguration.set("blockedWords", wordFilterList);

        try {
            yamlConfiguration.save(file);

        } catch (final IOException exception) {
            exception.printStackTrace();
        }
    }

    public boolean checkString(final String message) {

        for (String blockedWord : wordFilterList) {
            final Matcher matcher = Pattern.compile(blockedWord, 2).matcher(message.replaceAll("\\s+", ""));

            if (blockedWord.toLowerCase().contains("skyslayer"))
                continue;

            if (matcher.find())
                return false;
        }

        for (String regex : defaultPattern) {
            final Matcher matcher = Pattern.compile(regex, 2).matcher(message);
            if (matcher.find()) {
                String matched = matcher.group();

                if (matched.contains("slayer"))
                    continue;

                return false;
            }
        }

        return true;
    }

    public boolean runChatFilterCheck(final Player player, String message) {

        message = ChatColor.stripColor(message);

        if (player.hasPermission("slayer.team"))
            return true;

        final ChatFilter chatFilter = findPlayerById(player.getUniqueId());

        if (chatFilter.hasChatThrottle()) {
            if (System.currentTimeMillis() < chatFilter.getChatThrottle()) {
                messageUtil.sendMessage(player, "Bitte warte einen Moment§8.");
                return false;
            }
        }

        if (!PermissionUtil.hasPermission(player, "chat.bypass", false))
            chatFilter.setChatThrottle(System.currentTimeMillis() + (Bools.slowchat ? 6000 : 800));

        for (String blockedWord : wordFilterList) {
            final Matcher matcher = Pattern.compile(blockedWord, 2).matcher(message.replaceAll("\\s+", ""));

            if (blockedWord.toLowerCase().contains("skyslayer"))
                continue;

            if (matcher.find()) {
                messageUtil.sendMessage(player, "§c§oDeine Nachricht wurde blockiert§8.");
                messageUtil.sendMessageToTeamMembers("§4§lChatFilter §8> §c" + player.getName() + " §8( §f§o" + blockedWord + " §8)");
                messageUtil.sendMessageToTeamMembers("§4§lMESSAGE §8> §7" + message);
                return false;
            }
        }

        /**
         * Hier musst du entscheiden, ob du die Leerzeichen entfernen willst,
         * dadurch können aber False Flags entstehen.
         * **/
        for (String regex : defaultPattern) {
            final Matcher matcher = Pattern.compile(regex, 2).matcher(message);
            if (matcher.find()) {
                String matched = matcher.group();

                if (matched.contains("slayer"))
                    continue;

                messageUtil.sendMessage(player, "§c§oDeine Nachricht wurde blockiert§8.");
                messageUtil.sendMessageToTeamMembers("§4§lChatFilter §8> §c" + player.getName() + " §8( §f§o" + matched + " §8)");
                messageUtil.sendMessageToTeamMembers("§4§lMESSAGE §8> §7" + message);
                return false;
            }
        }

        if (chatFilter.hasLastMessage()) {
            if (message.equalsIgnoreCase(chatFilter.getLastMessage())) {
                messageUtil.sendMessage(player, "Diese Nachricht hast du gerade schon geschrieben§8.");
                return false;
            }
        }

        chatFilter.setLastMessage(message);

        return true;
    }

    private void addDefaultPattern() {
        File file = new File(CrownMain.getInstance().getDataFolder(), "wordFilter.yml");
        if (!file.exists()) {
            return;
        }


        /**
         * Hier kannst du noch eigene Patter hinzufügen, aber diese
         * werden nur beim Erstellen der Datei hinzugefügt. Sonst
         * musst du diese in der Config direkt hinzufügen.
         * **/

        final List<String> patternList = Arrays.asList("(http(s)?:\\/\\/.)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}(\\.| ?\\(?dot\\)? ?| ?\\(?punkt\\)? ?)[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)",
                "(?:[0-9]{1,3}( ?\\. ?|\\(?dot\\)?)){3}[0-9]{1,3}");

        final YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        yamlConfiguration.addDefault("blockedWords", new ArrayList<>());
        yamlConfiguration.addDefault("pattern", patternList);

        yamlConfiguration.options().copyDefaults(true);
        try {
            yamlConfiguration.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public ChatFilter findPlayerById(final UUID uniqueId) {
        if (chatFilterCache.containsKey(uniqueId)) {
            return chatFilterCache.get(uniqueId);
        }

        final ChatFilter chatFilter = new ChatFilter(uniqueId, "", -1L);
        chatFilterCache.put(uniqueId, chatFilter);
        return chatFilter;
    }

}
