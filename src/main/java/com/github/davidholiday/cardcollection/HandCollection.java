package com.github.davidholiday.cardcollection;

import com.github.davidholiday.game.Rule;
import com.github.davidholiday.game.RuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class HandCollection {

    private static final Logger LOG = LoggerFactory.getLogger(HandCollection.class);

    private List<Hand> handList;

    private Rule resplitToRule;

    public HandCollection(RuleSet ruleSet) {

        boolean splitRuleFound = false;
        for (Rule rule : Rule.getPlayerCanResplitToRuleSet()) {
            if (ruleSet.contains(rule)) {
                resplitToRule = rule;

                if (splitRuleFound == false) { splitRuleFound = true; }
                else { throw new IllegalStateException("RuleSet has more than one RESPLIT_TO rule and it shouldn't!"); }

            }
        }

        // this will help prevent resplits beyond what the rules say is allowable
        switch (resplitToRule) {
            case PLAYER_CAN_RESPLIT_TO_FOUR_HANDS:
                handList = Arrays.asList(new Hand[4]);
                break;
            case PLAYER_CAN_RESPLIT_TO_THREE_HANDS:
                handList = Arrays.asList(new Hand[3]);
                break;
            case PLAYER_CAN_RESPLIT_TO_TWO_HANDS:
                handList = Arrays.asList(new Hand[2]);
                break;
            default:
                throw new IllegalStateException("can't find rule with which to create HandCollection object!");
        }

    }



}
