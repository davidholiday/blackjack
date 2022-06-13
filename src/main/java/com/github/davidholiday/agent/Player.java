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

        // if we're here it's because we're at the top of a round, just after a reshuffle has occurred.
        if (actionToken.getAction() == Action.REQUEST_WAGER && actionToken.getDiscardTrayCardSize() == 0) {
            resetCount();
            LOG.info("{} reset count to: {}", actionToken.getActionTarget(), getCount());
        }

        // if we're here it's because we're at the end of the round, prior to adjudication, and the DEALER is giving us
        // an opportunity to update our count before cards get cleared.
        if (actionToken.getAction() == Action.UPDATE_COUNT) {
            updateCount(actionToken);
            LOG.info("{} updated count to: {}", actionToken.getActionTarget(), getCount());
            return actionToken.getDealerNextActionToken();
        }

        // if we're still in here it means we're dealing with regular play. As such we can count on all the
        // action targets to be in the form of {AGENT_POSITION}$H{HAND_INDEX}
        int handIndex = getHandIndexFromAgentPosition(actionToken.getActionTarget());
        int numActiveHands = getHandCollection().size();

        Action nextAction = getNextAction(actionToken, handIndex);
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
                // the -1 on the handIndex is because here that number references the hand that is being MADE, not the
                // hand that needs to be split to MAKE that new hand.
                splitHandInHandCollection(actionToken.getOfferedCards(), handIndex - 1);
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
                        LOG.debug("turning off split pair evaluation for player hand: {}", actionToken.getActionTarget());
                        // re-evaluate with pair evaluation turned off
                        ActionToken actionTokenNoSplit = new ActionToken.Builder(actionToken)
                                                                        .withEvaluatePairForSplit(false)
                                                                        .build();

                        return act(actionTokenNoSplit);
                }


            case DOUBLE_DOWN:
                double doubleDownWager = getLastAnteWager();
                return getOfferMoneyActionToken(actionToken, nextAction, doubleDownWager);
            case HIT:
                Card firstCardInHand = getHand(handIndex).peek(1).get(0);
                boolean playerCanHitSplitAces = actionToken.getRuleSet()
                                                           .contains(Rule.PLAYER_CAN_HIT_SPLIT_ACES);

                // the strategy object has no way of knowing whether or not it is looking at a split hand...
                if (handIndex == 1) {
                    return getNextActionToken(actionToken, nextAction);
                } else if (handIndex == 1 && firstCardInHand.getCardType() == CardType.ACE && playerCanHitSplitAces) {
                    return getNextActionToken(actionToken, nextAction);
                } else if (handIndex == 1 && firstCardInHand.getCardType() != CardType.ACE) {
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
