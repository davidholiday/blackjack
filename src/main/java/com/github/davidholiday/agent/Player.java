package com.github.davidholiday.agent;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardType;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.agent.strategy.count.CountStrategy;
import com.github.davidholiday.agent.strategy.play.PlayStrategy;
import com.github.davidholiday.game.Rule;
import com.github.davidholiday.game.RuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Player extends Agent {

    private static final Logger LOG = LoggerFactory.getLogger(Player.class);

    public Player(CountStrategy countStrategy,
                  PlayStrategy playStrategy,
                  int bankroll,
                  RuleSet ruleSet,
                  AgentPosition agentPosition) {

        super(countStrategy, playStrategy, bankroll, ruleSet, agentPosition);
    }

    @Override
    public ActionToken act(ActionToken actionToken) {
        updateCount(actionToken);
        int handIndex = getHandIndexFromAgentPosition(actionToken.getActionTarget());
        int numActiveHands = getHandCollection().size();

        Action nextAction = getNextAction(actionToken, getCount());
        switch (nextAction) {
            case DEALER_NEXT_ACTION:
                return actionToken.getDealerNextActionToken();
            case SUBMIT_WAGER:
                double wager = getWager(actionToken);
                return getOfferMoneyActionToken(actionToken, nextAction, wager);
            case TAKE_CARD:
                addCardsToHandCollection(
                        actionToken.getOfferedCards(),
                        getHandIndexFromAgentPosition(actionToken.getActionTarget())
                );
                return actionToken.getDealerNextActionToken();
            case TAKE_CARDS_FOR_SPLIT:
                splitHandInHandCollection(actionToken.getOfferedCards(), handIndex);
                return actionToken.getDealerNextActionToken();
            case TAKE_INSURANCE:
                double insuranceWager = getInsuranceWager(
                        actionToken,
                        getHandIndexFromAgentPosition(actionToken.getActionTarget())
                );
                return getOfferMoneyActionToken(actionToken, nextAction, insuranceWager);
            case DECLINE_INSURANCE:
                return getNextActionToken(actionToken, nextAction);
            case TAKE_MONEY:
                updateBankroll(actionToken.getOfferedMoney());
                return actionToken.getDealerNextActionToken();
            case SURRENDER:
                return getNextActionToken(actionToken, nextAction);
            case SPLIT:
                // SPLIT is unique in that it's the only evaluation that relies on context (number of player hands in
                //   play) that the strategy object has no awareness of
                //
                boolean toTwoHandsOk = actionToken.getRuleSet().contains(Rule.PLAYER_CAN_RESPLIT_TO_TWO_HANDS);
                boolean toThreeHandsOk = actionToken.getRuleSet().contains(Rule.PLAYER_CAN_RESPLIT_TO_THREE_HANDS);
                boolean toFourHandsOk = actionToken.getRuleSet().contains(Rule.PLAYER_CAN_RESPLIT_TO_FOUR_HANDS);
                boolean playerCanResplitAcesOk = actionToken.getRuleSet().contains(Rule.PLAYER_CAN_RESPLIT_ACES);

                double offeredMoney = getLastAnteWager();

                switch(numActiveHands) {
                    case 1:
                        if (toTwoHandsOk || toThreeHandsOk || toFourHandsOk) {
                            updateBankroll(-offeredMoney);
                            return getOfferMoneyActionTokenForSplit(actionToken, nextAction, offeredMoney);
                        }
                    case 2:
                        if (toThreeHandsOk || toFourHandsOk) {
                            CardType handTwoCardType = actionToken.getPlayerHandMap()
                                                                  .get(actionToken.getActionTarget())
                                                                  .peek(1)
                                                                  .get(0)
                                                                  .getCardType();

                            if (handTwoCardType == CardType.ACE && playerCanResplitAcesOk) {
                                updateBankroll(-offeredMoney);
                                return getOfferMoneyActionTokenForSplit(actionToken, nextAction, offeredMoney);
                            }

                        }
                    case 3:
                        if (toFourHandsOk) {
                            CardType handThreeCardType = actionToken.getPlayerHandMap()
                                                                    .get(actionToken.getActionTarget())
                                                                    .peek(2)
                                                                    .get(0)
                                                                    .getCardType();

                            if (handThreeCardType == CardType.ACE && playerCanResplitAcesOk) {
                                updateBankroll(-offeredMoney);
                                return getOfferMoneyActionTokenForSplit(actionToken, nextAction, offeredMoney);
                            }
                        }
                    default:
                        // re-evaluate with pair evaluation turned off
                        actionToken.getPlayerHandMap().get(actionToken.getActionTarget()).disablePairEvaluation();
                        return act(actionToken);
                }


            case DOUBLE_DOWN:
                double doubleDownWager = getLastAnteWager();
                return getOfferMoneyActionToken(actionToken, nextAction, doubleDownWager);
            case HIT:
                Card firstCardInHand = getHand(handIndex).peek(1).get(0);
                boolean playerCanHitSplitAces = actionToken.getRuleSet().contains(Rule.PLAYER_CAN_HIT_SPLIT_ACES);

                // the strategy object has no way of knowing whether or not it is looking at a split hand...
                if (handIndex == 1) {
                    return getNextActionToken(actionToken, nextAction);
                } else if (firstCardInHand.getCardType() == CardType.ACE && playerCanHitSplitAces) {
                    return getNextActionToken(actionToken, nextAction);
                } else if (firstCardInHand.getCardType() != CardType.ACE) {
                    return getNextActionToken(actionToken, nextAction);
                }
                // fall into STAND
            case STAND:
                return getNextActionToken(actionToken, nextAction);
            case OFFER_CARDS_FOR_DISCARD_TRAY:
                return new ActionToken.Builder()
                                      .withAction(nextAction)
                                      .withOfferedCards(clearHands())
                                      .withActionSource(actionToken.getActionTarget())
                                      .withActionTarget(AgentPosition.DEALER)
                                      .build();
            case NONE:
                return getNextActionToken(actionToken, nextAction);
        }

        throw new IllegalStateException("something went wrong - we are in a code path we should not be in. ");
    }


    private ActionToken getNextActionToken(ActionToken actionToken, Action nextAction) {
        return new ActionToken.Builder(actionToken)
                              .withActionTarget(actionToken.getActionSource())
                              .withActionSource(actionToken.getActionTarget())
                              .withAction(nextAction)
                              .build();
    }

    ActionToken getOfferMoneyActionToken(ActionToken actionToken, Action nextAction, double offerMoneyAmount) {
        return new ActionToken.Builder(actionToken)
                .withActionTarget(actionToken.getActionSource())
                .withActionSource(actionToken.getActionTarget())
                .withAction(nextAction)
                .withOfferedMoney(offerMoneyAmount)
                .build();
    }

    ActionToken getOfferMoneyActionTokenForSplit(ActionToken actionToken, Action nextAction, double offerMoneyAmount) {
        int handIndex = getHandIndexFromAgentPosition(actionToken.getActionTarget());
        int newHandIndex = handIndex + 1;

        return new ActionToken.Builder(actionToken)
                .withActionTarget(actionToken.getActionSource())
                .withActionSource(getAgentPositionFromHandIndex(newHandIndex))
                .withAction(nextAction)
                .withOfferedMoney(offerMoneyAmount)
                .build();
    }

}
