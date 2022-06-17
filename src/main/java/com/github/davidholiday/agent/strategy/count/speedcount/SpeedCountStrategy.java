package com.github.davidholiday.agent.strategy.count.speedcount;

import com.github.davidholiday.agent.strategy.count.CountStrategy;
import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardType;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.game.RuleSet;

public abstract class SpeedCountStrategy extends CountStrategy {


    public SpeedCountStrategy(RuleSet ruleSet, double baseWager) {
        super(ruleSet, baseWager);
    }

    @Override
    public double getInsuranceBet(Hand hand, ActionToken actionToken) { return 0; }

    @Override
    public void updateCount(ActionToken actionToken) {
        int numHands = actionToken.getPlayerHandMap().size();
        for (Hand hand : actionToken.getPlayerHandMap().values()) {
            for (Card card : hand.getAllCards(false)) {
                if (card.getCardType() == CardType.TWO
                        || card.getCardType() == CardType.THREE
                        || card.getCardType() == CardType.FOUR
                        || card.getCardType() == CardType.FIVE
                        || card.getCardType() == CardType.SIX) {

                    count += 1;
                }
            }
        }

        count -= numHands;
    }

    public int getInitialCount() {
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
