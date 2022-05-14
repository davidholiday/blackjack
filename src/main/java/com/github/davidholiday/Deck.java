package com.github.davidholiday;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deck {

    private static final Logger LOG = LoggerFactory.getLogger(Deck.class);

    private final List<Card> deck = new ArrayList<>();

    public Deck() {
        for (CardSuit cardSuit : CardSuit.values()) {
            if (cardSuit == CardSuit.NONE) { continue; }
            for (CardType cardType : CardType.values()) {
                if (cardType == CardType.JOKER || cardType == CardType.CUT) { continue; }
                Card card = new Card(cardType, cardSuit);
                deck.add(card);
            }
        }

        String msg = MessageFormat.format("deck is: {0}", deck);
        LOG.info(msg);
    }


}
