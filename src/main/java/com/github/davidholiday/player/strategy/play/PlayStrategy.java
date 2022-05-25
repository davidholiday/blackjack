package com.github.davidholiday.player.strategy.play;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.Game;

import java.util.Map;
import java.util.Optional;

public interface PlayStrategy {

    String getName();

    Action evaluateHand(Hand hand, Optional<Map<String, Integer>> count, Game.GameStateToken gamePublic);

    public abstract double wager(Optional<Map<String, Integer>> count, Game.GameStateToken gamePublic);

}
