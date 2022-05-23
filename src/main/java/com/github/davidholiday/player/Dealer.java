package com.github.davidholiday.player;

import com.github.davidholiday.cardcollection.DiscardTray;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.Shoe;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.player.strategy.count.CountStrategy;
import com.github.davidholiday.player.strategy.play.PlayStrategy;

public class Dealer extends Agent {

    private final Shoe shoe;

    private final DiscardTray discardTray = new DiscardTray();

    private Hand hand = new Hand();

    public Dealer(CountStrategy countStrategy, PlayStrategy playStrategy, int shoeSize) {
        super(countStrategy, playStrategy);
        shoe = new Shoe(shoeSize);
    }

    @Override
    public AgentAction act(Game.GamePublic gamePublic) {
        Action action = this.getNextAction(hand, gamePublic);
        /*
        build AgentAction obj
         */

        return null;
    }
}
