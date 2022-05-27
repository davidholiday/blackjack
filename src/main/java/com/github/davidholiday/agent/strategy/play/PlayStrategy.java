package com.github.davidholiday.agent.strategy.play;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;

public interface PlayStrategy {

    String getName();

    Action evaluateHand(Hand hand, int count, ActionToken actionToken);

    public abstract double wager(int count, ActionToken actionToken);

}
