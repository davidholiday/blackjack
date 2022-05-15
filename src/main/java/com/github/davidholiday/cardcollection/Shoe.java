package com.github.davidholiday.cardcollection;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardSuit;
import com.github.davidholiday.card.CardType;

import com.github.davidholiday.util.MessageTemplates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Shoe extends CardCollection {

    private final int expectedFullShoeSize;

    private final Card cutCard = new Card(CardType.CUT, CardSuit.NONE);

    private static final Logger LOG = LoggerFactory.getLogger(Deck.class);

    public Shoe(int decks) {
        if (decks < 1 || decks > 8) {
            String msg = MessageTemplates.getErrorMessage(
                    "1 >= [decks] <= 8",
                    "value for argument [decks]",
                    String.valueOf(decks)
            );
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }

        for (int i = 0; i < decks; i ++) {
            addCards(Deck.makeDeck(false));
        }

        expectedFullShoeSize = getCardListSize();
    }

    public void cut() {
        // "most casinos will tell you to place the cut card between the first and last 15 cards of the deck"
        // https://youtu.be/kmM3CjMPuz8?t=657
        if (getCardListSize() < 31) {
            String msg = "deck is too small to cut!";
            LOG.error(msg);
            throw new IllegalStateException(msg);
        }
        int floor = 14;
        int ceiling = getCardListSize() - 15;
        int cutIndex = getRandomIntForRange(floor, ceiling);
        cut(cutIndex);

        // insert cut card between 65% and 75% into the deck
        Double floorD = getCardListSize() * 0.65;
        Double ceilingD = getCardListSize() * 0.75;
        int insertIndex = getRandomIntForRange(floorD.intValue(), ceilingD.intValue());
        insert(cutCard, insertIndex);
    }


}
