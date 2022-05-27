package com.github.davidholiday.agent.strategy.count;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.ActionToken;

public class NoCountStrategy implements CountStrategy {

    public static final String NAME = "NO_COUNT_STRATEGY";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int updateCount(Hand hand, ActionToken actionToken) {
        return 0;
    }
}