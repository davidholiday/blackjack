package com.github.davidholiday.player;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.game.Rule;
import com.github.davidholiday.player.strategy.Strategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public abstract class Agent {

    private final Strategy strategy;

    public Agent(Strategy strategy) { this.strategy = strategy; }

    public abstract Action act(
            Action actionToAgent,
            Optional<Map<PlayerPosition, Hand>> hands,
            Set<Rule> ruleSet,
            Optional<Integer> count
    );

    public abstract void takeOfferedCards(Optional<List<Card>> offeredCards);

    public abstract Optional<Card> getOfferedCards();

    public abstract void takeOfferedMoney(Optional<Integer> offeredMoney);

    public abstract Optional<Integer> getOfferedMoney();

}
