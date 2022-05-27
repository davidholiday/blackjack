package com.github.davidholiday.game;


import com.github.davidholiday.agent.Agent;
import com.github.davidholiday.agent.strategy.play.NoOpDealerStrategy;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.agent.AgentPosition;
import com.github.davidholiday.agent.Dealer;
import com.github.davidholiday.agent.Player;
import com.github.davidholiday.cardcollection.Shoe;
import com.github.davidholiday.util.MessageTemplates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Game {

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);
    private Dealer dealer;
    private Map<AgentPosition, Player> playerMap;

    private Map<AgentPosition, Agent> agentMap;

    private RuleSet ruleSet;

    public static class Builder {
        private RuleSet ruleSet = new RuleSet();

        private NoOpDealerStrategy noOpPlayStrategy = new NoOpDealerStrategy();
        private Shoe shoe = new Shoe(6);
        private Dealer dealer = new Dealer(noOpPlayStrategy, shoe);
        private Map<AgentPosition, Player> playerMap;

        public Builder() {
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

        public Builder withDealer(Dealer dealer) {
            this.dealer = dealer;
            return this;
        }

        public Builder withRuleSet(RuleSet ruleSet) {
            this.ruleSet = ruleSet;
            return this;
        }

        public Game build() {
            Game game = new Game();

            game.ruleSet = ruleSet;
            game.dealer = dealer;
            checkRulesAndShoe(ruleSet, dealer);

            game.playerMap = Collections.unmodifiableMap(playerMap);

            Map<AgentPosition, Agent> agentMap = new HashMap<>();
            agentMap.put(AgentPosition.DEALER, dealer);
            for (AgentPosition agentPosition : playerMap.keySet()) {
                agentMap.put(agentPosition, playerMap.get(agentPosition));
            }
            game.agentMap = Collections.unmodifiableMap(agentMap);

            return game;
        }

        private void checkRulesAndShoe(RuleSet ruleSet, Dealer dealer) {

            // we rely on the RuleSet validation logic to ensure there is a decksize
            // rule in the set and that there is only one of them...
            Rule rule = ruleSet.getRuleSetStream()
                    .filter((r) -> Rule.getDeckRuleSet().contains(r))
                    .findFirst()
                    .get();

            switch (rule) {
                case ONE_DECK_SHOE:
                    if (dealer.getShoeSize() != 1) {
                        String msg = getShoeErrorMessage(rule, dealer.getShoeSize());
                        throw new IllegalStateException(msg);
                    }
                    break;
                case TWO_DECK_SHOE:
                    if (dealer.getShoeSize() != 2) {
                        String msg = getShoeErrorMessage(rule, dealer.getShoeSize());
                        throw new IllegalStateException(msg);
                    }
                    break;
                case FOUR_DECK_SHOE:
                    if (dealer.getShoeSize() != 4) {
                        String msg = getShoeErrorMessage(rule, dealer.getShoeSize());
                        throw new IllegalStateException(msg);
                    }
                    break;
                case SIX_DECK_SHOE:
                    if (dealer.getShoeSize() != 6) {
                        String msg = getShoeErrorMessage(rule, dealer.getShoeSize());
                        throw new IllegalStateException(msg);
                    }
                    break;
                case EIGHT_DECK_SHOE:
                    if (dealer.getShoeSize() != 8) {
                        String msg = getShoeErrorMessage(rule, dealer.getShoeSize());
                        throw new IllegalStateException(msg);
                    }
                    break;
            }
        }

        private String getShoeErrorMessage(Rule rule, int shoeSize) {
            return MessageTemplates.getErrorMessage(
                    rule.name(),
                    "in dealer shoe object",
                    String.valueOf(shoeSize)
            );
        }

    }
    private Game() {}

    public void playRounds(int rounds) {

        ActionBroker actionBroker = new ActionBroker();
        for (int i = 0; i < rounds; i ++) {
            ActionToken actionToken = new ActionToken.Builder()
                                                     .withAction(Action.GAME_START)
                                                     .withActionSource(AgentPosition.GAME)
                                                     .withActionTarget(AgentPosition.DEALER)
                                                     .withRuleSet(getRuleSet())
                                                     .build();

            ActionToken currentActionToken = actionBroker.send(actionToken, agentMap);
            while (currentActionToken.getActionTarget() != AgentPosition.GAME) {
                currentActionToken = actionBroker.send(currentActionToken, agentMap);
            }

        }
    }

    public RuleSet getRuleSet() { return ruleSet; }


    public Map<AgentPosition, Hand> getPlayerHandMap() {
        Map<AgentPosition, Hand> playerHandMap = new HashMap<>();
        playerMap.forEach((k, v) -> playerHandMap.put(k, v.getHand()));
        return playerHandMap;
    }



}
