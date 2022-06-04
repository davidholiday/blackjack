package com.github.davidholiday.cardcollection;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardType;
import com.github.davidholiday.card.CardValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class Hand extends CardCollection {

    private static final Logger LOG = LoggerFactory.getLogger(Deck.class);

    private int handValue;

    private int aceSpecialHandValue;

    private boolean isBlackJack;

    private boolean isBust;

    private boolean isSoft;

    private boolean isPair;

    private boolean isTwentyOne;

    public Hand() {}

    public Hand(List<Card> cards) {
        addCards(cards);
    }

    public Hand(Hand hand) {
        List<Card> cardList = hand.getAllCards(false);
        addCards(cardList);
    }

    @Override
    public void addCards(List<Card> cards) {
        super.addCards(cards);
        updateHandValue();
    }

    public boolean isBlackJack() { return isBlackJack; }

    public boolean isBust() { return isBust; }

    public boolean isSoft() { return isSoft; }

    public boolean isPair() { return isPair; }

    public boolean isTwentyOne() { return isTwentyOne; }

    public String toStringFull() {
        return super.toString() + " >> " + handValue + ":" + aceSpecialHandValue +
                " >> is soft: " + isSoft() +
                " >> is pair: " + isPair() +
                " >> is blackjack: " + isBlackJack() +
                " >> is bust: " + isBust() +
                " >> is 21: " + isTwentyOne();
    }

    @Override
    public String toString() {
        return super.toString() + " >> " + handValue + ":" + aceSpecialHandValue;
    }

    public void updateHandValue() {
        int newHandValue = 0;
        int newAceSpecialHandValue = 0;
        for (Card card : getAllCards(false)) {
            CardValue[] cardValues = card.getCardType().getValues();
            // only one ACE in any given hand can be an '11'.
            // moreover, ACE is the only card in the deck that has more than one value.
            // therefore, the 'aceSpecialHandValue' is the hand value wherein one of the
            //   ACESs is valued at (11) instead of (1)
            if (card.getCardType() == CardType.ACE && newAceSpecialHandValue == newHandValue) {
                assert cardValues[1] == CardValue.ELEVEN;
                newAceSpecialHandValue += cardValues[1].getValue();
            } else if (card.getCardType() == CardType.ACE && newAceSpecialHandValue != newHandValue) {
                assert cardValues[0] == CardValue.ONE;
                newAceSpecialHandValue += cardValues[0].getValue();
            } else if (card.getCardType() != CardType.ACE) {
                newAceSpecialHandValue += cardValues[0].getValue();
            }
            newHandValue += cardValues[0].getValue();
        }

        this.handValue = newHandValue;
        this.aceSpecialHandValue = newAceSpecialHandValue;

        updateStateBooleans();
    }

    private void updateStateBooleans() {
        isPair = updateIsPair();
        isSoft = updateIsSoft();
        isBlackJack = updateIsBlackJack();
        isBust = updateIsBust();
        isTwentyOne = updateIsTwentyOne();
    }

    private boolean updateIsPair() {
        if (getCardListSize() != 2) { return false; }

        Card firstCard = peek(2).get(0);
        Card secondCard = peek(2).get(1);
        if (firstCard.getCardType() == secondCard.getCardType()) { return true; }
        else { return false; }
    }

    private boolean updateIsSoft() {
        boolean isAceInHand = (handValue == aceSpecialHandValue) == false;
        boolean isSpecialHandValueBust = aceSpecialHandValue > 21;
        if (isAceInHand && isSpecialHandValueBust == false) { return true; }
        else { return false; }
    }

    private boolean updateIsBlackJack() {
        if (getCardListSize() != 2) { return false; }
        if (aceSpecialHandValue != 21) { return false; }
        else { return true; }
    }

    private boolean updateIsBust() {
        boolean isNoAceBust = handValue > 21 && aceSpecialHandValue == 0 ;
        boolean isWithAceBust = handValue > 21 && aceSpecialHandValue > 21;
        if ( isNoAceBust || isWithAceBust) { return true; }
        else { return false; }
    }

    private boolean updateIsTwentyOne() {
        if (handValue == 21 | aceSpecialHandValue == 21) { return true; }
        else { return false; }
    }


    // ty SO https://stackoverflow.com/a/27609
    @Override
    public boolean equals(Object o) {
        List<Card> localCardList = getAllCards(false);
        List<Card> otherCardList = ((Hand) o).getAllCards(false);

        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        if (localCardList.size() != otherCardList.size()) { return false; }

        for (Card card : localCardList) {
            if (otherCardList.contains(card) == false) { return false; }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAllCards(false));
    }

}
