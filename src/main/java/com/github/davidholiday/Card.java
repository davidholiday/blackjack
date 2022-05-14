package com.github.davidholiday;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.davidholiday.CardType.*;

public class Card {

    private final CardType cardType;
    private final CardSuit cardSuit;

    public Card(CardType cardType, CardSuit cardSuit) {
        this.cardType = cardType;
        this.cardSuit = cardSuit;
    }

    public CardType getCardType() { return cardType; }
    public CardSuit getCardSuit() { return cardSuit; }
    public int[] getNumericCardValue() {
        int value = 0;
        for (CardType cardValue : this.cardType.values()) {
            
        }
    }

}
