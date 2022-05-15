package com.github.davidholiday.card;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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

    // ty SO https://stackoverflow.com/a/27609
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return cardType == card.cardType && cardSuit == card.cardSuit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardType, cardSuit);
    }
}
