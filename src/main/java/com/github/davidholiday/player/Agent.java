package com.github.davidholiday.player;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.player.strategy.count.CountStrategy;
import com.github.davidholiday.player.strategy.play.PlayStrategy;

import java.util.*;

public abstract class Agent {

    private final CountStrategy countStrategy;

    private final PlayStrategy playStrategy;

    private final int bankroll;

    private Optional<Map<String, Integer>> count = Optional.empty();

    public Agent(CountStrategy countStrategy, PlayStrategy playStrategy, int bankroll) {
        this.countStrategy = countStrategy;
        this.playStrategy = playStrategy;

        if (bankroll < 0) {
            throw new IllegalArgumentException("bankroll argument must be a positive number!");
        }
        this.bankroll = bankroll;

    }

    public abstract AgentAction act(Game.GamePublic gamePublic);


    public class AgentAction {

        public final Map<AgentPosition, Action> actionMap;
        public final Map<AgentPosition, List<Card>> offeredCardsMap;
        public final Map<AgentPosition, Integer> offeredMoneyMap;

        public AgentAction (
                Map<AgentPosition, Action> actionMap,
                Map<AgentPosition, List<Card>> offeredCardsMap,
                Map<AgentPosition, Integer> offeredMoneyMap
        ) {
            this.actionMap = actionMap;
            this.offeredCardsMap = offeredCardsMap;
            this.offeredMoneyMap = offeredMoneyMap;
        }
    };

    public String getCountStrategyName() { return countStrategy.getName(); }

    public String getPlayStrategyName() { return playStrategy.getName(); }

    public Optional<Map<String, Integer>> getCount() {
        if (count.isPresent()) { return Optional.of(count.get()); }
        else { return Optional.empty(); }
    }

    void updateCount(Hand hand, Game.GamePublic gamePublic) {
        count = countStrategy.updateCount(hand, gamePublic);
    }

    Action getNextAction(Hand hand, Game.GamePublic gamePublic) {
        return playStrategy.evaluateHand(hand, count, gamePublic);
    }

    double wager(Game.GamePublic gamePublic) {
        return playStrategy.wager(count, gamePublic);
    }

}
