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

    public static final int SINGLE_WORKER_ROUND_THRESHOLD = 1001;

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

        /*

        TODO this might create problems as I'm not sure the worker that gets assigned to a given element in the
        TODO   parallel stream is guaranteed to be the worker that sees all of the work through for a given
        TODO   game object...
        TODO     maybe change the Game class to be a Runnable with a baked-in number of rounds?
        TODO     this way you can use an ExecutorService and have more control over what's going on ...

        also this
        https://dzone.com/articles/be-aware-of-forkjoinpoolcommonpool


        v------------ these two are the way -----v
        https://mkyong.com/logging/logback-different-log-file-for-each-thread/

        https://logback.qos.ch/manual/mdc.html

"
The MDC class contains only static methods. It lets the developer place information in a diagnostic context
that can be subsequently retrieved by certain logback components. The MDC manages contextual information on a per
thread basis. Typically, while starting to service a new client request, the developer will insert pertinent contextual
information, such as the client id, client's IP address, request parameters etc. into the MDC. Logback components, if
appropriately configured, will automatically include this information in each log entry.
"


         */
        int numWorkers = RUNTIME_INFO.AVAILABLE_PROCESSORS;
        if (numRounds < SINGLE_WORKER_ROUND_THRESHOLD) {
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

        // again -- this will work once the game object has a proper copy constructor...
        //Collections.nCopies(numWorkers, game);

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
