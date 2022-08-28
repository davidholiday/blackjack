package com.github.davidholiday.agent.strategy.count.reko;

import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.game.RuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class reKoFlatBetExperimentCountStrategy extends reKoCountStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(reKoFlatBetExperimentCountStrategy.class);

    public static final String NAME = "RE_KO_FLAT_BET_EXPERIMENT_STRATEGY";

    public reKoFlatBetExperimentCountStrategy(RuleSet ruleSet, double baseWager) {
        super(ruleSet, baseWager);
    }

    @Override
    public String getName() { return NAME; }

    @Override
    public double getWager(ActionToken actionToken) {
        lastAnteWager = baseWager;
        return baseWager;
    }
}
