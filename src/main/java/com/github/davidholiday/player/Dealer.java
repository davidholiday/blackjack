package com.github.davidholiday.player;

import com.github.davidholiday.cardcollection.DiscardTray;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.Shoe;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.player.strategy.count.CountStrategy;
import com.github.davidholiday.player.strategy.play.PlayStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dealer extends Agent {

    private static final Logger LOG = LoggerFactory.getLogger(Dealer.class);

    private final Shoe shoe;

    private final DiscardTray discardTray = new DiscardTray();

    private Hand hand = new Hand();


    public Dealer(CountStrategy countStrategy, PlayStrategy playStrategy, Shoe shoe) {
        super(countStrategy, playStrategy, Integer.MAX_VALUE);
        this.shoe = shoe;
    }

    @Override
    public AgentAction act(Game.GamePublic gamePublic) {
        Action action = this.getNextAction(hand, gamePublic);
        /*
        build AgentAction obj
         */

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
