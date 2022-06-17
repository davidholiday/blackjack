package com.github.davidholiday.agent.strategy.count.speedcount;

import com.github.davidholiday.agent.strategy.count.CountStrategy;
import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardType;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.game.RuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeedCountConservativeCountStrategy extends SpeedCountStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(SpeedCountConservativeCountStrategy.class);

    public static final String NAME = "SPEED_COUNT_CONSERVATIVE_STRATEGY";

    public SpeedCountConservativeCountStrategy(RuleSet ruleSet, double baseWager) {
        super(ruleSet, baseWager);
    }


    @Override
    public String getName() { return NAME; }

    @Override
    public double getWager(ActionToken actionToken) {

        // pg 24 of the book - conservative/simple bet ramp
        switch (shoeDeckSize) {
            case 1:
                if (count < 30) {
                    lastAnteWager = baseWager;
                    return baseWager;
                }
                else if (count == 31) {
                    lastAnteWager = baseWager * 2;
                    return lastAnteWager;
                }
                else if (count >= 32) {
                    lastAnteWager = baseWager * 3;
                    return lastAnteWager;
                }
            case 2:
                if (count < 31) {
                    lastAnteWager = baseWager;
                    return lastAnteWager;
                }
                else if (count == 31) {
                    lastAnteWager = baseWager * 2;
                    return lastAnteWager;
                }
                else if (count >= 32) {
                    lastAnteWager = baseWager * 4;
                    return lastAnteWager;
                }
            case 4:
                if (count < 31) {
                    lastAnteWager = baseWager;
                    return lastAnteWager;
                }
                else if (count == 31) {
                    lastAnteWager = baseWager * 2;
                    return lastAnteWager;
                }
                else if (count == 32) {
                    lastAnteWager = baseWager * 3;
                    return lastAnteWager;
                }
                else if (count == 33) {
                    lastAnteWager = baseWager * 4;
                    return lastAnteWager;
                }
                else if (count >= 34) {
                    lastAnteWager = baseWager * 5;
                    return lastAnteWager;
                }
            case 6:
                if (count < 31) {
                    lastAnteWager = baseWager;
                    return lastAnteWager;
                }
                else if (count == 31) {
                    lastAnteWager = baseWager * 2;
                    return lastAnteWager;
                }
                else if (count == 32) {
                    lastAnteWager = baseWager * 4;
                    return lastAnteWager;
                }
                else if (count >= 33) {
                    lastAnteWager = baseWager * 8;
                    return lastAnteWager;
                }
            case 8:
                if (count < 31) {
                    lastAnteWager = baseWager;
                    return lastAnteWager;
                }
                else if (count == 31) {
                    lastAnteWager = baseWager * 2;
                    return lastAnteWager;
                }
                else if (count == 32) {
                    lastAnteWager = baseWager * 4;
                    return lastAnteWager;
                }
                else if (count >= 33) {
                    lastAnteWager = baseWager * 6;
                    return lastAnteWager;
                }
                else if (count == 34) {
                    lastAnteWager = baseWager * 8;
                    return lastAnteWager;
                }
                else if (count >= 35) {
                    lastAnteWager = baseWager * 10;
                    return lastAnteWager;
                }
            default:
                throw new IllegalStateException("there should only be shoe sizes of 1, 2, 4, 6, or 8 decks");
        }
    }

}
