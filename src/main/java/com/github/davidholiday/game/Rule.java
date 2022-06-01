package com.github.davidholiday.game;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Rule {

    // ruleset derived from wizard of odds
    // https://wizardofodds.com/games/blackjack/calculator/

    ONE_DECK_SHOE,
    TWO_DECK_SHOE,
    FOUR_DECK_SHOE,
    SIX_DECK_SHOE,
    EIGHT_DECK_SHOE,
    DEALER_CAN_HIT_SOFT_17,
    PLAYER_CAN_DOUBLE_AFTER_SPLIT,
    PLAYER_CAN_DOUBLE_ON_ANY_FIRST_TWO_CARDS,
    PLAYER_CAN_DOUBLE_ON_NINE_ELEVEN_ONLY,
    PLAYER_CAN_DOUBLE_ON_TEN_ELEVEN_ONLY,
    PLAYER_CAN_RESPLIT_TO_TWO_HANDS,
    PLAYER_CAN_RESPLIT_TO_THREE_HANDS,
    PLAYER_CAN_RESPLIT_TO_FOUR_HANDS,
    PLAYER_CAN_RESPLIT_ACES,
    PLAYER_CAN_HIT_SPLIT_ACES,
    PLAYER_LOSES_ONLY_ORIGINAL_BET_AGAINST_DEALER_BLACKJACK,
    PLAYER_CAN_EARLY_SURRENDER,
    PLAYER_CAN_LATE_SURRENDER,
    BLACKJACK_PAYS_THREE_TO_TWO,
    BLACKJACK_PAYS_SIX_TO_FIVE;

    public static Set<Rule> getDeckRuleSet() {
        return Stream.of(
                ONE_DECK_SHOE,
                TWO_DECK_SHOE,
                FOUR_DECK_SHOE,
                SIX_DECK_SHOE,
                EIGHT_DECK_SHOE
        ).collect(Collectors.toSet());
    }

    public static Set<Rule> getPlayerCanDoubleOnRuleSet() {
        return Stream.of(
                PLAYER_CAN_DOUBLE_ON_ANY_FIRST_TWO_CARDS,
                PLAYER_CAN_DOUBLE_ON_NINE_ELEVEN_ONLY,
                PLAYER_CAN_DOUBLE_ON_TEN_ELEVEN_ONLY
        ).collect(Collectors.toSet());
    }

    public static Set<Rule> getPlayerCanResplitToRuleSet() {
        return Stream.of(
                PLAYER_CAN_RESPLIT_TO_TWO_HANDS,
                PLAYER_CAN_RESPLIT_TO_THREE_HANDS,
                PLAYER_CAN_RESPLIT_TO_FOUR_HANDS
        ).collect(Collectors.toSet());
    }

    public static Set<Rule> getBlackJackPaysRuleSet() {
        return Stream.of(
                BLACKJACK_PAYS_THREE_TO_TWO,
                BLACKJACK_PAYS_SIX_TO_FIVE
        ).collect(Collectors.toSet());
    }

    public static Set<Rule> getSurrenderRuleSet() {
        return Stream.of(
                PLAYER_CAN_EARLY_SURRENDER,
                PLAYER_CAN_LATE_SURRENDER
        ).collect(Collectors.toSet());
    }

}
