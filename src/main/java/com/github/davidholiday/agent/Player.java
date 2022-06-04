package com.github.davidholiday.agent;

import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.agent.strategy.count.CountStrategy;
import com.github.davidholiday.agent.strategy.play.PlayStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Player extends Agent {

    private static final Logger LOG = LoggerFactory.getLogger(Player.class);

    public Player(CountStrategy countStrategy, PlayStrategy playStrategy, int bankroll) {
        super(countStrategy, playStrategy, bankroll);
    }

    @Override
    public ActionToken act(ActionToken actionToken) {
        updateCount(actionToken);

        Action nextAction = getNextAction(actionToken);
        switch (nextAction) {
            case DEALER_NEXT_ACTION:
                return actionToken.getDealerNextActionToken();
            case SUBMIT_WAGER:
                double wager = getWager(actionToken);
                return getOfferMoneyActionToken(actionToken, nextAction, wager);
            case TAKE_CARD:
                addCardsToHand(actionToken.getOfferedCards());
                return actionToken.getDealerNextActionToken();
            case TAKE_INSURANCE:
                double insuranceWager = getInsuranceBet(actionToken);
                return getOfferMoneyActionToken(actionToken, nextAction, insuranceWager);
            case TAKE_MONEY:
                updateBankroll(actionToken.getOfferedMoney());
                return actionToken.getDealerNextActionToken();
            case SURRENDER:
                //
            case SPLIT:
                //
            case DOUBLE_DOWN:
                //
            case HIT:
                //
            case STAND:
                //
            case NONE:
                return getNextActionToken(actionToken, nextAction);
        }

        LOG.warn("something went wrong - we are in a code path we should not be in. sending end game token... ");
        return ActionToken.getEndGameActionToken();
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
