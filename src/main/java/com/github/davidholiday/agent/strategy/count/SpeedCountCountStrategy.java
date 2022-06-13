package com.github.davidholiday.agent.strategy.count;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardType;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.HandCollection;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpeedCountCountStrategy extends CountStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(SpeedCountCountStrategy.class);

    public static final String NAME = "SPEED_COUNT_STRATEGY";

    private int numHands = 0;

    public SpeedCountCountStrategy(int shoeDeckSize) {
        super(shoeDeckSize);
        resetCount();
    }

    @Override
    public void resetCount() {
        playerHandMaps.clear();
        numHands = 0;
        count = getInitialCount();
    }



    @Override
    public String getName() { return NAME; }

    @Override
    public double getWager(ActionToken actionToken) {

LOG.warn("{} has count of: {}", actionToken.getActionTarget(), count);

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

    @Override
    public double getInsuranceBet(Hand hand, ActionToken actionToken) { return 0; }

    @Override
    public double getLastAnteWager() { return lastAnteWager; }

    @Override
    public int updateCount(ActionToken actionToken) {



        return count;
    }


    private int getInitialCount() {
        switch (super.shoeDeckSize) {
            case 1:
                // fall into TWO - though we're ignoring a special case for initial count for now...
            case 2:
                return 30;
            case 3:
                // these don't exist I think but whatevs...
                // fall into FOUR
            case 4:
                return 29;
            case 5:
                // these don't exist I think but whatevs...
                // fall into SIX
            case 6:
                return 27;
            case 7:
                // these don't exist I think but whatevs...
                // fall into EIGHT
            case 8:
                return  26;
            default:
                throw new IllegalStateException("shoe deck size should be between one and eight decks!");
        }
    }

}
