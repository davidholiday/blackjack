package com.github.davidholiday.agent;

import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.agent.strategy.count.CountStrategy;
import com.github.davidholiday.agent.strategy.play.PlayStrategy;

public class Player extends Agent {

    private double current_wager = 0;

    public Player(CountStrategy countStrategy, PlayStrategy playStrategy, int bankroll) {
        super(countStrategy, playStrategy, bankroll);
    }

    @Override
    public ActionToken act(ActionToken actionToken) {
        updateCount(actionToken);

        switch (actionToken.getAction()) {

            case REQUEST_WAGER:
                current_wager = wager(actionToken);
                return new ActionToken.Builder(actionToken)
                                      .withActionTarget(actionToken.getActionSource())
                                      .withActionSource(actionToken.getActionTarget())
                                      .withAction(Action.SUBMIT_WAGER)
                                      .withOfferedMoney(wager(actionToken))
                                      .build();

            case TAKE_CARD:
                addCardsToHand(actionToken.getOfferedCards());
                return ActionToken.getDealerNextActionToken(actionToken);
            case OFFER_INSURANCE:

            case REQUEST_PLAY:
                return  getNextPlay(actionToken);

            // on BUST or end of game reset current_wager
        }

        return null;
    }


}
