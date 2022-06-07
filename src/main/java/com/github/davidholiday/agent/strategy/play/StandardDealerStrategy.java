package com.github.davidholiday.agent.strategy.play;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.game.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StandardDealerStrategy extends DealerStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(StandardDealerStrategy.class);

    public static final String NAME = "STANDARD_DEALER_STRATEGY";

    @Override
    public Action evaluateForSoft(Hand hand, ActionToken actionToken) {
        //LOG.info("dealer hand is: {}", hand.toStringFull());
        if (hand.isSoft() == false) { return Action.NONE; }

        boolean hitSoft17 = actionToken.getRuleSet().contains(Rule.DEALER_CAN_HIT_SOFT_17);

        if (hitSoft17 && hand.getAceSpecialHandValue() <= 17) { return Action.HIT; }
        else if (hitSoft17 == false && hand.getAceSpecialHandValue() < 17) { return Action.HIT; }
        else if (hand.isBust() == false){ return Action.STAND; }
        else { return Action.NONE; }

    }

    @Override
    public Action evaluateForHard(Hand hand, ActionToken actionToken) {
        //LOG.info("dealer hand is: {}", hand.toStringFull());
        if (hand.getHandValue() < 17) { return Action.HIT; }
        else if (hand.isBust() == false){ return Action.STAND; }
        else { return Action.NONE; }
    }

    @Override
    public String getName() { return NAME; }

    @Override
    public double getWager(int count, ActionToken actionToken) { return 0; }

    @Override
    public double getInsuranceBet(Hand hand, int count, ActionToken actionToken) { return 0; }

}
