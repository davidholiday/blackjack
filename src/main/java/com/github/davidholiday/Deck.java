package com.github.davidholiday;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Deck {

    private List<Card> deck;

    public Deck() {
        deck = new ArrayList<>();
        for (CardSuit cardSuit : CardSuit.values()) {
            if (cardSuit == CardSuit.NONE) { continue; }
            for (CardType cardType : CardType.values()) {
                if (cardType == CardType.JOKER || cardType == CardType.CUT) { continue; }
                for (CardValue cardValue : CardValue.values()) {
                    if (cardValue == CardValue.ZERO) { continue; }
                    if (cardType == CardType.ACE) {
                        List<CardValue> cardValues = Stream.of(CardValue.ONE, CardValue.ELEVEN)
                                                           .collect(Collectors.toList());
                    } else {

                    }
                    Card card = new Card(cardType, cardSuit, cardValues);
                    deck.add(card);
                }
            }
        }
    }

}
