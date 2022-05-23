package com.github.davidholiday.player.strategy.count;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.Game;

import java.util.Optional;

public interface CountStrategy {

    public String getName();

    public Optional<Integer> updateCount(Hand hand, Game.GamePublic gamePublic);

}
