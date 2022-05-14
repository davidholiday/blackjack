package com.github.davidholiday;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Card {

    private final CardType cardType;
    private final CardSuit cardSuit;

    public Card(CardType cardType, CardSuit cardSuit) {
        this.cardType = cardType;
        this.cardSuit = cardSuit;
    }

    public CardType getCardType() { return cardType; }
    public CardSuit getCardSuit() { return cardSuit; }
    public List<Integer> getNumericCardValue() {
        return Arrays.asList(cardType.getValues())
                     .stream()
                     .map((x) -> x.getValue())
                     .collect(Collectors.toList());
    }

    public String toString() { return cardType.toString() + ":" + cardSuit.toString(); }

}