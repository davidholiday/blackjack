package com.github.davidholiday.agent.strategy.count.reko;

import com.github.davidholiday.agent.strategy.count.CountStrategy;
import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardType;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.game.RuleSet;

public abstract class reKoCountStrategy extends CountStrategy {

    public reKoCountStrategy(RuleSet ruleSet, double baseWager) {
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
    public int getInitialCount() {
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
