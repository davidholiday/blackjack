package com.github.davidholiday.agent.strategy.count.reko;

import com.github.davidholiday.agent.strategy.count.CountStrategy;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.game.RuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class reKoStandardCountStrategy extends reKoCountStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(reKoStandardCountStrategy.class);

    public static final String NAME = "RE_KO_STANDARD_COUNT_STRATEGY";

    public reKoStandardCountStrategy(RuleSet ruleSet, double baseWager) {
        super(ruleSet, baseWager);
    }

    @Override
    public String getName() { return NAME; }

    @Override
    public double getWager(ActionToken actionToken) {

        // https://www.qfit.com/book/ModernBlackjackPage72.htm
        switch (shoeDeckSize) {
            case 1:
                if (count <= 0) {
                    lastAnteWager = baseWager;
                    return baseWager;
                }
                else if (count == 1) {
                    lastAnteWager = baseWager * 2;
                    return lastAnteWager;
                }
                else if (count >= 2) {
                    lastAnteWager = baseWager * 4;
                    return lastAnteWager;
                }
            case 2:
                if (count <= -1) {
                    lastAnteWager = baseWager;
                    return lastAnteWager;
                }
                else if (count == 0) {
                    lastAnteWager = baseWager * 2;
                    return lastAnteWager;
                }
                else if (count == 1) {
                    lastAnteWager = baseWager * 2.5;
                    return lastAnteWager;
                }
                else if (count == 2) {
                    lastAnteWager = baseWager * 5;
                    return lastAnteWager;
                }
                else if (count >= 3) {
                    lastAnteWager = baseWager * 7.5;
                    return lastAnteWager;
                }
            case 4:
                // fall into 6
            case 6:
                // six and eight technically shouldn't be the same ...
            case 8:
                if (count <= -4) {
                    lastAnteWager = baseWager;
                    return lastAnteWager;
                }
                else if (count <= -2) {
                    lastAnteWager = baseWager * 2;
                    return lastAnteWager;
                }
                else if (count <= 0) {
                    lastAnteWager = baseWager * 2.5;
                    return lastAnteWager;
                }
                else if (count <= 2) {
                    lastAnteWager = baseWager * 5;
                    return lastAnteWager;
                }
                else {
                    lastAnteWager = baseWager * 7.5;
                    return lastAnteWager;
                }
            default:
                throw new IllegalStateException("there should only be shoe sizes of 1, 2, 4, 6, or 8 decks");
        }
    }

}
