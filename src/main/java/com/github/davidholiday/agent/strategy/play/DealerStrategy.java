package com.github.davidholiday.agent.strategy.play;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class DealerStrategy implements PlayStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(DealerStrategy.class);

    @Override
    public Action evaluateHand(Hand hand, int count, ActionToken actionToken) {
        if (actionToken.getRuleSet().isEmpty()) {
            throw new IllegalArgumentException("DealerStrategy requires RuleSet in actionToken to be populated.");
        }

        switch (actionToken.getAction()) {
            case OFFER_CARDS:
                return Action.TAKE_CARD;
            case REQUEST_PLAY_DEALER:
                Action action = evaluateForSoft(hand, actionToken);
                if (action != Action.NONE) { return action; }

                action = evaluateForHard(hand, actionToken);
                if (action != Action.NONE) { return action; }
        }

        return Action.NONE;
    }

    /*
    evaluate for blackjack
     */

    public abstract Action evaluateForSoft(Hand hand, ActionToken actionToken);

    public abstract Action evaluateForHard(Hand hand, ActionToken actionToken);


}
