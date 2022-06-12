package com.github.davidholiday.agent.strategy.play;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;

public class NoOpDealerStrategy extends DealerStrategy {

    public static final String NAME = "NO_OP_DEALER_STRATEGY";

    @Override
    public Action evaluateForSoft(Hand hand, ActionToken actionToken) {
        if (hand.isSoft() == false) { return Action.NONE; }
        else { return Action.STAND; }
    }

    @Override
    public Action evaluateForHard(Hand hand, ActionToken actionToken) { return Action.STAND; }

    @Override
    public String getName() { return NAME; }

}
