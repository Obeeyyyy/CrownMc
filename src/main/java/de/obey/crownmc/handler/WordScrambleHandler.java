package de.obey.crownmc.handler;

import de.obey.crownmc.backend.enums.DataType;
import de.obey.crownmc.objects.WordScramble;
import de.obey.crownmc.util.MathUtil;
import de.obey.crownmc.util.MessageUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor @NonNull
public class WordScrambleHandler {

    private final MessageUtil messageUtil;
    private final UserHandler userHandler;

    @Getter
    private WordScramble scramble;

    public void isInScramble(final Player player, final String message) {
        if(scramble == null || scramble.getState() == 1)
            return;

        if(message.toLowerCase().contains(scramble.getWord()))
            end(player);
    }

    public void end(final Player player) {
        scramble.setState(1);
        messageUtil.broadcast(player.getName() + " hat das Wort in §f§o" + MathUtil.getSecondsFromMillis(System.currentTimeMillis() - scramble.getStarted()) + "§7 entziffert§8, §7es war §f§o" + scramble.getWord() + "§8.");
        messageUtil.sendMessage(player, "Du hast §e§o" + messageUtil.formatLong(scramble.getReward()) + "§6§l$§7 gewonnen§8.");
        userHandler.getUserInstant(player.getUniqueId()).addLong(DataType.MONEY, scramble.getReward());
        scramble = null;
        player.playSound(player.getLocation(), Sound.CAT_MEOW, 0.5f, 1);
    }

    private int ticks = 0;
    public void runInterval() {
        if(ticks/2 == 60*10) {
            ticks = 0;

            startNewScramble();
        }

        ticks++;
    }

    public void startNewScramble() {
        final String word = getRandomWord();
        final long moneyReward = 200 + random.nextInt(2300);

        scramble = new WordScramble(word, moneyReward);
        messageUtil.broadcast("Entziffer das Wort um §e§o" + messageUtil.formatLong(moneyReward) + "§6§l$§7 zu gewinnen§8.");
        messageUtil.broadcast("Wort: §8'§f§o" + scrambleWord(word) + "§8'. §7Viel Glück§8!");
    }

    private String scrambleWord(final String word) {
        char[] chars = word.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            int j = random.nextInt(chars.length);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        String value = new String(chars);

        while (value.equalsIgnoreCase(word)) {
            for (int i = 0; i < chars.length; i++) {
                int j = random.nextInt(chars.length);
                char temp = chars[i];
                chars[i] = chars[j];
                chars[j] = temp;
            }

            value = new String(chars);
        }


        return new String(chars);
    }

    private Random random;
    private String getRandomWord() {
        if(random == null)
            random = new Random();

        final List<String> words = new ArrayList<>();

        words.add("staubsauger");
        words.add("mentos");
        words.add("mathematik");
        words.add("schule");
        words.add("englisch");
        words.add("crownmc");
        words.add("computer");
        words.add("minecraft");
        words.add("lehrer");
        words.add("industrie");
        words.add("auto");
        words.add("fisch");
        words.add("hund");
        words.add("katze");
        words.add("stau");
        words.add("fluss");
        words.add("wohnung");
        words.add("block");
        words.add("diamant");
        words.add("obey");
        words.add("gold");
        words.add("tür");
        words.add("tomate");
        words.add("banane");
        words.add("milch");
        words.add("wasser");
        words.add("kuh");
        words.add("ei");
        words.add("leer");
        words.add("esel");
        words.add("schwein");
        words.add("kekse");
        words.add("spirale");
        words.add("reden");
        words.add("menschen");
        words.add("roulette");
        words.add("eisen");
        words.add("holz");
        words.add("maus");
        words.add("bildschirm");
        words.add("plotwelt");
        words.add("serverteam");
        words.add("bogen");
        words.add("apfel");
        words.add("schwert");
        words.add("himmel");
        words.add("wolke");
        words.add("enderchest");
        words.add("party");

        return words.get(random.nextInt(words.size()));
    }

}
