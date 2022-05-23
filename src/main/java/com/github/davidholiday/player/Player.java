package com.github.davidholiday.player;

import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.player.strategy.count.CountStrategy;
import com.github.davidholiday.player.strategy.play.PlayStrategy;

public class Player extends Agent {

    private Hand hand = new Hand();

    public Player(CountStrategy countStrategy, PlayStrategy playStrategy) {
        super(countStrategy, playStrategy);
    }

    @Override
    public AgentAction act(Game.GamePublic gamePublic) {

        this.updateCount(hand, gamePublic);

        Action action = this.getNextAction(hand, gamePublic);

        // if action is WAGER
        // this.wager(gamePublic);

        // if action is TAKE_CARD
        // take cards from gamePublic object
        // make sure to update count

        /*
        build AgentAction obj

        player hand needs to be a part of this object
         */

        return null;
    }
}
