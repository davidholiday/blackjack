package com.github.davidholiday.player.strategy.play;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.game.Game;

import java.util.Map;
import java.util.Optional;

public abstract class PlayerStrategy implements PlayStrategy {


    @Override
    public Action evaluateHand(Hand hand, int count, ActionToken actionToken) {
        if (actionToken.getRuleSet().isEmpty()) {
            throw new IllegalArgumentException("PlayerStrategy requires RuleSet to be present in actionToken argument");
        }
        Action action = evaluateForSurrender(hand, count, actionToken);
        if (action != Action.NONE) { return action; }

        action = evaluateForSplit(hand, count, actionToken);
        if (action != Action.NONE) { return action; }

        action = evaluateForSoft(hand, count, actionToken);
        if (action != Action.NONE) { return action; }

        return evaluateForHard(hand, count, actionToken);
    }

    public abstract Action evaluateForSurrender(Hand hand, int count, ActionToken actionToken);

    public abstract Action evaluateForSplit(Hand hand, int count, ActionToken actionToken);

    public abstract Action evaluateForSoft(Hand hand, int count, ActionToken actionToken);

    public abstract Action evaluateForHard(Hand hand, int count, ActionToken actionToken);

}
