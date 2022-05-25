package com.github.davidholiday.player;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.player.strategy.count.CountStrategy;
import com.github.davidholiday.player.strategy.play.PlayStrategy;

public class Player extends Agent {

    private Hand hand = new Hand();

    public Player(CountStrategy countStrategy, PlayStrategy playStrategy, int bankroll) {
        super(countStrategy, playStrategy, bankroll);
    }


    public Hand getHand() {
        return new Hand(hand);
    }

    @Override
    public ActionToken act(ActionToken actionToken) {
        return null;
    }
}
