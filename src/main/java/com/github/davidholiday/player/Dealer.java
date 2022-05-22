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

public class Dealer extends Agent {

    public Dealer(Strategy strategy) {
        super(strategy);
    }

    @Override
    public Action act(
            Action actionToAgent,
            Optional<Map<PlayerPosition, Hand>> hands,
            Set<Rule> ruleSet,
            Optional<Integer> count
    ) {
        return null;
    }

    @Override
    public void takeOfferedCards(Optional<List<Card>> offeredCards) {

    }

    @Override
    public Optional<Card> getOfferedCards() {
        return Optional.empty();
    }

    @Override
    public void takeOfferedMoney(Optional<Integer> offeredMoney) {

    }

    @Override
    public Optional<Integer> getOfferedMoney() {
        return Optional.empty();
    }
}
