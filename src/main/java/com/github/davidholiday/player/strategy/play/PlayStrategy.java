package com.github.davidholiday.player.strategy.play;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.game.Rule;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PlayStrategy {

    String getName();

    Action evaluateHand(Hand hand, Optional<Map<String, Integer>> count, Game.GamePublic gamePublic);

    public abstract double wager(Optional<Map<String, Integer>> count, Game.GamePublic gamePublic);

}
