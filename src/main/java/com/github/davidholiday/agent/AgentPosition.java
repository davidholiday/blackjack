package com.github.davidholiday.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum AgentPosition {
    GAME,
    DEALER,
    FIRST_BASE,
    SHORT_STOP,
    THIRD_BASE,
    PLAYER_ONE,
    PLAYER_ONE$H1,
    PLAYER_ONE$H2,
    PLAYER_ONE$H3,
    PLAYER_ONE$H4,
    PLAYER_TWO,
    PLAYER_TWO$H1,
    PLAYER_TWO$H2,
    PLAYER_TWO$H3,
    PLAYER_TWO$H4,
    PLAYER_THREE,
    PLAYER_THREE$H1,
    PLAYER_THREE$H2,
    PLAYER_THREE$H3,
    PLAYER_THREE$H4,
    PLAYER_FOUR,
    PLAYER_FOUR$H1,
    PLAYER_FOUR$H2,
    PLAYER_FOUR$H3,
    PLAYER_FOUR$H4,
    PLAYER_FIVE,
    PLAYER_FIVE$H1,
    PLAYER_FIVE$H2,
    PLAYER_FIVE$H3,
    PLAYER_FIVE$H4,
    PLAYER_SIX,
    PLAYER_SIX$H1,
    PLAYER_SIX$H2,
    PLAYER_SIX$H3,
    PLAYER_SIX$H4,
    PLAYER_SEVEN,
    PLAYER_SEVEN$H1,
    PLAYER_SEVEN$H2,
    PLAYER_SEVEN$H3,
    PLAYER_SEVEN$H4,
    NONE;

    public static List<AgentPosition> getPlayerOrderedList() {
        return List.of(
                PLAYER_ONE,
                PLAYER_TWO,
                PLAYER_THREE,
                PLAYER_FOUR,
                PLAYER_FIVE,
                PLAYER_SIX,
                PLAYER_SEVEN
        );
    }

    public static List<AgentPosition> getAgentHandList(AgentPosition agentPosition) {

        switch (agentPosition) {
            case PLAYER_ONE:
                return List.of(
                        PLAYER_ONE$H1,
                        PLAYER_ONE$H2,
                        PLAYER_ONE$H3,
                        PLAYER_ONE$H4
                );
            case PLAYER_TWO:
                return List.of(
                        PLAYER_TWO$H1,
                        PLAYER_TWO$H2,
                        PLAYER_TWO$H3,
                        PLAYER_TWO$H4
                );
            case PLAYER_THREE:
                return List.of(
                        PLAYER_THREE$H1,
                        PLAYER_THREE$H2,
                        PLAYER_THREE$H3,
                        PLAYER_THREE$H4
                );
            case PLAYER_FOUR:
                return List.of(
                        PLAYER_FOUR$H1,
                        PLAYER_FOUR$H2,
                        PLAYER_FOUR$H3,
                        PLAYER_FOUR$H4
                );
            case PLAYER_FIVE:
                return List.of(
                        PLAYER_FIVE$H1,
                        PLAYER_FIVE$H2,
                        PLAYER_FIVE$H3,
                        PLAYER_FIVE$H4
                );
            case PLAYER_SIX:
                return List.of(
                        PLAYER_SIX$H1,
                        PLAYER_SIX$H2,
                        PLAYER_SIX$H3,
                        PLAYER_SIX$H4
                );
            case PLAYER_SEVEN:
                return List.of(
                        PLAYER_SEVEN$H1,
                        PLAYER_SEVEN$H2,
                        PLAYER_SEVEN$H3,
                        PLAYER_SEVEN$H4
                );
            default:
                return new ArrayList<AgentPosition>();
        }

    }



}
