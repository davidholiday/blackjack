package com.github.davidholiday.player;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.player.strategy.count.CountStrategy;
import com.github.davidholiday.player.strategy.play.PlayStrategy;

public class Player extends Agent {


    public Player(CountStrategy countStrategy, PlayStrategy playStrategy, int bankroll) {
        super(countStrategy, playStrategy, bankroll);
    }

    @Override
    public ActionToken act(ActionToken actionToken) {
        updateCount(actionToken);
        Action action = getNextPlay(actionToken);

        // switch/case based on action to build actionToken...

        return null;
    }
}
