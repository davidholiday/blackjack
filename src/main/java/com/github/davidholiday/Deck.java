package com.github.davidholiday;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deck {

    private static final Logger LOG = LoggerFactory.getLogger(Deck.class);

    private final List<Card> deck;

    // so the Shoe class can create a deck without making a deck object 
    public static List<Card> makeDeck(boolean withJokers) {
        List<Card> rv = new ArrayList<>();

        for (CardSuit cardSuit : CardSuit.values()) {
            if (cardSuit == CardSuit.NONE) { continue; }
            for (CardType cardType : CardType.values()) {
                if (cardType == CardType.CUT) { continue; }
                if (cardType == CardType.JOKER && withJokers == false) { continue; }
                Card card = new Card(cardType, cardSuit);
                rv.add(card);
            }
        }

        return rv;
    }

    public Deck() {
        deck = makeDeck(false);
        String msg = MessageFormat.format("deck is: {0}", deck);
        LOG.info(msg);
    }


}
