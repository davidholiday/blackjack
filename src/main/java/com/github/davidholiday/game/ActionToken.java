package com.github.davidholiday.game;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.agent.AgentPosition;
import com.github.davidholiday.util.GeneralUtils;

import java.util.*;

public class ActionToken {
    private Map<AgentPosition, Hand> playerHandMap;
    private Action action;
    private List<Card> offeredCards;
    private double offeredMoney;
    private AgentPosition actionSource;
    private AgentPosition actionTarget;
    private RuleSet ruleSet;

    private int discardTrayCardSize;

    private int discardTrayDeckSize;

    public static class Builder {

        private Map<AgentPosition, Hand> playerHandMap = new HashMap<>();;
        private Action action = Action.NONE;
        private List<Card> offeredCards = new ArrayList<>();
        private double offeredMoney = 0;

        private AgentPosition actionSource = AgentPosition.NONE;
        private AgentPosition actionTarget = AgentPosition.NONE;
        private RuleSet ruleSet = new RuleSet(new HashSet<Rule>());

        private int discardTrayCardSize = 0;

        //private int discardTrayDeckSize = 0;

        public Builder(ActionToken actionToken) {
            this.playerHandMap = actionToken.playerHandMap;
            this.action = actionToken.action;
            this.offeredCards = actionToken.offeredCards;
            this.offeredMoney = actionToken.offeredMoney;
            this.actionSource = actionToken.actionSource;
            this.actionTarget = actionToken.actionTarget;
            this.ruleSet = actionToken.ruleSet;
            this.discardTrayCardSize = actionToken.discardTrayCardSize;
            //this.discardTrayDeckSize = actionToken.discardTrayDeckSize;
        }

        public Builder(Game game, Action action) {
            this.ruleSet = game.getRuleSet();
            this.action = action;
            this.discardTrayCardSize = game.getDiscardTrayCardSize();
            //this.discardTrayDeckSize = game.getDiscardTrayDeckSize();
        }

        public Builder(Action action) {
            this.action = action;
        }

        public Builder() {}

        public Builder withPlayerHandMap(Map<AgentPosition, Hand> playerHandMap) {
            this.playerHandMap = playerHandMap;
            return this;
        }

        public Builder withActionSource(AgentPosition actionSource) {
            this.actionSource = actionSource;
            return this;
        }

        public Builder withActionTarget(AgentPosition actionTarget) {
            this.actionTarget = actionTarget;
            return this;
        }

        public Builder withAction(Action action) {
            this.action = action;
            return this;
        }

        public Builder withOfferedCards(List<Card> offeredCards) {
            this.offeredCards = offeredCards;
            return this;
        }

        public Builder withOfferedMoney(double offeredMoney) {
            if (offeredMoney < 0) {
                String msg = "offeredMoney argument must be greater than zero but was " + offeredMoney;
                throw new IllegalArgumentException(msg);
            }
            this.offeredMoney = offeredMoney;
            return this;
        }

        public Builder withRuleSet(RuleSet ruleSet) {
            this.ruleSet = ruleSet;
            return this;
        }

        public Builder withDiscardTrayCardSize(int discardTrayCardSize) {
            if (discardTrayCardSize < 0) {
                String msg = "discardTrayCardSize can not be set to negative value: " + discardTrayCardSize;
                throw new IllegalArgumentException(msg);
            }
            this.discardTrayCardSize = discardTrayCardSize;

            return this;
        }

        public Builder withDiscardTrayDeckSize(int discardTrayDeckSize) {
            if (discardTrayCardSize < 0) {
                String msg = "discardTrayCardSize can not be set to negative value: " + discardTrayCardSize;
                throw new IllegalArgumentException(msg);
            }
            this.discardTrayCardSize = discardTrayCardSize;

            return this;
        }

        public ActionToken build() {
            ActionToken actionToken = new ActionToken();
            actionToken.playerHandMap = this.playerHandMap;
            actionToken.actionSource = this.actionSource;
            actionToken.actionTarget = this.actionTarget;
            actionToken.action = this.action;
            actionToken.offeredCards = this.offeredCards;
            actionToken.offeredMoney = this.offeredMoney;
            actionToken.ruleSet = this.ruleSet;
            actionToken.discardTrayCardSize = this.discardTrayCardSize;
//            boolean bothDiscardSizeValuesZero = this.discardTrayCardSize == 0 && this.discardTrayDeckSize == 0;
//            boolean cardDividedByDeckSizeCorrect =
//                    bothDiscardSizeValuesZero ?
//                    true :
//                    this.discardTrayCardSize / this.discardTrayDeckSize == GeneralUtils.DECK_SIZE_NO_JOKERS;
//
//            if (bothDiscardSizeValuesZero == false && cardDividedByDeckSizeCorrect == false) {
//                String msg = "discardTrayCardSize: " + discardTrayCardSize
//                        + " / discardTrayDeckSize: " + discardTrayDeckSize
//                        + " != " + GeneralUtils.DECK_SIZE_NO_JOKERS;
//                throw new IllegalArgumentException(msg);
//            }
//
//            actionToken.discardTrayCardSize = this.discardTrayCardSize;
//            actionToken.discardTrayDeckSize = this.discardTrayDeckSize;
            return actionToken;
        }

    }

    private ActionToken() {}

    // TODO maybe to JSON?
    @Override
    public String toString() {
        return " playerHandMap:  " + playerHandMap.entrySet() +
               " actionSource: " + actionSource +
               " actionTarget: " + actionTarget +
               " action: " + action +
               " offeredCards: " + offeredCards +
               " offeredMoney: " + offeredMoney +
               //" ruleSet: " + ruleSet +
               " discardTrayCardSize: " + discardTrayCardSize;
    }

    public Map<AgentPosition, Hand> getPlayerHandMap() {
        return Collections.unmodifiableMap(playerHandMap);
    }

    public Action getAction() {
        return action;
    }

    public AgentPosition getActionTarget() { return actionTarget; }

    public AgentPosition getActionSource() { return actionSource; }

    public List<Card> getOfferedCards() {
        return Collections.unmodifiableList(offeredCards);
    }

    public double getOfferedMoney() {
        return offeredMoney;
    }

    public RuleSet getRuleSet() {
        return ruleSet;
    }

    public int getDiscardTrayCardSize() { return discardTrayCardSize; }

    public int getDiscardTrayDeckSize() { return discardTrayDeckSize; }

    public ActionToken getDealerNextActionToken() {
        // specifically not including player offering of cards or money in this token
        return new ActionToken.Builder()
                              .withAction(Action.DEALER_NEXT_ACTION)
                              .withRuleSet(ruleSet)
                              .withPlayerHandMap(playerHandMap)
                              .withActionSource(actionTarget)
                              .withActionTarget(AgentPosition.DEALER)
                              .build();
    }

    public static ActionToken getDealerNextActionToken(ActionToken actionToken) {
        // specifically not including player offering of cards or money in this token
        return new ActionToken.Builder()
                              .withAction(Action.DEALER_NEXT_ACTION)
                              .withRuleSet(actionToken.getRuleSet())
                              .withPlayerHandMap(actionToken.getPlayerHandMap())
                              .withActionSource(actionToken.getActionTarget())
                              .withActionTarget(AgentPosition.DEALER)
                              .build();
    }

    public static ActionToken getEndGameActionToken() {
        return new ActionToken.Builder()
                              .withActionSource(AgentPosition.DEALER)
                              .withActionTarget(AgentPosition.GAME)
                              .withAction(Action.GAME_END)
                              .build();
    }


}
















