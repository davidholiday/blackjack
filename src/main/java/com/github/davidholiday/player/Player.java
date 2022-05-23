package com.github.davidholiday.player;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.game.Rule;
import com.github.davidholiday.player.strategy.count.CountStrategy;
import com.github.davidholiday.player.strategy.play.PlayStrategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Player extends Agent {

    private Hand hand = new Hand();

    public Player(CountStrategy countStrategy, PlayStrategy playStrategy) {
        super(countStrategy, playStrategy);
    }

    @Override
    public AgentAction act(Game.GamePublic gamePublic) {
        this.updateCount(hand, gamePublic);
        Action action = this.evaluateHand(hand, gamePublic);
        /*
        build AgentAction obj
         */

        return null;
    }
}
