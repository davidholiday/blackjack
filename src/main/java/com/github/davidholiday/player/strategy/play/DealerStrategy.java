package com.github.davidholiday.player.strategy.play;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.game.Rule;
import com.github.davidholiday.player.strategy.play.PlayStrategy;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class DealerStrategy implements PlayStrategy {

    @Override
    public Action evaluateHand(Hand hand, Optional<Map<String, Integer>> count, Game.GamePublic gamePublic) {
        Action action = evaluateForSoft(hand, gamePublic);
        if (action != Action.NONE) { return action; }

        return evaluateForHard(hand, gamePublic);
    }

    /*
    evaluate for blackjack
     */

    public abstract Action evaluateForSoft(Hand hand, Game.GamePublic gamePublic);

    public abstract Action evaluateForHard(Hand hand, Game.GamePublic gamePublic);


}