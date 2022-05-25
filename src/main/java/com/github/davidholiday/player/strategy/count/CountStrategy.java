package com.github.davidholiday.player.strategy.count;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Game;

import java.util.Map;
import java.util.Optional;

public interface CountStrategy {

    public String getName();

    public Optional<Map<String, Integer>> updateCount(Hand hand, Game.GameStateToken gamePublic);

}
