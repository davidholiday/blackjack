package com.github.davidholiday.agent.strategy.count;

import com.github.davidholiday.agent.AgentPosition;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.HandCollection;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.game.Rule;
import com.github.davidholiday.game.RuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public abstract class CountStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(CountStrategy.class);

    protected int shoeDeckSize;

    protected double lastAnteWager = 0;

    protected double baseWager;

    protected int count = 0;

    List<Map<AgentPosition, Hand>> playerHandMaps = new ArrayList<>();

    public CountStrategy(RuleSet ruleSet, double baseWager) {
        this.baseWager = baseWager;

        Optional<Rule> deckSizeRuleOptional = Optional.empty();

        for (Rule rule : ruleSet.getRuleSetStream().collect(Collectors.toList())) {
            if (Rule.getDeckRuleSet().contains(rule)) {
                deckSizeRuleOptional = Optional.of(rule);
            }
        }

        if (deckSizeRuleOptional.isEmpty()) {
            throw new IllegalStateException("ruleSet does not contain deck size rule!");
        }

        Rule deckSizeRule = deckSizeRuleOptional.get();
        switch (deckSizeRule) {
            case ONE_DECK_SHOE:
                this.shoeDeckSize = 1;
                break;
            case TWO_DECK_SHOE:
                this.shoeDeckSize = 2;
                break;
            case FOUR_DECK_SHOE:
                this.shoeDeckSize = 4;
                break;
            case SIX_DECK_SHOE:
                this.shoeDeckSize = 6;
                break;
            case EIGHT_DECK_SHOE:
                this.shoeDeckSize = 8;
                break;
            default:
                throw new IllegalStateException("unexpected deck rule found");
        }

        resetCount();
    }

    public abstract String getName();

    public abstract double getWager(ActionToken actionToken);

    public abstract double getInsuranceBet(Hand hand, ActionToken actionToken);

    public abstract void updateCount(ActionToken actionToken);

    public abstract int getInitialCount();

    public void resetCount() {
        playerHandMaps.clear();
        count = getInitialCount();
    }

    public int getCount() { return count; }

    // as play happens we want to update the 'current' playerHandMap with the latest information.
    //   at the end of the round we'll evaluate the count
    public void updateCurrentPlayerHandMap(ActionToken actionToken) {
        playerHandMaps.set(playerHandMaps.size()-1, actionToken.getPlayerHandMap());
    }

    public double getLastAnteWager() { return lastAnteWager; };

}
