package com.github.davidholiday.player.strategy;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.Rule;

import java.util.Optional;
import java.util.Set;

public abstract class PlayerStrategy implements Strategy {


    @Override
    public Action evaluateHand(Hand hand, Card dealerUpCard, Set<Rule> ruleSet, Optional<Integer> count) {

        Action action = evaluateForSurrender(hand, dealerUpCard, ruleSet, count);
        if (action != Action.NONE) { return action; }

        action = evaluateForSplit(hand, dealerUpCard, ruleSet, count);
        if (action != Action.NONE) { return action; }

        action = evaluateForSoft(hand, dealerUpCard, ruleSet, count);
        if (action != Action.NONE) { return action; }

        return evaluateForHard(hand, dealerUpCard, ruleSet, count);
    }

    public abstract Action evaluateForSurrender(Hand hand, Card dealerUpCard, Set<Rule> ruleSet, Optional<Integer> count);

    public abstract Action evaluateForSplit(Hand hand, Card dealerUpCard, Set<Rule> ruleSet, Optional<Integer> count);

    public abstract Action evaluateForSoft(Hand hand, Card dealerUpCard, Set<Rule> ruleSet, Optional<Integer> count);

    public abstract Action evaluateForHard(Hand hand, Card dealerUpCard, Set<Rule> ruleSet, Optional<Integer> count);

    public abstract double wager(Hand hand, Card dealerUpCard, Set<Rule> ruleSet, Action action,Optional<Integer> count);

}
