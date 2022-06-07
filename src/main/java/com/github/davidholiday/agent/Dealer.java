package com.github.davidholiday.agent;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardSuit;
import com.github.davidholiday.card.CardType;
import com.github.davidholiday.cardcollection.DiscardTray;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.HandOutcome;
import com.github.davidholiday.cardcollection.Shoe;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.agent.strategy.count.NoCountStrategy;
import com.github.davidholiday.agent.strategy.play.PlayStrategy;
import com.github.davidholiday.game.Rule;
import com.github.davidholiday.game.RuleSet;
import com.github.davidholiday.util.GeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.davidholiday.agent.AgentPosition.*;

public class Dealer extends Agent {

    private static final Logger LOG = LoggerFactory.getLogger(Dealer.class);

    private final Shoe shoe;

    private final DiscardTray discardTray = new DiscardTray();

    private final Map<AgentPosition, Double> playerWagerMap = new HashMap<>();

    private final Map<AgentPosition, Double> playerInsuranceMap = new HashMap<>();

    private final Set<AgentPosition> playerDoneSet = new HashSet<>();

    private boolean reshuffleFlag = true;

    private boolean hideHoleCard = true;

    private boolean adjudicationPhase = false;

    private boolean insuranceBetSettled = false;

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

    /*

    unlike the Player Agents, the Dealer's Strategy object is only invoked inside the REQUEST_PLAY case block. all
    other dealer self-actions are handled within the act() method.

    in contrast, the Player Agents act method switches on the response from its strategy object's getNextAction()
    method.

     */

    @Override
    public ActionToken act(ActionToken actionToken) {

        // for convenience in the PLAY RESPONSES section at the bottom
        AgentPosition sourceAgentPosition = actionToken.getActionSource();
        Hand sourceAgentHand = actionToken.getPlayerHandMap().get(sourceAgentPosition);
        RuleSet ruleset = actionToken.getRuleSet();

        switch (actionToken.getAction()) {
            //
            // DEALER ACTIONS
            //
            case GAME_START:
                playerWagerMap.clear();
                playerInsuranceMap.clear();
                playerDoneSet.clear();
                hideHoleCard = true;
                adjudicationPhase = false;
                insuranceBetSettled = false;

                if (reshuffleFlag) {
                    LOG.debug("reshuffle flag is set - shuffling and cutting shoe...");
                    shoe.shuffle(10);
                    shoe.cut();
                    reshuffleFlag = false;
                    LOG.debug("done with reshuffle!");
                }
                // fall into DEALER_NEXT_ACTION
            case DEALER_NEXT_ACTION:
                // fall into REQUEST_WAGER
            case REQUEST_WAGER:
                Optional<ActionToken> solicitWagerActionToken = getSolicitWagerActionToken(actionToken);
                if (solicitWagerActionToken.isPresent()) {
                    return solicitWagerActionToken.get();
                }
                // fall into DEAL_HAND
            case DEAL_HAND:
                Optional<ActionToken> offerCardActionToken = getOfferCardActionToken(actionToken);
                if (offerCardActionToken.isPresent()) {
                    return offerCardActionToken.get();
                }
                // fall into OFFER_CARD
            case OFFER_CARDS:
                if (actionToken.getOfferedCards().size() > 0) {
                    if (actionToken.getActionSource() == DEALER && actionToken.getActionTarget() == DEALER) {
                        addCardsToHand(actionToken.getOfferedCards());

// FOR TESTING INSURANCE BETS AND BLACKJACK DETECTION
//                        clearHand();
//                        Card ten = new Card(CardType.TEN, CardSuit.SPADES);
//                        Card jack = new Card(CardType.JACK, CardSuit.HEARTS);
//                        Card ace = new Card(CardType.ACE, CardSuit.HEARTS);
//                        Card deuce = new Card(CardType.TWO, CardSuit.HEARTS);
//                        addCardsToHand(List.of(ace, ten));
                    } else {
                        addCardsToDiscardTray(actionToken.getOfferedCards());
                    }
                    return actionToken.getDealerNextActionToken();
                }
                // fall into OFFER_INSURANCE
            case OFFER_INSURANCE:
                Optional<ActionToken> offerInsuranceActionToken = getOfferInsuranceActionToken(actionToken);
                if (offerInsuranceActionToken.isPresent()) {
                    return offerInsuranceActionToken.get();
                }
                // fall into SETTLE_INSURANCE_BETS
            case SETTLE_INSURANCE_BETS:
                Optional<ActionToken> settleInsuranceBetActionToken = getSettleInsuranceBetActionToken(actionToken);
                if (settleInsuranceBetActionToken.isPresent()) {
                    return settleInsuranceBetActionToken.get();
                } else {
                    // check to make sure we've resolved all insurance bets then clear the map
                    for (double wager : playerInsuranceMap.values()) {
                        if (wager > 0) {
                            throw new IllegalStateException("insurance bets did not get resolved correctly!");
                        }
                    }
                    if (playerInsuranceMap.isEmpty() == false) {
                        LOG.debug("all insurance bets settled. clearing playerInsuranceMap");
                        playerInsuranceMap.clear();
                    }
                    insuranceBetSettled = true;
                }
                // fall into CHECK_FOR_DEALER_BLACKJACK
            case CHECK_FOR_DEALER_BLACKJACK:
                if (getHandInternal().isBlackJack() && adjudicationPhase == false) {
                    LOG.info("*!* DEALER HAS BLACKJACK-- ADJUDICATING GAME *!*");
                    adjudicationPhase = true;
                    for (AgentPosition agentPosition : actionToken.getPlayerHandMap().keySet()) {
                        playerDoneSet.add(agentPosition);
                    }
                    return actionToken.getDealerNextActionToken();
                }
                // fall into REQUEST_PLAY
            case REQUEST_PLAY:
                if (adjudicationPhase == false) {
                    LOG.info("REQUEST_PLAY >> player hands are: {}", actionToken.getPlayerHandMap());
                }
                Optional<ActionToken> requestPlayActionToken = getRequestPlayActionToken(actionToken);
                if (requestPlayActionToken.isPresent()) {
                    return requestPlayActionToken.get();
                }
                // we handle the dealer's play only after all the players have gone
                hideHoleCard = false;
                Optional<ActionToken> requestDealerPlayActionToken = getDealerPlayActionToken(actionToken);
                if (requestDealerPlayActionToken.isPresent()) {
                    return requestDealerPlayActionToken.get();
                }
                // fall into ADJUDICATE_GAME
            case ADJUDICATE_GAME:
                if (adjudicationPhase == false) {
                    LOG.info("*!* ALL PLAYS MADE -- ADJUDICATING GAME *!*");
                    adjudicationPhase = true;
                }
                Optional<ActionToken> getAdjudicateActionToken = getAdjudicateActionToken(actionToken);
                if (getAdjudicateActionToken.isPresent()) {
                    return getAdjudicateActionToken.get();
                }
                // fall into GAME_END
            case GAME_END:
                // clear dealer hand first
                addCardsToDiscardTray(clearHand());

                // now do player hands
                Optional<ActionToken> getClearHandActionToken = getClearHandActionToken(actionToken);
                if (getClearHandActionToken.isPresent()) {
                    return getClearHandActionToken.get();
                }

                // integrity check
                if (hideHoleCard) {
                    throw new IllegalStateException("hole card should not be hidden at endgame!");
                }

                if (playerDoneSet.size() != actionToken.getPlayerHandMap().size()) {
                    throw new IllegalStateException("players have not all completed their play at endgame state!");
                }

                if (playerWagerMap.isEmpty() == false) {
                    throw new IllegalStateException("playerWagerMap should be empty at endgame state!");
                }

                if (playerInsuranceMap.isEmpty() == false) {
                    throw new IllegalStateException("playerInsuranceMap should be empty at endgame state!");
                }

                if (insuranceBetSettled == false) {
                    throw new IllegalStateException("insurances bets not settled at endgame state!");
                }

                // end the round
                return actionToken.getEndGameActionToken(getHand(), getDiscardTrayCardSize());

            //
            // PLAY RESPONSES
            //
            case SUBMIT_WAGER:
                LOG.info("player: {} wagers: ${}", actionToken.getActionSource(), actionToken.getOfferedMoney());
                playerWagerMap.put(actionToken.getActionSource(), actionToken.getOfferedMoney());
                LOG.debug("playerWagerMap is now: " + playerWagerMap);
                return actionToken.getDealerNextActionToken();
            case TAKE_INSURANCE:
                LOG.info("player: {} takes insurance at: ${}",
                        actionToken.getActionSource(),
                        actionToken.getOfferedMoney()
                );
                playerInsuranceMap.put(actionToken.getActionSource(), actionToken.getOfferedMoney());
                LOG.debug("playerInsuranceMap is now: " + playerInsuranceMap);
                return actionToken.getDealerNextActionToken();
            case OFFER_CARDS_FOR_DISCARD_TRAY:
                if (adjudicationPhase == false) {
                    String msg = "we should not be putting cards in the discard tray before adjudication phase!";
                    throw new IllegalStateException(msg);
                }

                if (actionToken.getActionSource() == DEALER && actionToken.getActionTarget() == DEALER) {
                    addCardsToDiscardTray(clearHand());
                }
                addCardsToDiscardTray(actionToken.getOfferedCards());
                return actionToken.getDealerNextActionToken();
            case SURRENDER:
                //
            case SPLIT:
                //
            case DOUBLE_DOWN:
                //
            case HIT:
                // make sure the agent isn't busted
                if (sourceAgentHand.isBust()) {
                    throw new IllegalStateException(sourceAgentPosition + " attempted to HIT but their hand is BUST!");
                }

                LOG.info("player: {} HITS", sourceAgentPosition);
                List<Card> offeredCardList = List.of(draw());
                return new ActionToken.Builder().withAction(Action.OFFER_CARDS)
                                                .withActionSource(DEALER)
                                                .withActionTarget(DEALER).withOfferedCards(offeredCardList)
                                                .build();
            case STAND:
                LOG.info("player: {} STANDS", sourceAgentPosition);
                // fall into NONE
            case NONE:
                if (sourceAgentHand.isBust()) {
                    LOG.info("player: {} is BUST", sourceAgentPosition);
                }
                playerDoneSet.add(actionToken.getActionSource());
                return actionToken.getDealerNextActionToken();
            default:
                return actionToken.getEndGameActionToken(getHand(), getDiscardTrayCardSize());



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

    public Hand getHandInternal() { return super.getHand(); }

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

    private void addCardsToDiscardTray(List<Card> cardList) { discardTray.addCards(cardList); }

    private ActionToken getOfferMoneyToActionToken(AgentPosition recipient, double offeredMoney) {
        return new ActionToken.Builder()
                              .withActionTarget(recipient)
                              .withActionSource(DEALER)
                              .withAction(Action.OFFER_MONEY)
                              .withOfferedMoney(offeredMoney)
                              .build();
    }

    private Optional<ActionToken> getOfferMoneyToOptional(AgentPosition recipient, double offeredMoney) {
        return Optional.of(getOfferMoneyToActionToken(recipient, offeredMoney));
    }

    private Optional<ActionToken> getSolicitWagerActionToken(ActionToken actionToken) {
        if (adjudicationPhase) { return Optional.empty(); }
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

    private Optional<ActionToken> getOfferCardActionToken(ActionToken actionToken) {
        if (adjudicationPhase) { return Optional.empty(); }
        for (AgentPosition agentPosition : dealOrder) {
            if (actionToken.getPlayerHandMap().containsKey(agentPosition)) {
                if (actionToken.getPlayerHandMap().get(agentPosition).getCardListSize() < 2) {

                    ActionToken offerCardActionToken = new ActionToken.Builder(actionToken)
                                                                      .withAction(Action.OFFER_CARDS)
                                                                      .withActionSource(DEALER)
                                                                      .withActionTarget(agentPosition)
                                                                      .withOfferedCards(draw(1))
                                                                      .build();
                    return Optional.of(offerCardActionToken);
                }
            }
        }
        return Optional.empty();
    }

    private Optional<ActionToken> getOfferInsuranceActionToken(ActionToken actionToken) {
        if (insuranceBetSettled) { return Optional.empty(); }
        if (adjudicationPhase) { return Optional.empty(); }


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

    private Optional<ActionToken> getRequestPlayActionToken(ActionToken actionToken) {
        if (adjudicationPhase) { return Optional.empty(); }

        for (AgentPosition agentPosition : dealOrder) {
            if (agentPosition == DEALER) { continue; }
            if (actionToken.getPlayerHandMap().containsKey(agentPosition)) {
                if (playerDoneSet.contains(agentPosition) == false) {

                    ActionToken requestPlayActionToken = new ActionToken.Builder(actionToken)
                                                                        .withAction(Action.REQUEST_PLAY)
                                                                        .withActionSource(DEALER)
                                                                        .withActionTarget(agentPosition)
                                                                        .build();
                    return Optional.of(requestPlayActionToken);
                }
            }
        }
        return Optional.empty();
    }

    private Optional<ActionToken> getSettleInsuranceBetActionToken(ActionToken actionToken) {
        if (adjudicationPhase) { return Optional.empty(); }
        if (playerInsuranceMap.isEmpty()) { return Optional.empty(); }

        // doing it this way because an insurance bet of 0 is the flag telling us we've handled that player's
        // insurance bet. It will be cleared after all insurance bets have been settled
        for (AgentPosition agentPosition : playerInsuranceMap.keySet()) {
            double wager = playerInsuranceMap.get(agentPosition);
            if (wager == 0) {
                continue;
            } else {
                playerInsuranceMap.put(agentPosition, 0.0);
            }

            if (getHandInternal().isBlackJack()) {
                double offeredMoney = wager * 2;
                LOG.info("dealer has blackjack. paying ${} to player {}", offeredMoney, agentPosition);

                ActionToken settleInsuranceBetActionToken =
                        new ActionToken.Builder(actionToken)
                                       .withAction(Action.OFFER_MONEY)
                                       .withActionSource(DEALER)
                                       .withActionTarget(agentPosition)
                                       .withOfferedMoney(offeredMoney)
                                       .build();

                return Optional.of(settleInsuranceBetActionToken);
            } else {
                LOG.info("dealer does not blackjack. taking ${} insurance bet from player {}", wager, agentPosition);
                updateBankroll(wager);
                return Optional.of(actionToken.getDealerNextActionToken());
            }
        }
        return Optional.empty();
    }

    private Optional<ActionToken> getDealerPlayActionToken(ActionToken actionToken) {
        if (adjudicationPhase) { return Optional.empty(); }

        // the DEALER is in the playerHandMap, so if all the players have gone, playerDoneSet.size() should
        // either equal playerHandMap.size() or be one less
        if (playerDoneSet.size() < actionToken.getPlayerHandMap().size() - 1) {
            String msg = "dealer should not be playing their hand before all the players have gone!";
            throw new IllegalStateException(msg);
        }
        if (playerDoneSet.contains(DEALER)) { return Optional.empty(); }
        Action dealerAction = getNextAction(actionToken);

        ActionToken requestDealerPlayActionToken = new ActionToken.Builder(actionToken)
                                                                  .withAction(dealerAction)
                                                                  .withActionSource(DEALER)
                                                                  .withActionTarget(DEALER)
                                                                  .build();

        return Optional.of(requestDealerPlayActionToken);

    }

    private Optional<ActionToken> getAdjudicateActionToken(ActionToken actionToken) {

        // make sure everyone has played their hand before we do this
        if (playerDoneSet.size() < actionToken.getPlayerHandMap().size()) {
            String msg = "can't adjudicate a game before all the players have finished playing!";
            throw new IllegalStateException(msg);
        }

        // make sure hole card is revealed. if not something has gone wrong with the logic upstream...
        Card hiddenHoleCard = new Card(CardType.HIDDEN, CardSuit.NONE);
        if (getHandInternal().getAllCards(false).contains(hiddenHoleCard)) {
            String msg = "can't adjudicate a game with dealer hole card still hidden!";
            throw new IllegalStateException(msg);
        }

        Hand dealerHand = getHandInternal();
        RuleSet ruleSet = actionToken.getRuleSet();

        for (AgentPosition agentPosition : dealOrder) {
            if (agentPosition == DEALER) { continue; }
            if (playerWagerMap.containsKey(agentPosition) == false) { continue; }
            if (actionToken.getPlayerHandMap().containsKey(agentPosition)) {
                Hand playerHand = actionToken.getPlayerHandMap().get(agentPosition);
                LOG.info("adjudicating {} hand: {} vs DEALER hand: {}", agentPosition, playerHand, dealerHand);
                LOG.debug(
                        "adjudicating {} hand: {} vs DEALER hand: {}",
                        agentPosition,
                        playerHand.toStringFull(),
                        dealerHand.toStringFull()
                );

                // player bust - gets adjudicated before dealer hand
                if (playerHand.isBust()) {
                    LOG.info("player: " + agentPosition + " is bust. DEALER WIN");
                    updateBankroll(playerWagerMap.remove(agentPosition));
                    continue;
                }
                // player blackjack
                else if (playerHand.isBlackJack()) {
                    if (getHandInternal().isBlackJack()) {
                        // push
                        LOG.info("player: " + agentPosition + " has blackjack but so does dealer. PUSH");
                        double offerMoneyAmount = playerWagerMap.remove(agentPosition);
                        return getOfferMoneyToOptional(agentPosition, offerMoneyAmount);
                    }
                    else if (ruleSet.contains(Rule.BLACKJACK_PAYS_SIX_TO_FIVE)) {
                        // pay 6:5
                        LOG.info("player: " + agentPosition + " has BlackJack! PLAYER WIN");
                        double offerMoneyAmount = playerWagerMap.remove(agentPosition) * 2.2;
                        return getOfferMoneyToOptional(agentPosition, offerMoneyAmount);
                    }
                    else {
                        // pay 3:2
                        LOG.info("player: " + agentPosition + " has BlackJack! PLAYER WIN");
                        double offerMoneyAmount = playerWagerMap.remove(agentPosition) * 2.5;
                        return getOfferMoneyToOptional(agentPosition, offerMoneyAmount);
                    }
                }
                else {
                    HandOutcome handOutcome = dealerHand.getHandOutcome(playerHand);
                    double playerWager = playerWagerMap.remove(agentPosition);
                    switch (handOutcome) {
                        case WIN:
                            LOG.info("player: " + agentPosition + " LOSES TO DEALER");
                            updateBankroll(playerWager);
                            break;
                        case LOSE:
                            LOG.info("player: " + agentPosition + " WINS");
                            double offerMoneyAmount = playerWager * 2;
                            return getOfferMoneyToOptional(agentPosition, offerMoneyAmount);
                        case PUSH:
                            LOG.info("player: " + agentPosition + " PUSH");
                            offerMoneyAmount = playerWager;
                            return getOfferMoneyToOptional(agentPosition, offerMoneyAmount);
                    }
                }
            }
        }
        return Optional.empty();
    }

    private Optional<ActionToken> getClearHandActionToken(ActionToken actionToken) {
        for (AgentPosition agentPosition : dealOrder) {
            if (actionToken.getPlayerHandMap().containsKey(agentPosition)) {
                if (actionToken.getPlayerHandMap().get(agentPosition).getCardListSize() == 0) { continue; }

                ActionToken clearHandActionToken = new ActionToken.Builder(actionToken)
                                                                  .withAction(Action.CLEAR_HAND)
                                                                  .withActionSource(AgentPosition.DEALER)
                                                                  .withActionTarget(agentPosition)
                                                                  .build();

                return Optional.of(clearHandActionToken);
            }
        }
        return Optional.empty();
    }

}
