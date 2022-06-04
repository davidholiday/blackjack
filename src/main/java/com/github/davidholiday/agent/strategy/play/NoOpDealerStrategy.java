package com.github.davidholiday.agent.strategy.play;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;

public class NoOpDealerStrategy extends DealerStrategy {

    public static final String NAME = "NO_OP_DEALER_STRATEGY";

    @Override
    public Action evaluateForSoft(Hand hand, ActionToken actionToken) {
        return Action.NONE;
    }

    @Override
    public Action evaluateForHard(Hand hand, ActionToken actionToken) {
        return Action.NONE;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double getWager(int count, ActionToken actionToken) { return 0; }

    @Override
    public double getInsuranceBet(Hand hand, int count, ActionToken actionToken) { return 0; }


}
