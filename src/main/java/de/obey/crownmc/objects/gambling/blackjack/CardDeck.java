package de.obey.crownmc.objects.gambling.blackjack;
/*

    Author - Obey -> CrownMc
       10.08.2023 / 06:05

    You are NOT allowed to use this code in any form 
 without permission from me, obey, the creator of this code.
*/

import java.util.ArrayList;
import java.util.Collections;

public final class CardDeck {

    private final ArrayList<Card> cards = new ArrayList<>();

    public CardDeck() {
        for (Card value : Card.values()) {
            cards.add(value);
            cards.add(value);
            cards.add(value);
            cards.add(value);
        }
        Collections.shuffle(cards);
    }

    public Card getRandomCard() {
        final Card card = cards.get(0);
        cards.remove(0);
        Collections.shuffle(cards);
        return card;
    }

}
