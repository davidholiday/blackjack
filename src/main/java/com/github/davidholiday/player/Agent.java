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

    private Optional<Integer> count = Optional.empty();

    public Agent(CountStrategy countStrategy, PlayStrategy playStrategy) {
        this.countStrategy = countStrategy;
        this.playStrategy = playStrategy;
    }

    public abstract AgentAction act(Game.GamePublic gamePublic);


    public class AgentAction {

        public final Map<PlayerPosition, Action> actionMap;
        public final Map<PlayerPosition, List<Card>> offeredCardsMap;
        public final Map<PlayerPosition, Integer> offeredMoneyMap;

        public AgentAction (
                Map<PlayerPosition, Action> actionMap,
                Map<PlayerPosition, List<Card>> offeredCardsMap,
                Map<PlayerPosition, Integer> offeredMoneyMap
        ) {
            this.actionMap = actionMap;
            this.offeredCardsMap = offeredCardsMap;
            this.offeredMoneyMap = offeredMoneyMap;
        }
    };

    public String getCountStrategyName() { return countStrategy.getName(); }

    public String getPlayStrategyName() { return playStrategy.getName(); }

    void updateCount(Hand hand, Game.GamePublic gamePublic) {
        count = countStrategy.updateCount(hand, gamePublic);
    }

    public Optional<Integer> getCount() {
        if (count.isPresent()) { return Optional.of(count.get()); }
        else { return Optional.empty(); }
    }

    public Action evaluateHand(Hand hand, Game.GamePublic gamePublic) {
        return playStrategy.evaluateHand(hand, count, gamePublic);
    }

}
