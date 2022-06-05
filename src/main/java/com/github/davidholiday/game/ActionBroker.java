package com.github.davidholiday.game;

import com.github.davidholiday.agent.Agent;
import com.github.davidholiday.agent.AgentPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

public class ActionBroker {

    private final Map<AgentPosition, Agent> agentMap;

    private final List<ActionToken> flightRecorder = new ArrayList<>();

    private static final Logger LOG = LoggerFactory.getLogger(ActionBroker.class);

    public List<ActionToken> getFlightRecorder() {
        return flightRecorder.stream()
                             .collect(Collectors.toList());
    }

    public ActionBroker() {
        this.agentMap = new HashMap<>();
    }

    public ActionBroker(Map<AgentPosition, Agent> agentMap) {
        this.agentMap = agentMap;
    }

    public void addAgent(AgentPosition agentPosition, Agent agent) {
        agentMap.put(agentPosition, agent);
    }

    // pneumatic tube transport
    // https://en.wikipedia.org/wiki/Pneumatic_tube

    public ActionToken send(ActionToken actionToken) {

        AgentPosition actionTarget = actionToken.getActionTarget();
        if (this.agentMap.containsKey(actionTarget) == false) {
            throw new IllegalArgumentException("action target: " + actionTarget + " not found in agentMap");
        }

        LOG.debug("received actionToken: " + actionToken);
        //flightRecorder.add(actionToken);
        ActionToken nextActionToken = agentMap.get(actionTarget)
                                              .act(actionToken);

        if (nextActionToken.getAction() == Action.GAME_END) {
            LOG.debug("received actionToken: " + nextActionToken);
        }
        //flightRecorder.add(nextActionToken);
        return nextActionToken;
    }

    public ActionToken send(ActionToken actionToken, Map<AgentPosition, Agent> agentMap) {

        AgentPosition actionTarget = actionToken.getActionTarget();
        if (agentMap.containsKey(actionTarget) == false) {
            throw new IllegalArgumentException("action target: " + actionTarget + " not found in agentMap");
        }

        LOG.debug("received actionToken: " + actionToken);
        //flightRecorder.add(actionToken);
        ActionToken nextActionToken = agentMap.get(actionTarget)
                                              .act(actionToken);

        LOG.debug("received reply actionToken: " + nextActionToken);
        //flightRecorder.add(nextActionToken);
        return nextActionToken;
    }

    //
    // TODO serialize() and flush methods for the flight recorder
    // TODO flight recorder should get serialized into a json object
    //
}
