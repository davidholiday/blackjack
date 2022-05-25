package com.github.davidholiday.player.strategy.play;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.game.Game;

import java.util.Map;
import java.util.Optional;

public abstract class DealerStrategy implements PlayStrategy {

    @Override
    public Action evaluateHand(Hand hand, int count, ActionToken actionToken) {
        if (actionToken.getRuleSet().isEmpty()) {
            throw new IllegalArgumentException("DealerStrategy requires RuleSet to be present in actionToken argument");
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
