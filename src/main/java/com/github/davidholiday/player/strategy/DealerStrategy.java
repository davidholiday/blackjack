package com.github.davidholiday.player.strategy;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.Rule;

import java.util.Optional;
import java.util.Set;

public abstract class DealerStrategy implements Strategy {

    @Override
    public Action evaluateHand(Hand hand, Card dealerUpCard, Set<Rule> ruleSet, Optional<Integer> count) {
        Action action = evaluateForSoft(hand, ruleSet);
        if (action != Action.NONE) { return action; }

        return evaluateForHard(hand, ruleSet);
    }

    /*
    evaluate for blackjack
     */

    public abstract Action evaluateForSoft(Hand hand, Set<Rule> ruleSet);

    public abstract Action evaluateForHard(Hand hand, Set<Rule> ruleSet);


}
