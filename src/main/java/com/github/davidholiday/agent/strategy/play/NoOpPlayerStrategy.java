package com.github.davidholiday.agent.strategy.play;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;

public class NoOpPlayerStrategy extends PlayerStrategy {

    public static final String NAME = "NO_OP_PLAYER_STRATEGY";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Action evaluateHandForInsurance(Hand hand, int count, ActionToken actionToken) {
        return Action.DECLINE_INSURANCE;
    }

    @Override
    public Action evaluateForSurrender(Hand hand, int count, ActionToken actionToken) {
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
}
