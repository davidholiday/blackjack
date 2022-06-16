package com.github.davidholiday.agent.strategy.count;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardType;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.game.RuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class reKoStrategyModerateBetRamp extends CountStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(SpeedCountCountStrategy.class);

    public static final String NAME = "RE_KO_CONSERVATIVE_BET_STRATEGY";

    private int numHands = 0;

    public reKoStrategyModerateBetRamp(RuleSet ruleSet, double baseWager) {
        super(ruleSet, baseWager);
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

        // https://www.qfit.com/book/ModernBlackjackPage72.htm
        switch (shoeDeckSize) {
            case 1:
                if (count <= 0) {
                    lastAnteWager = baseWager;
                    return baseWager;
                }
                else if (count == 1) {
                    lastAnteWager = baseWager * 4;
                    return lastAnteWager;
                }
                else if (count >= 2) {
                    lastAnteWager = baseWager * 8;
                    return lastAnteWager;
                }
            case 2:
                if (count <= -1) {
                    lastAnteWager = baseWager;
                    return lastAnteWager;
                }
                else if (count == 0) {
                    lastAnteWager = baseWager * 2;
                    return lastAnteWager;
                }
                else if (count == 1) {
                    lastAnteWager = baseWager * 4;
                    return lastAnteWager;
                }
                else if (count == 2) {
                    lastAnteWager = baseWager * 6;
                    return lastAnteWager;
                }
                else if (count >= 3) {
                    lastAnteWager = baseWager * 8;
                    return lastAnteWager;
                }
            case 4:
                // fall into 6
            case 6:
                // fall into 8
            case 8:
                if (count <= -4) {
                    lastAnteWager = baseWager;
                    return lastAnteWager;
                }
                else if (count <= -2) {
                    lastAnteWager = baseWager * 2;
                    return lastAnteWager;
                }
                else if (count <= 0) {
                    lastAnteWager = baseWager * 4;
                    return lastAnteWager;
                }
                else if (count <= 2) {
                    lastAnteWager = baseWager * 6;
                    return lastAnteWager;
                }
                else {
                    lastAnteWager = baseWager * 8;
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
    public void updateCount(ActionToken actionToken) {
        int numHands = actionToken.getPlayerHandMap().size();
        for (Hand hand : actionToken.getPlayerHandMap().values()) {
            for (Card card : hand.getAllCards(false)) {
                if (card.getCardType() == CardType.TWO
                        || card.getCardType() == CardType.THREE
                        || card.getCardType() == CardType.FOUR
                        || card.getCardType() == CardType.FIVE
                        || card.getCardType() == CardType.SIX
                        || card.getCardType() == CardType.SEVEN) {

                    count += 1;
                } else if (card.getCardType() == CardType.TEN
                        || card.getCardType() == CardType.JACK
                        || card.getCardType() == CardType.QUEEN
                        || card.getCardType() == CardType.KING
                        || card.getCardType() == CardType.ACE) {

                    count -= 1;
                }
            }
        }

    }


    // https://www.qfit.com/book/ModernBlackjackPage71.htm
    private int getInitialCount() {
        switch (super.shoeDeckSize) {
            case 1:
                return -1;
            case 2:
                return -5;
            case 4:
                return -12;
            case 6:
                return -20;
            case 8:
                return  -27;
            default:
                throw new IllegalStateException("shoe deck size should be between one and eight decks!");
        }
    }

}
