package com.github.davidholiday.agent.strategy.count;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardType;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.HandCollection;
import com.github.davidholiday.game.ActionToken;

import java.util.HashMap;
import java.util.Map;

public abstract class CountStrategy {

    int shoeDeckSize;

    int count = 0;

    double lastAnteWager = 0;

    double baseWager = 10;

    Map<Integer, CardType> seenCardTypesMap = new HashMap<>();

    public CountStrategy(int shoeDeckSize) {
        if (shoeDeckSize < 0 || shoeDeckSize > 8) {
            throw new IllegalArgumentException("shoe size should be between one and eight decks!");
        }

        this.shoeDeckSize = shoeDeckSize;
        resetCount();
    }

    public abstract void resetCount();

    public abstract String getName();

    public abstract double getWager(ActionToken actionToken);

    public abstract double getInsuranceBet(Hand hand, ActionToken actionToken);

    public abstract double getLastAnteWager();

    public int getCount() { return count; }

    public int updateCount(HandCollection handCollection, ActionToken actionToken) {

        if (actionToken.getDiscardTrayCardSize() == 0) {
            resetCount();
        }

        return count;
    }

}
