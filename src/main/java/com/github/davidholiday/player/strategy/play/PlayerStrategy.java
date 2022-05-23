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

public abstract class PlayerStrategy implements PlayStrategy {


    @Override
    public Action evaluateHand(Hand hand, Optional<Map<String, Integer>> count, Game.GamePublic gamePublic) {

        Action action = evaluateForSurrender(hand, count, gamePublic);
        if (action != Action.NONE) { return action; }

        action = evaluateForSplit(hand, count, gamePublic);
        if (action != Action.NONE) { return action; }

        action = evaluateForSoft(hand, count, gamePublic);
        if (action != Action.NONE) { return action; }

        return evaluateForHard(hand, count, gamePublic);
    }

    public abstract Action evaluateForSurrender(Hand hand, Optional<Map<String, Integer>> count, Game.GamePublic gamePublic);

    public abstract Action evaluateForSplit(Hand hand, Optional<Map<String, Integer>> count, Game.GamePublic gamePublic);

    public abstract Action evaluateForSoft(Hand hand, Optional<Map<String, Integer>> count, Game.GamePublic gamePublic);

    public abstract Action evaluateForHard(Hand hand, Optional<Map<String, Integer>> count, Game.GamePublic gamePublic);

}
