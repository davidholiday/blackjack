package com.github.davidholiday.agent;

import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.agent.strategy.count.CountStrategy;
import com.github.davidholiday.agent.strategy.play.PlayStrategy;
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
                //
            case DOUBLE_DOWN:
                double doubleDownWager = getLastAnteWager();
                return getOfferMoneyActionToken(actionToken, nextAction, doubleDownWager);
            case HIT:
                return getNextActionToken(actionToken, nextAction);
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

}
