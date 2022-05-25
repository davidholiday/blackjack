package com.github.davidholiday.game;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.player.AgentPosition;

import java.util.*;

public class ActionToken {
    private Map<AgentPosition, Hand> playerHandMap;
    private Action action;
    private List<Card> offeredCards;
    private double offeredMoney;
    private RuleSet ruleSet;

    public static class Builder {
        private Map<AgentPosition, Hand> playerHandsMap = new HashMap<>();;
        private Action action = Action.NONE;
        private List<Card> offeredCards = new ArrayList<>();
        private double offeredMoney = 0;
        private RuleSet ruleSet;

        public Builder(Game game, Action action) {
            this.playerHandsMap = game.getPlayerHandMap();
            this.ruleSet = game.getRuleSet();
            this.action = action;
        }

        public Builder(Action action) {
            this.action = action;
        }

        public Builder() {}

        public Builder withPlayerHandsMap(Map<AgentPosition, Hand> playerHandsMap) {
            this.playerHandsMap = playerHandsMap;
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
                String msg = "offeredMoney argument must be greater than zero but was " + offeredMoney
                throw new IllegalArgumentException(msg);
            }
            this.offeredMoney = offeredMoney;
            return this;
        }

        public ActionToken build() {
            ActionToken actionToken = new ActionToken();
            actionToken.playerHandMap = this.playerHandsMap;
            actionToken.action = this.action;
            actionToken.offeredCards = this.offeredCards;
            actionToken.offeredMoney = this.offeredMoney;
            return actionToken;
        }


    }

    private ActionToken() {}


}
















