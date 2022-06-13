package com.github.davidholiday.agent.strategy.count;

import com.github.davidholiday.App;
import com.github.davidholiday.agent.AgentPosition;
import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardType;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.HandCollection;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class SpeedCountStrategy extends CountStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(SpeedCountStrategy.class);

    public static final String NAME = "SPEED_COUNT_STRATEGY";

    public SpeedCountStrategy(int shoeDeckSize) {
        super(shoeDeckSize);
        resetCount();
    }

    @Override
    public void resetCount() {
        switch (super.shoeDeckSize) {
            case 1:
                // fall into TWO - though we're ignoring a special case for initial count for now...
            case 2:
                count = 30;
                break;
            case 3:
                // these don't exist I think but whatevs...
                // fall into FOUR
            case 4:
                count = 29;
                break;
            case 5:
                // these don't exist I think but whatevs...
                // fall into SIX
            case 6:
                count = 27;
                break;
            case 7:
                // these don't exist I think but whatevs...
                // fall into EIGHT
            case 8:
                count = 26;
                break;
            default:
                throw new IllegalStateException("shoe deck size should be between one and eight decks!");
        }
    }

    @Override
    public String getName() { return NAME; }


    @Override
    public int updateCount(HandCollection handCollection, ActionToken actionToken) {

        // on CLEAR_HAND is when we do our subtraction and set ourselves up to compute the next wager...
        if (actionToken.getAction() == Action.CLEAR_HAND) {
            Set<AgentPosition> agentsNoHandIds = new HashSet<>();

            for (AgentPosition agentPosition : actionToken.getPlayerHandMap().keySet()) {
                if (agentPosition == AgentPosition.DEALER) { continue; }
                String sourceAgentPositionNoHandIndex = agentPosition.toString().split("\\$")[0];
                agentsNoHandIds.add(AgentPosition.valueOf(sourceAgentPositionNoHandIndex));
            }

            count -= agentsNoHandIds.size();
        // otherwise we recompute the current count w/o the end of round modifier
        //
        // TODO there's probably a better way to keep a 'running count' but for now we'll just
        // TODO   recompute the 'running count' ever time as it's pretty easy to do...
        } else {
            for (Hand hand : handCollection.getHandList()) {
                for (Card card : hand.getAllCards(false)) {
                    super.seenCardTypesMap.put(card.hashCode(), card.getCardType());
                }
            }

            for (Hand hand : actionToken.getPlayerHandMap().values()) {
                for (Card card : hand.getAllCards(false)) {
                    super.seenCardTypesMap.put(card.hashCode(), card.getCardType());
                }
            }

            // we need to make sure to reset the count before adding more stuff to it. again - not the best
            // way to do this but it works for now
            resetCount();

            for (CardType cardType : seenCardTypesMap.values()) {
                if (cardType == CardType.TWO
                        || cardType == CardType.THREE
                        || cardType == CardType.FOUR
                        || cardType == CardType.FIVE
                        || cardType == CardType.SIX) {

                    count += 1;
                }

            }

        }

        return count;
    }

    @Override
    public double getWager(ActionToken actionToken) {

        LOG.info("{} has count of: {}", actionToken.getActionTarget(), count);

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

}
