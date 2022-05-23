package com.github.davidholiday.player;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.CardCollection;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.game.Rule;
import com.github.davidholiday.player.strategy.Strategy;

import java.util.*;

public abstract class Agent {

    private final Strategy strategy;

    private final Map<PlayerPosition, List<Card>> offeredCardsForMap = new HashMap<>();

    private final Map<PlayerPosition, Integer> offeredMoneyForMap = new HashMap<>();

    private Optional<Integer> count;

    public Agent(Strategy strategy) { this.strategy = strategy; }

    public abstract Map<PlayerPosition, Action> act(

            Game.GamePublic gamePublic

//            Map<PlayerPosition, Action> actionMap,
//            Map<PlayerPosition, Hand> handsMap,
//            Map<PlayerPosition, List<Card>> offeredCardsMap,
//            Map<PlayerPosition, Integer> offeredMoneyMap,
//            Set<Rule> ruleSet,
//            Optional<Integer> count
    );

    public abstract void collectOfferedCards();

    public abstract Map<PlayerPosition, List<Card>> getOfferedCardsForMap();

    abstract void updateOfferedCardsForMap(PlayerPosition playerPosition, List<Card> newCardList);

    public abstract void collectOfferedMoney();

    public abstract Map<PlayerPosition, Integer> getOfferedMoneyMap();

    abstract void updateOfferedMoneyMap(PlayerPosition playerPosition, Integer newAmount);

}
