package com.github.davidholiday.player;

import com.github.davidholiday.cardcollection.DiscardTray;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.Shoe;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.player.strategy.count.CountStrategy;
import com.github.davidholiday.player.strategy.count.NoCountStrategy;
import com.github.davidholiday.player.strategy.play.PlayStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.Map;
import java.util.Queue;

public class Dealer extends Agent {

    private static final Logger LOG = LoggerFactory.getLogger(Dealer.class);

    private final Shoe shoe;

    private final DiscardTray discardTray = new DiscardTray();

    public Dealer(PlayStrategy playStrategy, Shoe shoe) {
        super(new NoCountStrategy(), playStrategy, Integer.MAX_VALUE);
        this.shoe = shoe;
    }


    public Map<AgentPosition, Deque<Action>> dealRound(Map<AgentPosition, Deque<Action>> actionDequeMap) {
        // look through actionDequeMap keys() to
        //   - deal
        //   - then to handle each player hand
        //     - make sure to record each interaction in the actionDequeMap
        //   - then handle own hand if applicable
        //
        // then return actionDequeMap to caller for flight recording

        return null;
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
}
