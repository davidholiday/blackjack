package com.github.davidholiday.player;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.DiscardTray;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.Shoe;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.game.Rule;
import com.github.davidholiday.player.strategy.count.CountStrategy;
import com.github.davidholiday.player.strategy.play.PlayStrategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
        Action action = this.evaluateHand(hand, gamePublic);
        /*
        build AgentAction obj
         */

        return null;
    }
}
