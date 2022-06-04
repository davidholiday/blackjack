package com.github.davidholiday.agent;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.agent.strategy.count.CountStrategy;
import com.github.davidholiday.agent.strategy.play.PlayStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Agent {

    private static final Logger LOG = LoggerFactory.getLogger(Agent.class);

    private final Hand hand;

    private final CountStrategy countStrategy;

    private final PlayStrategy playStrategy;

    private double bankroll;

    private int count = 0;

    public Agent(CountStrategy countStrategy, PlayStrategy playStrategy, double bankroll) {
        this.hand = new Hand();
        this.countStrategy = countStrategy;
        this.playStrategy = playStrategy;

        if (bankroll < 0) {
            throw new IllegalArgumentException("bankroll argument must be a positive number!");
        }
        this.bankroll = bankroll;

    }

    public abstract ActionToken act(ActionToken actionToken);

    public Hand getHand() {
        return new Hand(hand);
    }

    public void addCardToHand(Card card) {
        hand.addCards(Stream.of(card).collect(Collectors.toList()));
    }

    public void addCardsToHand(List<Card> cardList) {
        hand.addCards(cardList);
    }

    public String getCountStrategyName() { return countStrategy.getName(); }

    public String getPlayStrategyName() { return playStrategy.getName(); }

    public int getCount() {
        return count;
    }

    void updateCount(ActionToken actionToken) {
        count = countStrategy.updateCount(hand, actionToken);
    }

    Action getNextAction(ActionToken actionToken) { return playStrategy.evaluateHand(hand, count, actionToken); }

    public void updateBankroll(double updateBy) {

        if (bankroll + updateBy < 0 ) {
            LOG.info("bankroll has been ruined for player " + this.toString());
            bankroll = 0;
        } else if (bankroll + updateBy > Double.MAX_VALUE) {
            LOG.info("bankroll has been exceeded for player " + this.toString());
            bankroll = Double.MAX_VALUE;
        }
        else {
            bankroll += updateBy;
        }

    }

    public double getBankroll() { return bankroll; }

    public double getWager(ActionToken actionToken) {
        double wager = playStrategy.getWager(count, actionToken);
        updateBankroll(-wager);
        return wager;
    }

    public double getInsuranceBet(ActionToken actionToken) {
        double insurance = playStrategy.getInsuranceBet(hand, getCount(), actionToken);
        updateBankroll(-insurance);
        return insurance;
    }

}
