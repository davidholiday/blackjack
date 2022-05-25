package com.github.davidholiday.player.strategy.count;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.game.Game;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CountStrategy {

    public String getName();

    public int updateCount(ActionToken actionToken);

}
