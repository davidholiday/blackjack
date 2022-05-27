package com.github.davidholiday.agent.strategy.count;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.ActionToken;

public interface CountStrategy {

    public String getName();

    public int updateCount(Hand hand, ActionToken actionToken);

}
