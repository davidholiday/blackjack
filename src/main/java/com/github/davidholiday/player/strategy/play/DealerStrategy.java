package com.github.davidholiday.player.strategy.play;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.Game;

import java.util.Map;
import java.util.Optional;

public abstract class DealerStrategy implements PlayStrategy {

    @Override
    public Action evaluateHand(Hand hand, Optional<Map<String, Integer>> count, Game.GameStateToken gamePublic) {
        Action action = evaluateForSoft(hand, gamePublic);
        if (action != Action.NONE) { return action; }

        return evaluateForHard(hand, gamePublic);
    }

    /*
    evaluate for blackjack
     */

    public abstract Action evaluateForSoft(Hand hand, Game.GameStateToken gamePublic);

    public abstract Action evaluateForHard(Hand hand, Game.GameStateToken gamePublic);


}
