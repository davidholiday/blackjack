package com.github.davidholiday.agent;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.HandCollection;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.agent.strategy.count.CountStrategy;
import com.github.davidholiday.agent.strategy.play.PlayStrategy;
import com.github.davidholiday.game.RuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Agent {

    private static final Logger LOG = LoggerFactory.getLogger(Agent.class);

    private final HandCollection handCollection;

    private final CountStrategy countStrategy;

    private final PlayStrategy playStrategy;

    private double bankroll;

    private double initialBankroll;

    private AgentPosition agentPosition;

    private int count = 0;

    public Agent(CountStrategy countStrategy,
                 PlayStrategy playStrategy,
                 double bankroll,
                 RuleSet ruleSet,
                 AgentPosition agentPosition) {
//        this.hand = new Hand();
        this.handCollection = new HandCollection(ruleSet);

        this.countStrategy = countStrategy;
        this.playStrategy = playStrategy;

        if (bankroll < 0) {
            throw new IllegalArgumentException("bankroll argument must be a positive number!");
        }
        this.bankroll = bankroll;
        this.initialBankroll = bankroll;
        this.agentPosition = agentPosition;

    }

    public abstract ActionToken act(ActionToken actionToken);

    public AgentPosition getAgentPosition() { return agentPosition; }

    public AgentPosition getAgentPositionFromHandIndex(int handIndex) {
        return AgentPosition.getAgentHandList(agentPosition)
                            .get(handIndex);
    }

    public int getHandIndexFromAgentPosition(AgentPosition agentPosition) {
        if (agentPosition == AgentPosition.DEALER) { return 0; }

        String indexString = agentPosition.toString()
                                          .split("\\$H")[1];

        return Integer.parseInt(indexString);
    }

    public Map<AgentPosition, Hand> getAllHands() {
        Map<AgentPosition, Hand> agentHandMap = new HashMap<>();
        List<Hand> handList = getHandCollection();
        for (int i = 0; i < handList.size(); i ++) {
            AgentPosition agentPosition = getAgentPositionFromHandIndex(i);
            agentHandMap.put(agentPosition, handList.get(i));
        }

        return agentHandMap;
    }

    public Hand getHand(int handIndex) {
        return new Hand(handCollection.getHand(handIndex));
    }

    public List<Hand> getHandCollection() {
        return handCollection.getHandList()
                             .stream()
                             .map(h -> new Hand(h))
                             .collect(Collectors.toList());
    }

    public void addCardsToHandCollection(List<Card> cardList, int handIndex) {
        handCollection.addCardsToHand(cardList, handIndex);
    }

    public void addCardToHandCollection(Card card, int handIndex) {
        handCollection.addCardToHand(card, handIndex);
    }

    public void splitHandInHandCollection(List<Card> newCardList, int handIndex) {
        if (newCardList.size() != 2) {
            throw new IllegalArgumentException("can only split a hand when provided with exactly two new cards, yo");
        }
        List<Card> oldHandCardList = handCollection.popHand(handIndex).getAllCards(true);

        List<Card> newHandCardListOne = List.of(oldHandCardList.get(0), newCardList.get(0));
        List<Card> newHandCardListTwo = List.of(oldHandCardList.get(1), newCardList.get(1));

        Hand newHandOne = new Hand();
        newHandOne.addCards(newHandCardListOne);

        Hand newHandTwo = new Hand();
        newHandTwo.addCards(newHandCardListTwo);

        handCollection.addHand(newHandOne);
        handCollection.addHand(newHandTwo);
    }

    public List<Card> clearHand(int handIndex) {
        return handCollection.getHand(handIndex)
                             .getAllCards(true);
    }

    public List<Card> clearHands() { return handCollection.clearHands(); };

    public String getCountStrategyName() { return countStrategy.getName(); }

    public String getPlayStrategyName() { return playStrategy.getName(); }

    public int getCount() {
        return count;
    }

    void updateCount(ActionToken actionToken) {
        count = countStrategy.updateCount(actionToken);
    }

    Action getNextAction(ActionToken actionToken, int handIndex) {

        // the reason for this is, during the SPLIT handshake between the DEALER and PLAYER, the handIndex is out of
        // sync between what the dealer is referencing and what exists. The handler for this action needs the current
        // ACTUAL handIndex, not the incremented one the DEALER needs to track the new hand.
        if (actionToken.getAction() == Action.OFFER_CARDS_FOR_SPLIT) {
            handIndex -= 1;
        }
        Hand hand = handCollection.getHand(handIndex);
        return playStrategy.evaluateHand(hand, count, actionToken);
    }

    void updateBankroll(double updateBy) {

        if (bankroll + updateBy < 0 ) {
            LOG.info("*!* bankroll has been ruined for agent: {} *!*", this);
            LOG.info("*!* resetting bankroll to: {} for agent: {} *!*", initialBankroll, this);
            bankroll = initialBankroll;
        } else if (bankroll + updateBy > Double.MAX_VALUE) {
            LOG.info("bankroll has been exceeded for agent {} *!*", this);
            bankroll = Double.MAX_VALUE;
        }
        else {
            bankroll += updateBy;
        }

    }

    public double getBankroll() { return bankroll; }

    double getWager(ActionToken actionToken) {
        double wager = countStrategy.getWager(actionToken);
        updateBankroll(-wager);
        return wager;
    }

    double getLastAnteWager() { return countStrategy.getLastAnteWager(); }

    double getInsuranceWager(ActionToken actionToken, int handIndex) {
        Hand hand = handCollection.getHand(handIndex);
        double insurance = countStrategy.getInsuranceBet(hand, actionToken);
        updateBankroll(-insurance);
        return insurance;
    }


}
