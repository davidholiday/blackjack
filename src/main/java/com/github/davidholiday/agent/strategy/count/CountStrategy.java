package com.github.davidholiday.agent.strategy.count;

import com.github.davidholiday.agent.AgentPosition;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.HandCollection;
import com.github.davidholiday.game.ActionToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class CountStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(CountStrategy.class);

    int shoeDeckSize;

    int count = 0;

    double lastAnteWager = 0;

    double baseWager = 10;

    List<Map<AgentPosition, Hand>> playerHandMaps = new ArrayList<>();

    public CountStrategy(int shoeDeckSize) {
        if (shoeDeckSize < 0 || shoeDeckSize > 8) {
            throw new IllegalArgumentException("shoe size should be between one and eight decks!");
        }

        this.shoeDeckSize = shoeDeckSize;
        resetCount();
    }

    public abstract void resetCount();

    public abstract String getName();

    public abstract double getWager(ActionToken actionToken);

    public abstract double getInsuranceBet(Hand hand, ActionToken actionToken);

    public abstract double getLastAnteWager();

    public abstract int updateCount(ActionToken actionToken);

    public int getCount() { return count; }

    // as play happens we want to update the 'current' playerHandMap with the latest information.
    //   at the end of the round we'll evaluate the count
    public void updateCurrentPlayerHandMap(ActionToken actionToken) {
        playerHandMaps.set(playerHandMaps.size()-1, actionToken.getPlayerHandMap());
    }


}
