package de.obey.crownmc.objects.events;

import lombok.Getter;
import lombok.Setter;

@Getter
public class WordScramble {

    private final String word;
    private final long reward, started;

    @Setter
    private int state = 0;

    public WordScramble(final String word, final long reward) {
        this.word = word;
        this.reward = reward;
        started = System.currentTimeMillis();
    }

}
