package com.github.davidholiday.agent;

import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.agent.strategy.count.CountStrategy;
import com.github.davidholiday.agent.strategy.play.PlayStrategy;

public class Player extends Agent {


    public Player(CountStrategy countStrategy, PlayStrategy playStrategy, int bankroll) {
        super(countStrategy, playStrategy, bankroll);
    }

    @Override
    public ActionToken act(ActionToken actionToken) {
        updateCount(actionToken);

        switch (actionToken.getAction()) {
            case REQUEST_WAGER:
                return new ActionToken.Builder(actionToken)
                                      .withActionTarget(actionToken.getActionSource())
                                      .withActionSource(actionToken.getActionTarget())
                                      .withAction(Action.WAGER)
                                      .withOfferedMoney(wager(actionToken))
                                      .build();

        }

        return null;
    }
}
