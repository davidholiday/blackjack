package com.github.davidholiday.game;

import com.github.davidholiday.agent.Agent;
import com.github.davidholiday.agent.AgentPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActionBroker {

    private final List<ActionToken> flightRecorder = new ArrayList<>();

    private static final Logger LOG = LoggerFactory.getLogger(ActionBroker.class);

    public List<ActionToken> getFlightRecorder() {
        return flightRecorder.stream()
                             .collect(Collectors.toList());
    }


    // pneumatic tube transport
    // https://en.wikipedia.org/wiki/Pneumatic_tube
    public ActionToken send(ActionToken actionToken, Map<AgentPosition, Agent> agentMap) {

        AgentPosition actionTarget = actionToken.getActionTarget();
        if (agentMap.containsKey(actionTarget) == false) {
            throw new IllegalArgumentException("action target: " + actionTarget + " not found in agentMap");
        }

        LOG.info("received actionToken: " + actionToken);
        flightRecorder.add(actionToken);
        ActionToken nextActionToken = agentMap.get(actionTarget)
                                              .act(actionToken);

        LOG.info("received reply actionToken: " + nextActionToken);
        flightRecorder.add(nextActionToken);
        return nextActionToken;
    }

    //
    // TODO serialize() and flush methods for the flight recorder
    //
}