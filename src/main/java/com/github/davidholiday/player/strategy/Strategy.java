package com.github.davidholiday.player.strategy;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.Rule;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface Strategy {

    Optional<Integer> updateCount(Optional<Integer> currentCount, List<Card> cardsToCount);

    Action evaluateHand(Hand hand, Card dealerUpCard, Set<Rule> ruleSet, Optional<Integer> count);

}
