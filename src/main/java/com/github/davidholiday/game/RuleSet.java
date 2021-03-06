package com.github.davidholiday.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import static com.github.davidholiday.game.Rule.*;
import static com.github.davidholiday.util.MessageTemplates.getErrorMessage;

public class RuleSet {

    private static final Logger LOG = LoggerFactory.getLogger(RuleSet.class);

    private final Set<Rule> unmodifiableRuleSet;

    public static class Builder {

        private Set<Rule> ruleSetJava;

        public Builder() {
            ruleSetJava = new HashSet<>();
        }

        public Builder withRule(Rule rule) {
            ruleSetJava.add(rule);
            return this;
        }

        public RuleSet build() {
            // ruleset is evaluated in RuleSet constructor
            return new RuleSet(ruleSetJava);
        }

    }

    public RuleSet() {

        // defaults set as per Wizard of Odds
        // https://wizardofodds.com/games/blackjack/calculator/
        Set<Rule> defaultRuleSet = Stream.of(
                SIX_DECK_SHOE,
                PLAYER_CAN_DOUBLE_AFTER_SPLIT,
                PLAYER_CAN_DOUBLE_ON_ANY_FIRST_TWO_CARDS,
                PLAYER_CAN_RESPLIT_TO_FOUR_HANDS,
                PLAYER_LOSES_ONLY_ORIGINAL_BET_AGAINST_DEALER_BLACKJACK,
                BLACKJACK_PAYS_THREE_TO_TWO
        ).collect(Collectors.toUnmodifiableSet());

        validateRuleSet(defaultRuleSet);
        unmodifiableRuleSet = defaultRuleSet;
    }

    public RuleSet(Set<Rule> ruleSet) {
        if (ruleSet.size() != 0) {
            validateRuleSet(ruleSet);
        }
        this.unmodifiableRuleSet = Collections.unmodifiableSet(ruleSet);
    }


    public boolean contains(Rule rule) { return unmodifiableRuleSet.contains(rule); }

    public void validateRuleSet() {
        this.validateRuleSet(this.unmodifiableRuleSet);
    }

    public void validateRuleSet(Set<Rule> ruleSet) {
        String errorMessage = "";

        if (ruleSet.contains(PLAYER_CAN_EARLY_SURRENDER)) {
            throw new IllegalStateException("rule PLAYER_CAN_EARLY_SURRENDER is not currently implemented by simulator");
        }

        // ensure ruleset contains one and only one deck definition
        //
        int expectedDeckRules = 1;
        Long actualDeckRulesL = ruleSet.stream()
                                       .filter((rule) -> Rule.getDeckRuleSet().contains(rule))
                                       .count();

        errorMessage = getErrorMessage(
                expectedDeckRules,
                "[(n)_DECK_SHOE] rules",
                actualDeckRulesL.intValue()
        );
        if (expectedDeckRules != actualDeckRulesL.intValue()) {
            throw new IllegalArgumentException(errorMessage);
        }


        // ensure ruleset contains one and only one PLAYER_CAN_DOUBLE_ON definition
        //
        int expectedPlayerCanDoubleOnRules = 1;
        Long actualPlayerCanDoubleOnRulesL = ruleSet.stream()
                                                    .filter((rule) -> Rule.getPlayerCanDoubleOnRuleSet().contains(rule))
                                                    .count();

        errorMessage = getErrorMessage(
                expectedPlayerCanDoubleOnRules,
                "[PLAYER_CAN_DOUBLE_ON] Rules",
                actualPlayerCanDoubleOnRulesL.intValue()
        );
        if (expectedPlayerCanDoubleOnRules != actualPlayerCanDoubleOnRulesL.intValue()) {
            throw new IllegalArgumentException(errorMessage);
        }

        // ensure ruleset contains one and only one PLAYER_CAN_RESPLIT_TO definition
        //
        int expectedPlayerCanResplitToRules = 1;
        Long actualPlayerCanResplitToRulesL = ruleSet.stream()
                                                     .filter((rule) -> Rule.getPlayerCanResplitToRuleSet().contains(rule))
                                                     .count();
        errorMessage = getErrorMessage(
                expectedPlayerCanResplitToRules,
                "[PLAYER_CAN_RESPLIT_TO] Rules",
                actualPlayerCanResplitToRulesL.intValue()
        );
        if (expectedPlayerCanResplitToRules != actualPlayerCanResplitToRulesL.intValue()) {
            throw new IllegalArgumentException(errorMessage);
        }


        // ensure ruleset contains one and only one BLACKJACK_PAYS definition
        //
        int expectedBlackJackPaysRules = 1;
        Long actualBlackJackPaysRulesL = ruleSet.stream()
                                                .filter((rule) -> Rule.getBlackJackPaysRuleSet().contains(rule))
                                                .count();
        errorMessage = getErrorMessage(
                expectedBlackJackPaysRules,
                "[BLACKJACK_PAYS] Rules",
                actualBlackJackPaysRulesL.intValue()
        );
        if (expectedBlackJackPaysRules != actualBlackJackPaysRulesL.intValue()) {
            throw new IllegalArgumentException(errorMessage);
        }

        // ensure ruleset contains one and only one PLAYER_CAN_SURRENDER definition
        //
        int expectedMaxSurrenderRuleCount = 1;
        Long actualSurrenderRuleCountL = ruleSet.stream()
                                                .filter((rule) -> Rule.getSurrenderRuleSet().contains(rule))
                                                .count();
        errorMessage = getErrorMessage(
                expectedMaxSurrenderRuleCount,
                "max of [PLAYER_CAN_SURRENDER] Rules",
                actualSurrenderRuleCountL.intValue()
        );
        if (actualSurrenderRuleCountL.intValue() > expectedMaxSurrenderRuleCount) {
            throw new IllegalArgumentException(errorMessage);
        }

    }

    public Stream<Rule> getRuleSetStream() {
        return unmodifiableRuleSet.stream();
    }

    public List<Rule> getRuleSetList() { return unmodifiableRuleSet.stream().collect(Collectors.toList()); }

    public boolean isEmpty() { return unmodifiableRuleSet.isEmpty(); }

    @Override
    public String toString() {
        return unmodifiableRuleSet.toString();
    }

}
