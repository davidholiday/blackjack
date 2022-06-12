package com.github.davidholiday.agent.strategy.count;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.HandCollection;
import com.github.davidholiday.game.ActionToken;

public class NoCountStrategy extends CountStrategy {

    public static final String NAME = "NO_COUNT_STRATEGY";

    public NoCountStrategy(int shoeDeckSize) {
        super(shoeDeckSize);
    }

    @Override
    public void resetCount() { /* noop */ }

    @Override
    public int getCount() { return 0; }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int updateCount(HandCollection handCollection, ActionToken actionToken) {
        return 0;
    }

    @Override
    public double getWager(int count, ActionToken actionToken) {
        lastAnteWager = 10;
        return 10;
    }

    @Override
    public double getInsuranceBet(Hand hand, int count, ActionToken actionToken) { return 0; }

    @Override
    public double getLastAnteWager() { return lastAnteWager; }
}
