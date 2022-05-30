package com.github.davidholiday.agent.strategy.play;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;

import java.util.stream.Collectors;

public abstract class DealerStrategy implements PlayStrategy {

    @Override
    public Action evaluateHand(Hand hand, int count, ActionToken actionToken) {
        if (actionToken.getRuleSet().isEmpty()) {
            throw new IllegalArgumentException("DealerStrategy requires RuleSet in actionToken to be populated.");
        }
        Action action = evaluateForSoft(hand, actionToken);
        if (action != Action.NONE) { return action; }

        return evaluateForHard(hand, actionToken);
    }

    /*
    evaluate for blackjack
     */

    public abstract Action evaluateForSoft(Hand hand, ActionToken actionToken);

    public abstract Action evaluateForHard(Hand hand, ActionToken actionToken);


}
