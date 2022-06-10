package com.github.davidholiday.agent.strategy.count;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.HandCollection;
import com.github.davidholiday.game.ActionToken;

import java.util.List;

public interface CountStrategy {

    public int getCount();

    public String getName();

    public int updateCount(HandCollection handCollection, ActionToken actionToken);

}
