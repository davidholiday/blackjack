package com.github.davidholiday.cardcollection;

import java.text.MessageFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.davidholiday.App;
import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardSuit;
import com.github.davidholiday.card.CardType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.davidholiday.util.MessageTemplates.getErrorMessage;

public class Deck extends CardCollection {

    private final int expectedFullDeckSize;

    private static final Logger LOG = LoggerFactory.getLogger(Deck.class);

    public Deck(boolean withJokers) {
        List<Card> cardList = makeDeck(withJokers);
        if (withJokers) { expectedFullDeckSize = 54; }
        else { expectedFullDeckSize = 52; }
        this.addCards(cardList);
    }

    public List<Card> makeDeck(boolean withJokers) {
        List<Card> rv = new ArrayList<>();

        for (CardSuit cardSuit : CardSuit.values()) {
            if (cardSuit == CardSuit.NONE) { continue; }
            for (CardType cardType : CardType.values()) {
                if (cardType == CardType.CUT || cardType == CardType.JOKER) { continue; }
                Card card = new Card(cardType, cardSuit);
                rv.add(card);
            }
        }

        if (withJokers) {
            rv.addAll(
                    Stream.of(
                            new Card(CardType.JOKER, CardSuit.NONE),
                            new Card(CardType.JOKER, CardSuit.NONE)
                    ).collect(Collectors.toList())
            );
        }

        if (App.RUNTIME_INFO.ASSERTIONS_ENABLED) { validateDeck(1, withJokers, rv); }
        return rv;
    }

    @Override
    public void addCards(List<Card> cardList) {
        int currentDeckSize = this.getCardListSize();
        int newDeckSize = cardList.size() + currentDeckSize;
        if (newDeckSize > this.expectedFullDeckSize) {
            String msg = MessageFormat.format(
                    "attempt to add too many cards to a deck with a max size of {0}", expectedFullDeckSize);
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        super.addCards(cardList);
    }

}
