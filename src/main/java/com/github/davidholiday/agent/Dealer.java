package com.github.davidholiday.agent;

import com.github.davidholiday.cardcollection.DiscardTray;
import com.github.davidholiday.cardcollection.Shoe;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.agent.strategy.count.NoCountStrategy;
import com.github.davidholiday.agent.strategy.play.PlayStrategy;
import com.github.davidholiday.util.GeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dealer extends Agent {

    private static final Logger LOG = LoggerFactory.getLogger(Dealer.class);

    private final Shoe shoe;

    private final DiscardTray discardTray = new DiscardTray();

    public Dealer(PlayStrategy playStrategy, Shoe shoe) {
        super(new NoCountStrategy(), playStrategy, Integer.MAX_VALUE);
        this.shoe = shoe;
    }

    @Override
    public ActionToken act(ActionToken actionToken) {

        // switch/case on action
        //
        // in in 'deal' mode, evaluate
        switch (actionToken.getAction()) {
            case GAME_START:
                return new ActionToken.Builder(actionToken)
                                      .withActionSource(AgentPosition.DEALER)
                                      .withActionTarget(AgentPosition.GAME)
                                      .withAction(Action.GAME_END)
                                      .build();
        }

        Action action = getNextPlay(actionToken);


        return null;
    }

    @Override
    public void updateBankroll(double updateBy) {
        if (getBankroll() + updateBy < 0) {
            LOG.info("Dealer bankroll ruin. Resetting to: " + Double.MAX_VALUE);
            super.updateBankroll(Double.MAX_VALUE);
        }
    }

    public int getShoeDeckSize() { return shoe.getCardListSize() / GeneralUtils.DECK_SIZE_NO_JOKERS; }

    public int getShoeCardSize() { return shoe.getCardListSize(); }

    public int getDiscardTrayDeckSize() { return discardTray.getCardListSize() / GeneralUtils.DECK_SIZE_NO_JOKERS; }

    public int getDiscardTrayCardSize() {return discardTray.getCardListSize(); }
}
