package com.github.davidholiday.game;


import com.github.davidholiday.agent.Agent;
import com.github.davidholiday.agent.strategy.play.DealerStrategy;
import com.github.davidholiday.agent.strategy.play.NoOpDealerStrategy;
import com.github.davidholiday.agent.strategy.play.StandardDealerStrategy;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.agent.AgentPosition;
import com.github.davidholiday.agent.Dealer;
import com.github.davidholiday.agent.Player;
import com.github.davidholiday.cardcollection.Shoe;
import com.github.davidholiday.util.MessageTemplates;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.xml.transform.Result;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;

public class Game implements Callable<Map<String, String>> {

    public static final String ROUND_COUNT_KEY = "ROUND_COUNT";

    public static final String START_BANKROLL_SUFFIX_KEY = "_START_BANKROLL";

    public static final String END_BANKROLL_SUFFIX_KEY = "_END_BANKROLL";

    public static final String PLAY_STRATEGY_SUFFIX_KEY = "_PLAY_STRATEGY";

    public static final String COUNT_STRATEGY_SUFFIX_KEY = "_COUNT_STRATEGY";

    public static final String BETTING_UNIT_SUFFIX_KEY = "_BETTING_UNIT";

    public static final String RESET_BANKROLL_AFTER_ROUNDS_KEY = "RESET_BANKROLL_AFTER_ROUNDS";

    public static final String GAME_ID_KEY = "GAME_ID";

    public static final int CIRCUIT_BREAKER_FOR_ROUNDS = 1000;

    private static final Logger LOG = LoggerFactory.getLogger(Game.class);

    private Dealer dealer;
    private Map<AgentPosition, Player> playerMap;

    private Map<AgentPosition, Agent> agentMap;

    private RuleSet ruleSet;

    private ActionBroker actionBroker;

    private int numRounds = 1;

    private boolean resetBankRollAfterRounds = false;

    public static class Builder {
        private RuleSet ruleSet = new RuleSet();

        private final DealerStrategy standardDealerStrategy = new StandardDealerStrategy();
        private Shoe shoe = new Shoe(6);
        private Dealer dealer = new Dealer(standardDealerStrategy, shoe, ruleSet);
        private Map<AgentPosition, Player> playerMap;

        private int numRounds = 1;

        private boolean resetBankRollAfterRounds = false;


        public Builder() {
            this.playerMap = new HashMap<>();
        }

        public Builder withPlayer(Player player) {
            AgentPosition agentPosition = player.getAgentPosition();

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

        public Builder withNumRounds(int numRounds) {
            if (numRounds < 1) {
                throw new IllegalArgumentException("numRounds can't be less than 1");
            }

            this.numRounds = numRounds;
            return this;
        }

        public Builder withResetBankRollAfterRounds (boolean resetBankRollAfterRounds) {
            this.resetBankRollAfterRounds = resetBankRollAfterRounds;
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
            game.actionBroker = new ActionBroker(game.agentMap);
            game.numRounds = numRounds;
            game.resetBankRollAfterRounds = resetBankRollAfterRounds;
            return game;
        }

        private void checkRulesAndShoe(RuleSet ruleSet, Dealer dealer) {

            // it's possible to make an empty ruleset. this double checks that not only is the ruleset populated,
            // but that it's populated with a gameplay-legal set of rules
            ruleSet.validateRuleSet();

            // we rely on the RuleSet validation logic to ensure there is a decksize
            // rule in the set and that there is only one of them...
            Rule rule = ruleSet.getRuleSetStream()
                               .filter((r) -> Rule.getDeckRuleSet().contains(r))
                               .findFirst()
                               .get();

            switch (rule) {
                case ONE_DECK_SHOE:
                    if (dealer.getShoeDeckSize() != 1) {
                        String msg = getShoeErrorMessage(rule, dealer.getShoeDeckSize());
                        throw new IllegalStateException(msg);
                    }
                    break;
                case TWO_DECK_SHOE:
                    if (dealer.getShoeDeckSize() != 2) {
                        String msg = getShoeErrorMessage(rule, dealer.getShoeDeckSize());
                        throw new IllegalStateException(msg);
                    }
                    break;
                case FOUR_DECK_SHOE:
                    if (dealer.getShoeDeckSize() != 4) {
                        String msg = getShoeErrorMessage(rule, dealer.getShoeDeckSize());
                        throw new IllegalStateException(msg);
                    }
                    break;
                case SIX_DECK_SHOE:
                    if (dealer.getShoeDeckSize() != 6) {
                        String msg = getShoeErrorMessage(rule, dealer.getShoeDeckSize());
                        throw new IllegalStateException(msg);
                    }
                    break;
                case EIGHT_DECK_SHOE:
                    if (dealer.getShoeDeckSize() != 8) {
                        String msg = getShoeErrorMessage(rule, dealer.getShoeDeckSize());
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

    public Map<String, String> call() {

        // so logback can differentiate where the log streams are coming from and put the streams from
        // each worker into its own file
//        String threadID = String.valueOf(Thread.currentThread().getId());
//        MDC.put("THREAD_LOG_ID", threadID);

        // threads are (rightly) picking up on whatever callable they can get their hands on when they are ready
        // for work - meaning delimiting the logs by worker is causing the worker logs to contain multiple game
        // runs - and thus - is confusing AF. delimiting by game object instead for my sanity
        String gameId = Integer.toHexString(hashCode());
        MDC.put(GAME_ID_KEY, gameId);

        // we return a map with aggregate data for the round...
        Map<String, String> rv = new HashMap<>();
        rv.put(RESET_BANKROLL_AFTER_ROUNDS_KEY, Boolean.toString(resetBankRollAfterRounds));
        rv.put(GAME_ID_KEY, gameId);

        LOG.info("*!* BEGIN RUN OF " + numRounds + " ROUNDS *!* ");
        LOG.info("playerMap is: {}", playerMap);

        for (Map.Entry<AgentPosition, Agent> agentMapEntry : agentMap.entrySet()) {
            LOG.info("{} has bankroll of ${}", agentMapEntry.getKey(), agentMapEntry.getValue().getBankroll());

            LOG.info("{} is using play strategy of {}",
                    agentMapEntry.getKey(),
                    agentMapEntry.getValue().getPlayStrategyName()
            );

            LOG.info("{} is using count strategy of {}",
                    agentMapEntry.getKey(),
                    agentMapEntry.getValue().getCountStrategyName()
            );

            // make sure each agent's ruined flag is set to false before starting the run
            //   this is a callable so it's possible the call() method will be called more than once...
            agentMapEntry.getValue().resetWasRuinedFlag();

            // record start values
            if (agentMapEntry.getKey() != AgentPosition.DEALER) {
                String initialBankrollKey = agentMapEntry.getKey() + START_BANKROLL_SUFFIX_KEY;
                rv.put(initialBankrollKey, Double.toString(agentMapEntry.getValue().getBankroll()));

                String bettingUnitKey = agentMapEntry.getKey() + BETTING_UNIT_SUFFIX_KEY;
                rv.put(bettingUnitKey, Double.toString(agentMapEntry.getValue().getBettingUnit()));

                String playStrategyKey = agentMapEntry.getKey() + PLAY_STRATEGY_SUFFIX_KEY;
                rv.put(playStrategyKey, agentMapEntry.getValue().getPlayStrategyName());

                String countStrategyKey = agentMapEntry.getKey() + COUNT_STRATEGY_SUFFIX_KEY;
                rv.put(countStrategyKey, agentMapEntry.getValue().getCountStrategyName());
            }
        }

        for (int i = 0; i < numRounds; i ++) {
            LOG.info("*!* ROUND START *!* ");
            ActionToken actionToken = new ActionToken.Builder()
                                                     .withAction(Action.GAME_START)
                                                     .withActionSource(AgentPosition.GAME)
                                                     .withActionTarget(AgentPosition.DEALER)
                                                     .withRuleSet(getRuleSet())
                                                     .withPlayerHandMap(getPlayerHandMap())
                                                     .build();

            ActionToken currentActionToken = actionBroker.send(actionToken);
            int cycleCount = 0;
            while (currentActionToken.getActionTarget() != AgentPosition.GAME) {
                if (cycleCount > CIRCUIT_BREAKER_FOR_ROUNDS) {
                    throw new IllegalStateException("circuit breaker tripped - halting on error");
                }

                // ensures token that gets passed has correct ruleset and playerhandmap values
                boolean ruleSetOk = currentActionToken.getRuleSet() == getRuleSet();
                boolean playerHandMapOk = currentActionToken.getPlayerHandMap().equals(getPlayerHandMap());
                boolean discardTrayCardCountOk = currentActionToken.getDiscardTrayCardSize() == getDiscardTrayCardSize();
                if (ruleSetOk == false || playerHandMapOk == false || discardTrayCardCountOk == false) {
                    LOG.debug(
                        "creating new actionToken instance with correct ruleSet, playerHandMap, and discardTrayCount"
                    );
                    currentActionToken = new ActionToken.Builder(currentActionToken)
                                                        .withRuleSet(getRuleSet())
                                                        .withPlayerHandMap(getPlayerHandMap())
                                                        .withDiscardTrayCardSize(getDiscardTrayCardSize())
                                                        .build();
                }

                currentActionToken = actionBroker.send(currentActionToken);
                cycleCount ++;
            }

            LOG.info("*!* ROUND COMPLETE *!* ");
            LOG.info("playerMap is: {}", playerMap);
            for (Map.Entry<AgentPosition, Agent> agentMapEntry : agentMap.entrySet()) {
                LOG.info("{} has bankroll of ${}", agentMapEntry.getKey(), agentMapEntry.getValue().getBankroll());
            }
        }

        // record round tallies
        rv.put(ROUND_COUNT_KEY, Integer.toString(numRounds));
        for (Map.Entry<AgentPosition, Agent> agentMapEntry : agentMap.entrySet()) {
            if (agentMapEntry.getKey() != AgentPosition.DEALER) {
                String endBankrollKey = agentMapEntry.getKey() + END_BANKROLL_SUFFIX_KEY;
                boolean playerWasRuined = agentMapEntry.getValue().getWasRuinedFlag();
                double bankroll = playerWasRuined ? 0 : agentMapEntry.getValue().getBankroll();
                rv.put(endBankrollKey, Double.toString(bankroll));
            }
        }

        if (resetBankRollAfterRounds) {
            for (Map.Entry<AgentPosition, Agent> agentMapEntry : agentMap.entrySet()) {
                agentMapEntry.getValue().resetBankroll();
                LOG.info("{} bankroll reset to ${}", agentMapEntry.getKey(), agentMapEntry.getValue().getBankroll());
            }
        }

        //
        // we reset the 'wasRuined' flag at the top of the round cycle.
        //

        // done
        LOG.info("*!* JSON RESULTS: {}", rv);
        return rv;
    }

    public RuleSet getRuleSet() { return ruleSet; }


    public Map<AgentPosition, Hand> getPlayerHandMap() {
        Map<AgentPosition, Hand> playerHandMap = new HashMap<>();

        for (Map.Entry<AgentPosition, Agent> agentMapEntry : agentMap.entrySet()) {
            if (agentMapEntry.getKey() == AgentPosition.DEALER) { continue; }

            Map<AgentPosition, Hand> agentHandMap = agentMapEntry.getValue().getAllHands();
            for (Map.Entry<AgentPosition, Hand> agentHandMapEntry : agentHandMap.entrySet()) {
                playerHandMap.put(agentHandMapEntry.getKey(), agentHandMapEntry.getValue());
            }
        }

        playerHandMap.put(AgentPosition.DEALER, dealer.getHand());
        return playerHandMap;
    }


    public int getDiscardTrayCardSize() {
        return dealer.getDiscardTrayCardSize();
    }

    public int getDiscardTrayDeckSize() {
        return dealer.getDiscardTrayDeckSize();
    }

}
