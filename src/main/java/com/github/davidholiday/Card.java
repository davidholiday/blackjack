package com.github.davidholiday;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.github.davidholiday.CardType.ACE;

public class Card {

    private final CardType cardType;
    private final CardSuit cardSuit;
    private final List<CardValue> cardValues = new ArrayList<>();

    public Card(CardType cardType, CardSuit cardSuit, List<CardValue> cardValues) {
        this.cardType = cardType;
        this.cardSuit = cardSuit;

        if (cardType == ACE) { assert cardValues.size() == 2;}
        else { assert cardValues.size() == 1; }

        switch (cardType) {
            case ACE:
                assert cardValues.contains(CardValue.ONE);
                assert cardValues.contains(CardValue.ELEVEN);
                break;
            case TWO:
                assert cardValues.contains(CardValue.TWO);
                break;
            case THREE:
                assert cardValues.contains(CardValue.THREE);
                break;
            case FOUR:
                assert cardValues.contains(CardValue.FOUR);
                break;
            case FIVE:
                assert cardValues.contains(CardValue.FIVE);
                break;
            case SIX:
                assert cardValues.contains(CardValue.SIX);
                break;
            case SEVEN:
                assert cardValues.contains(CardValue.SEVEN);
                break;
            case EIGHT:
                assert cardValues.contains(CardValue.EIGHT);
                break;
            case NINE:
                assert cardValues.contains(CardValue.NINE);
                break;
            case TEN:
                assert cardValues.contains(CardValue.TEN);
            case JACK:
                assert cardValues.contains(CardValue.TEN);
            case QUEEN:
                assert cardValues.contains(CardValue.TEN);
            case KING:
                assert cardValues.contains(CardValue.TEN);
                break;
            case JOKER:
                assert cardValues.contains(CardValue.ZERO);
            case CUT:
                assert cardValues.contains(CardValue.ZERO);
                break;
        }

        this.cardValues.addAll(cardValues);
    }

    public CardType getCardType() { return cardType; }
    public CardSuit getCardSuit() { return cardSuit; }
    public List<CardValue> getCardValues() {  return cardValues.stream().collect(Collectors.toList()); }

}
