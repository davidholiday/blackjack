package com.github.davidholiday.agent.strategy.play;

import com.github.davidholiday.agent.AgentPosition;
import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardType;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.game.Rule;
import com.github.davidholiday.game.RuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicFourSixEightDeckPlayerStrategy extends PlayerStrategy{

    private static final Logger LOG = LoggerFactory.getLogger(BasicFourSixEightDeckPlayerStrategy.class);

    public static final String NAME = "BASIC_PLAYER_STRATEGY";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public double getWager(int count, ActionToken actionToken) { return 10; }

    @Override
    public double getInsuranceBet(Hand hand, int count, ActionToken actionToken) { return 0; }

    @Override
    public Action evaluateHandForInsurance(Hand hand, int count, ActionToken actionToken) { return Action.NONE; }

    @Override
    public Action evaluateForSurrender(Hand hand, int count, ActionToken actionToken) {
        if (hand.getCardListSize() != 2) { return Action.NONE; }

        RuleSet ruleSet = actionToken.getRuleSet();
        if (ruleSet.contains(Rule.PLAYER_CAN_EARLY_SURRENDER)
                || ruleSet.contains(Rule.PLAYER_CAN_LATE_SURRENDER)) {

            CardType dealerUpCardType = getDealerUpCard(actionToken).getCardType();
            boolean dealerCanHitSoft17 = ruleSet.contains(Rule.DEALER_CAN_HIT_SOFT_17);
            switch (hand.getHandValue()) {
                case 17:
                    if (dealerCanHitSoft17
                            && dealerUpCardType == CardType.ACE) {
                        return Action.SURRENDER;
                    }
                    break;
                case 16:
                    if (dealerUpCardType == CardType.NINE
                            || dealerUpCardType == CardType.TEN
                            || dealerUpCardType == CardType.JACK
                            || dealerUpCardType == CardType.QUEEN
                            || dealerUpCardType == CardType.KING
                            || dealerUpCardType == CardType.ACE) {

                        // a pair of 8s is treated differently than 16 that isn't a pair of 8s
                        if (hand.isPair() == false) {
                            return Action.SURRENDER;
                        }
                        if (hand.isPair() && dealerUpCardType == CardType.ACE) {
                            return Action.SURRENDER;
                        }
                    }
                    break;
                case 15:
                    if (dealerUpCardType == CardType.TEN
                            || dealerUpCardType == CardType.JACK
                            || dealerUpCardType == CardType.QUEEN
                            || dealerUpCardType == CardType.KING) {
                        return Action.SURRENDER;
                    }

                    if (dealerCanHitSoft17 && dealerUpCardType == CardType.ACE) {
                        return Action.SURRENDER;
                    }
            }
        }
        return Action.NONE;
    }

    @Override
    public Action evaluateForSplit(Hand hand, int count, ActionToken actionToken) {
        if (hand.isPair()) { return Action.STAND; }
        else { return Action.NONE; }
    }

    @Override
    public Action evaluateForSoft(Hand hand, int count, ActionToken actionToken) {
        if (hand.isSoft()) { return Action.STAND; }
        else { return Action.NONE; }
    }

    @Override
    public Action evaluateForHard(Hand hand, int count, ActionToken actionToken) {
        return Action.STAND;
    }


    private Card getDealerUpCard(ActionToken actionToken) {

        Hand dealerHand = actionToken.getPlayerHandMap()
                                     .get(AgentPosition.DEALER);

        if (dealerHand.getCardListSize() != 2) {
            throw new IllegalStateException("dealer should have exactly two cards when player is making plays!");
        }

        if (dealerHand.getAllCards(false).get(0).getCardType() != CardType.HIDDEN) {
            throw new IllegalStateException("dealer hole card should be hidden when player is making plays!");
        }

        if (dealerHand.getAllCards(false).get(1).getCardType() == CardType.HIDDEN) {
            throw new IllegalStateException("dealer up card should not be hidden when player is making plays!");
        }

        return dealerHand.getAllCards(false).get(1);
    }

}
