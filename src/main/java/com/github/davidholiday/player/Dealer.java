package com.github.davidholiday.player;

import com.github.davidholiday.cardcollection.DiscardTray;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.Shoe;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.game.RuleSet;
import com.github.davidholiday.player.strategy.count.CountStrategy;
import com.github.davidholiday.player.strategy.count.NoCountStrategy;
import com.github.davidholiday.player.strategy.play.PlayStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Dealer extends Agent {

    private static final Logger LOG = LoggerFactory.getLogger(Dealer.class);

    private final Shoe shoe;

    private final DiscardTray discardTray = new DiscardTray();

    public Dealer(PlayStrategy playStrategy, Shoe shoe) {
        super(new NoCountStrategy(), playStrategy, Integer.MAX_VALUE);
        this.shoe = shoe;
    }


    public void start(RuleSet ruleSet, Map<AgentPosition, Player> playerMap) {


        // it makes sense to have the dealer handle the interchanges. if you have Game class do it, then
        // GAME class has to worry about being a message broker. In this case that's overkill because
        // all interactions in this game are through the dealer...
//        // look through actionDequeMap keys() to
//        //   - deal
//        //   - BJ + Insurance?
//        //   - then to handle each player hand
//        //     - make sure to record each interaction in the actionDequeMap
//        //   - then handle own hand if applicable
//        //


        // TODO this needs to return some kind of flight recording to GAME for serialization

    }





    @Override
    public ActionToken act(ActionToken actionToken) {

        // switch/case on action
        //
        // in in 'deal' mode, evaluate

        return null;
    }

    @Override
    public void updateBankroll(double updateBy) {
        if (getBankroll() + updateBy < 0) {
            LOG.info("Dealer bankroll ruin. Resetting to: " + Double.MAX_VALUE);
            super.updateBankroll(Double.MAX_VALUE);
        }
    }

    public int getShoeSize() { return shoe.getCardListSize(); }
}
