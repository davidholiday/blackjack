package com.github.davidholiday;

import com.github.davidholiday.agent.AgentPosition;
import com.github.davidholiday.agent.Player;
import com.github.davidholiday.agent.strategy.count.NoCountStrategy;
import com.github.davidholiday.agent.strategy.play.BasicFourSixEightDeckPlayerStrategy;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.game.Rule;
import com.github.davidholiday.game.RuleSet;
import com.github.davidholiday.util.RuntimeInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static final RuntimeInfo RUNTIME_INFO = new RuntimeInfo();

    public static void main( String[] args ) {

        List<Game> gameList = getGameList();

        int numRounds = 1;
        if (args.length == 1) {
            try {
                numRounds = Integer.parseInt(args[0]);
                if (numRounds < 1) { throw new NumberFormatException();}
            } catch (NumberFormatException e) {
                LOG.error("numRounds argument must be a positive number greater than zero!");
                System.exit(8);
            }
        }

        int numWorkers = RUNTIME_INFO.AVAILABLE_PROCESSORS;
        if (numRounds < 1001) {
            LOG.info("running in single thread for: {} rounds", numRounds);
            gameList.get(0).playRounds(numRounds);
        } else {
            LOG.info("will spin up: {} threads for: rounds", numRounds);
            int roundsPerWorker = numRounds / numWorkers;
            int roundsPerWorkerRemainder = numRounds % numWorkers;
            LOG.info("{} rounds will be handled per thread", roundsPerWorker);
            if (roundsPerWorkerRemainder > 0) {
                LOG.info("handling remainder of {} rounds in single thread first", roundsPerWorkerRemainder);
                gameList.get(0).playRounds(roundsPerWorkerRemainder);
                LOG.info("done!");
            }
            LOG.info("starting workers...");
            gameList.parallelStream().forEach(g -> g.playRounds(roundsPerWorker));
            LOG.info("done!");
        }

        //game.playRounds(numRounds);

    }

    /**
     * doing it this way for now because a copy constructor would involve a lot of deep copy work that I
     * don't want to do right now...
     * @return
     */
    private static List<Game> getGameList() {
        List<Game> gameList = new ArrayList<>();

        for (int i = 0; i < RUNTIME_INFO.AVAILABLE_PROCESSORS; i ++)  {
            NoCountStrategy noCountStrategy = new NoCountStrategy();
            //NoOpPlayerStrategy noOpPlayerStrategy = new NoOpPlayerStrategy();
            BasicFourSixEightDeckPlayerStrategy playerStrategy = new BasicFourSixEightDeckPlayerStrategy();
            Player playerOne = new Player(noCountStrategy, playerStrategy, 10);
            Player playerTwo = new Player(noCountStrategy, playerStrategy, 10);

            RuleSet ruleSet = new RuleSet.Builder()
                                         .withRule(Rule.BLACKJACK_PAYS_THREE_TO_TWO)
                                         .withRule(Rule.SIX_DECK_SHOE)
                                         .withRule(Rule.PLAYER_CAN_DOUBLE_ON_ANY_FIRST_TWO_CARDS)
                                         .withRule(Rule.PLAYER_CAN_RESPLIT_TO_FOUR_HANDS)
                                         .withRule(Rule.PLAYER_CAN_LATE_SURRENDER)
                                         .build();

            Game game = new Game.Builder()
                                .withPlayerAtPosition(playerOne, AgentPosition.PLAYER_ONE)
                                .withPlayerAtPosition(playerTwo, AgentPosition.PLAYER_TWO)
                                .withRuleSet(ruleSet)
                                .build();

            gameList.add(game);
        }

        return gameList;
    }

}
