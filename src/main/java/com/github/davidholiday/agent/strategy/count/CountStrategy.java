package com.github.davidholiday.agent.strategy.count;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.HandCollection;
import com.github.davidholiday.game.ActionToken;

import java.util.List;

public abstract class CountStrategy {

    private int shoeDeckSize;

    private int initialCount;

    double lastAnteWager = 0;

    public CountStrategy(int shoeDeckSize) {
        if (shoeDeckSize < 0 || shoeDeckSize > 8) {
            throw new IllegalArgumentException("shoe size should be between one and eight decks!");
        }

        this.shoeDeckSize = shoeDeckSize;
        resetCount();
    }

    public abstract void resetCount();

    public abstract int getCount();

    public abstract String getName();

    public abstract int updateCount(HandCollection handCollection, ActionToken actionToken);

    public abstract double getWager(int count, ActionToken actionToken);

    public abstract double getInsuranceBet(Hand hand, int count, ActionToken actionToken);

    public abstract double getLastAnteWager();

}
