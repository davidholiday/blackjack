package com.github.davidholiday.game;


import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.player.Agent;
import com.github.davidholiday.player.AgentPosition;
import com.github.davidholiday.player.Dealer;
import com.github.davidholiday.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game {

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);

    private RuleSet ruleSet;
    private Dealer dealer;
    private Map<AgentPosition, Player> playerMap;

    public static class Builder {
        private RuleSet ruleSet;
        private Dealer dealer;
        private Map<AgentPosition, Player> playerMap;

        public Builder(RuleSet ruleSet, Dealer dealer) {
            this.ruleSet = ruleSet;
            this.dealer = dealer;
            this.playerMap = new HashMap<>();
        }

        public Builder withPlayerAtPosition(Player player, AgentPosition agentPosition) {
            if (agentPosition == AgentPosition.DEALER
                    || agentPosition == AgentPosition.FIRST_BASE
                    || agentPosition == AgentPosition.SHORT_STOP
                    || agentPosition == AgentPosition.THIRD_BASE

            ) {
                throw new IllegalArgumentException("player can't be assigned: " + agentPosition);
            }

            if (this.playerMap.containsKey(agentPosition)) {
                throw new IllegalArgumentException("player already assigned to position: " + agentPosition);
            }

            this.playerMap.put(agentPosition, player);
            return this;
        }


        public Game build() {
            Game game = new Game();
            game.ruleSet = ruleSet;
            game.dealer = dealer;
            game.playerMap = playerMap;

            return game;
        }


    }
    private Game() {}

    public class GamePublic {
        public final Map<AgentPosition, Hand> playerHandMap;
        public final Queue<Action> actionQueue;
        public final List<Card> offeredCards;
        public final int offeredMoney;
        public final Set<Rule> ruleSet;

        public GamePublic(Map<AgentPosition, Hand> playerHandMap,
                          Queue<Action> actionQueue,
                          List<Card> offeredCards,
                          int offeredMoney,
                          Set<Rule> ruleSet) {

            this.playerHandMap = playerHandMap;
            this.actionQueue = actionQueue;
            this.offeredCards = offeredCards;
            this.offeredMoney = offeredMoney;
            this.ruleSet = ruleSet;
        }

    }

    public void playRounds(int rounds) {
        for (int i = 0; i < rounds; i ++) {
            Map<AgentPosition, Queue<Action>> actionQueueMap = getActionQueueMap();

        }
    }

    public Map<AgentPosition, Hand> getPlayerHandMap() {
        Map<AgentPosition, Hand> playerHandMap = new HashMap<>();
        this.playerMap.forEach((k, v) -> playerHandMap.put(k, v.getHand()));
        return playerHandMap;
    }

    public Map<AgentPosition, Queue<Action>> getActionQueueMap() {
        Map<AgentPosition, Queue<Action>> actionQueueMap = new HashMap<>();
        this.playerMap.forEach((k, v) -> actionQueueMap.put(k, new LinkedList<>()));
        return actionQueueMap;
    }


}
