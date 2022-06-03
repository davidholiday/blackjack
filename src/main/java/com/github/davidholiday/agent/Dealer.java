package com.github.davidholiday.agent;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardSuit;
import com.github.davidholiday.card.CardType;
import com.github.davidholiday.cardcollection.DiscardTray;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.Shoe;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.agent.strategy.count.NoCountStrategy;
import com.github.davidholiday.agent.strategy.play.PlayStrategy;
import com.github.davidholiday.util.GeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.davidholiday.agent.AgentPosition.*;

public class Dealer extends Agent {

    private static final Logger LOG = LoggerFactory.getLogger(Dealer.class);

    private final Shoe shoe;

    private final DiscardTray discardTray = new DiscardTray();

    private final Map<AgentPosition, Double> playerWagerMap = new HashMap<>();

    private final Map<AgentPosition, Double> playerInsuranceMap = new HashMap<>();

    private boolean reshuffleFlag = true;

    private boolean hideHoleCard = true;

    private List<AgentPosition> dealOrder =
            Stream.of(
                PLAYER_ONE,
                PLAYER_TWO,
                PLAYER_THREE,
                PLAYER_FOUR,
                PLAYER_FIVE,
                PLAYER_SIX,
                PLAYER_SEVEN,
                DEALER
        ).collect(Collectors.toList());


    public Dealer(PlayStrategy playStrategy, Shoe shoe) {
        super(new NoCountStrategy(), playStrategy, Integer.MAX_VALUE);
        this.shoe = shoe;
    }

    @Override
    public ActionToken act(ActionToken actionToken) {

        switch (actionToken.getAction()) {
            case GAME_START:
                playerWagerMap.clear();
                playerInsuranceMap.clear();
                hideHoleCard = true;

                if (reshuffleFlag) {
                    LOG.info("reshuffle flag is set - shuffling and cutting shoe...");
                    shoe.shuffle(10);
                    shoe.cut();
                    reshuffleFlag = false;
                    LOG.info("done with reshuffle!");
                }
                // fall into DEALER_NEXT_ACTION

            case DEALER_NEXT_ACTION:
                // fall into REQUEST_WAGER

            case REQUEST_WAGER:
                Optional<ActionToken> solicitWagerActionToken = getSolicitWagerActionToken(actionToken);
                if (solicitWagerActionToken.isPresent()) {
                    return solicitWagerActionToken.get();
                }
            case DEAL_HAND:
                Optional<ActionToken> takeCardActionToken = getTakeCardActionToken(actionToken);
                if (takeCardActionToken.isPresent()) {
                    return takeCardActionToken.get();
                }
            case OFFER_INSURANCE:
                Optional<ActionToken> offerInsuranceActionToken = getOfferInsuranceActionToken(actionToken);
                if (offerInsuranceActionToken.isPresent()) {
                    return offerInsuranceActionToken.get();
                }
                return ActionToken.getEndGameActionToken();
//            case REQUEST_PLAY:
//                Optional<ActionToken> requestPlayActionToken = getRequestPlayActionToken(actionToken);
//                if (requestPlayActionToken.isPresent()) {
//                    return requestPlayActionToken.get();
//                }
            // remaining DEALER initiated actions here



            case SUBMIT_WAGER:
                playerWagerMap.put(actionToken.getActionSource(), actionToken.getOfferedMoney());
                LOG.info("playerWagerMap is now: " + playerWagerMap);
                return ActionToken.getDealerNextActionToken(actionToken);
            case TAKE_CARD:
                addCardsToHand(actionToken.getOfferedCards());
                return ActionToken.getDealerNextActionToken(actionToken);
            case TAKE_INSURANCE:
                playerInsuranceMap.put(actionToken.getActionSource(), actionToken.getOfferedMoney());
                LOG.info("playerInsuranceMap is now: " + playerInsuranceMap);
                return ActionToken.getDealerNextActionToken(actionToken);

            default:
                return ActionToken.getEndGameActionToken();


        }

    }

    @Override
    public Hand getHand() {
        if (hideHoleCard == false) { return super.getHand(); }
        Hand hand = super.getHand();
        if (hand.getCardListSize() > 0) {
            Card card = new Card(CardType.HIDDEN, CardSuit.NONE);
            hand.replace(card, 0);
            hand.updateHandValue();
        }
        return hand;
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
        for (AgentPosition agentPosition : dealOrder) {
            if (actionToken.getPlayerHandMap().containsKey(agentPosition)) {
                if (agentPosition == AgentPosition.DEALER) { continue; }
                if (playerWagerMap.containsKey(agentPosition)) { continue; }

                ActionToken requestWagerActionToken = new ActionToken.Builder(actionToken)
                                                                     .withAction(Action.REQUEST_WAGER)
                                                                     .withActionSource(AgentPosition.DEALER)
                                                                     .withActionTarget(agentPosition)
                                                                     .build();

                return Optional.of(requestWagerActionToken);
            }
        }
        return Optional.empty();
    }

    private Optional<ActionToken> getTakeCardActionToken(ActionToken actionToken) {
        for (AgentPosition agentPosition : dealOrder) {
            if (actionToken.getPlayerHandMap().containsKey(agentPosition)) {
                if (actionToken.getPlayerHandMap().get(agentPosition).getCardListSize() < 2) {

                    ActionToken takeCardActionToken = new ActionToken.Builder(actionToken)
                                                                     .withAction(Action.TAKE_CARD)
                                                                     .withActionSource(DEALER)
                                                                     .withActionTarget(agentPosition)
                                                                     .withOfferedCards(draw(1))
                                                                     .build();
                    return Optional.of(takeCardActionToken);
                }
            }
        }
        return Optional.empty();
    }

    private Optional<ActionToken> getOfferInsuranceActionToken(ActionToken actionToken) {

        // the insurance bet only happens after all the players have bet, have their cards, and the
        // dealer is showing an ACE. Because we fall into this method on every DEALER_NEXT_ACTION we need
        // to check to make sure we offer insurance only once per round and only when it's appropriate to
        // do so

        // everyone should have two cards
        for (AgentPosition agentPosition : actionToken.getPlayerHandMap().keySet()) {
            if (actionToken.getPlayerHandMap().get(agentPosition).getCardListSize() != 2) {
                return Optional.empty();
            }
        }

        // the dealer's hole card should be hidden
        boolean holeCardRight = getHand().getAllCards(false)
                                         .get(0)
                                         .getCardType() == CardType.HIDDEN;

        // dealer should be showing an ACE
        boolean dealerShowingAce = getHand().getAllCards(false)
                                            .get(1)
                                            .getCardType() == CardType.ACE;

        if (holeCardRight && dealerShowingAce) {
            for (AgentPosition agentPosition : dealOrder) {
                if (agentPosition == DEALER) { continue; }
                if (actionToken.getPlayerHandMap().containsKey(agentPosition)) {
                    if (playerInsuranceMap.containsKey(agentPosition) == false) {
                        ActionToken offerInsuranceActionToken =  new ActionToken.Builder(actionToken)
                                                                                .withAction(Action.OFFER_INSURANCE)
                                                                                .withActionTarget(agentPosition)
                                                                                .withActionSource(DEALER)
                                                                                .build();

                        return Optional.of(offerInsuranceActionToken);
                    }
                }
            }
        }
        return Optional.empty();
    }

}
