package com.github.davidholiday.agent;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardType;
import com.github.davidholiday.cardcollection.DiscardTray;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.Shoe;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.agent.strategy.count.NoCountStrategy;
import com.github.davidholiday.agent.strategy.play.PlayStrategy;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.util.GeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Dealer extends Agent {

    private static final Logger LOG = LoggerFactory.getLogger(Dealer.class);

    private final Shoe shoe;

    private final DiscardTray discardTray = new DiscardTray();

    private final Map<AgentPosition, Double> playerWagerMap = new HashMap<>();

    private boolean reshuffleFlag = true;

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

                if (reshuffleFlag) {
                    LOG.info("reshuffle flag is set - shuffling and cutting shoe...");
                    shoe.shuffle(10);
                    shoe.cut();
                    reshuffleFlag = false;
                    LOG.info("done with reshuffle!");
                }
                playerWagerMap.clear();
                // fall into DEALER_NEXT_ACTION

            case DEALER_NEXT_ACTION:
                // fall into REQUEST_WAGER

            case REQUEST_WAGER:
                Optional<ActionToken> solicitWagerActionToken = getSolicitWagerActionToken(actionToken);
                if (solicitWagerActionToken.isPresent()) {
                    return solicitWagerActionToken.get();
                }
            // remaining DEALER initiated actions here
                return getEndGameActionToken();


            case WAGER:
                playerWagerMap.put(actionToken.getActionSource(), actionToken.getOfferedMoney());
                LOG.info("playerWagerMap is now: " + playerWagerMap);
                return getDealerNextActionToken(actionToken);

            default:
                return getEndGameActionToken();


        }

    }


    public Hand getDealerHandForPlayer() {
        if (this.getHand().getCardListSize() > 1) {
            List<Card> dealerCardListForPlayer = this.getHand()
                                                     .getAllCards(false)
                                                     .subList(1, this.getHand().getCardListSize());

            Hand dealerHandForPlayer = new Hand(dealerCardListForPlayer);
            return dealerHandForPlayer;
        }

        return new Hand();
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

    private Card draw() {
        Card card = shoe.draw();
        if (card.getCardType() == CardType.CUT) {
            this.reshuffleFlag = true;
            return shoe.draw();
        }
        return card;
    }
    private List<Card> draw(int count) {
        List<Card> cardList = shoe.draw(count);
        if (cardList.contains(shoe.getCutCard())) {
            this.reshuffleFlag = true;
            cardList.remove(shoe.getCutCard());
            cardList.add(shoe.draw());
            return cardList;
        }
        return cardList;
    }

    private Optional<ActionToken> getSolicitWagerActionToken(ActionToken actionToken) {
        for (AgentPosition agentPosition : actionToken.getPlayerHandMap().keySet()) {
            if (agentPosition == AgentPosition.DEALER) { continue; }
            if (playerWagerMap.containsKey(agentPosition)) { continue; }

            ActionToken requestWagerActionToken = new ActionToken.Builder(actionToken)
                                                                 .withAction(Action.REQUEST_WAGER)
                                                                 .withActionSource(AgentPosition.DEALER)
                                                                 .withActionTarget(agentPosition)
                                                                 .build();

            return Optional.of(requestWagerActionToken);

        }

        return Optional.empty();
    }

    private ActionToken getDealerNextActionToken(ActionToken actionToken) {
        return new ActionToken.Builder()
                              .withAction(Action.DEALER_NEXT_ACTION)
                              .withRuleSet(actionToken.getRuleSet())
                              .withPlayerHandMap(actionToken.getPlayerHandMap())
                              .withActionSource(AgentPosition.DEALER)
                              .withActionTarget(AgentPosition.DEALER)
                              .build();
    }

    private ActionToken getEndGameActionToken() {
        return new ActionToken.Builder()
                              .withActionSource(AgentPosition.DEALER)
                              .withActionTarget(AgentPosition.GAME)
                              .withAction(Action.GAME_END)
                              .build();
    }

}
