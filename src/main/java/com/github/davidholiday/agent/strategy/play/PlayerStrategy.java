package com.github.davidholiday.agent.strategy.play;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class PlayerStrategy implements PlayStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerStrategy.class);

    @Override
    public Action evaluateHand(Hand hand, int count, ActionToken actionToken) {
        if (actionToken.getRuleSet().isEmpty()) {
            throw new IllegalArgumentException("PlayerStrategy requires RuleSet in actionToken to be populated.");
        }

        switch (actionToken.getAction()) {
            case REQUEST_WAGER:
                return Action.SUBMIT_WAGER;
            case TAKE_CARD:
                hand.addCards(actionToken.getOfferedCards());
                return Action.DEALER_NEXT_ACTION;
            case OFFER_INSURANCE:
                return evaluateHandForInsurance(hand, count, actionToken);
            case REQUEST_PLAY:
                Action action = evaluateForSurrender(hand, count, actionToken);
                if (action != Action.NONE) { return action; }

                action = evaluateForSplit(hand, count, actionToken);
                if (action != Action.NONE) { return action; }

                action = evaluateForSoft(hand, count, actionToken);
                if (action != Action.NONE) { return action; }

                return evaluateForHard(hand, count, actionToken);
        }

        LOG.warn("returning NONE action by way of code path we should not be in");
        return Action.NONE;
    }

    public abstract double wager(int count, ActionToken actionToken);

    public abstract Action evaluateHandForInsurance(Hand hand, int count, ActionToken actionToken);

    public abstract Action evaluateForSurrender(Hand hand, int count, ActionToken actionToken);

    public abstract Action evaluateForSplit(Hand hand, int count, ActionToken actionToken);

    public abstract Action evaluateForSoft(Hand hand, int count, ActionToken actionToken);

    public abstract Action evaluateForHard(Hand hand, int count, ActionToken actionToken);

}
