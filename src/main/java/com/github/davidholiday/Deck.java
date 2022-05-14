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
            for (CardType cardType : CardType.values()) {

            }
        }
    }

}
