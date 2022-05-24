package com.github.davidholiday.game;


import com.github.davidholiday.player.Agent;
import com.github.davidholiday.player.AgentPosition;
import com.github.davidholiday.player.Dealer;
import com.github.davidholiday.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game {

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);

    public static class Builder {

        private final RuleSet ruleSet;

        private final Dealer dealer;

        private final Map<AgentPosition, Agent> playerMap;

        public Builder(RuleSet ruleSet, Dealer dealer) {
            this.ruleSet = ruleSet;
            this.dealer = dealer;
            this.playerMap = new HashMap<>();
        }

        public Builder withPlayerAtPosition(Agent player, AgentPosition agentPosition) {
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


    }

    private Game() {

    }


    public class GamePublic {
        /*
        nested inner class GamePublic?
                this way a view object can be created of the game objects data w/o exposing the whole
        game object
        Map<PlayerPosition, Action> actionMap,
        Map<PlayerPosition, Hand> handsMap,
        Map<PlayerPosition, List<Card>> offeredCardsMap,
        Map<PlayerPosition, Integer> offeredMoneyMap,
        Set<Rule> ruleSet,
        Optional<Integer> count
     */

    }



}
