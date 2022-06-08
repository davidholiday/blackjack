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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import me.tongfei.progressbar.*;




public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static final RuntimeInfo RUNTIME_INFO = new RuntimeInfo();

    public static final int SINGLE_WORKER_ROUND_THRESHOLD = 1000;

    public static void main( String[] args ) throws InterruptedException, ExecutionException {

        int totalRounds = 1;
        if (args.length == 1) {
            try {
                totalRounds = Integer.parseInt(args[0]);
                if (totalRounds < 1) { throw new NumberFormatException();}
            } catch (NumberFormatException e) {
                LOG.error("numRounds argument must be a positive number greater than zero!");
                System.exit(8);
            }
        }

        int numWorkers = RUNTIME_INFO.AVAILABLE_PROCESSORS * 2;

        // each "game" object represents a unit of work for a worker
        // what we want is to create between (1) and (RUNTIME_INFO.AVAILABLE_PROCESSORS * 2) game objects
        // for the workers to chew on per evolution
        int gameListSize =
                totalRounds <= SINGLE_WORKER_ROUND_THRESHOLD
                        ? totalRounds
                        : Math.min((totalRounds / SINGLE_WORKER_ROUND_THRESHOLD), RUNTIME_INFO.AVAILABLE_PROCESSORS * 2);

        // this is how many rounds of blackjack each game obj will perform
        // also - this is the amount of 'work' each worker will undertake per evolution
        int roundsPerWorker = Math.min((totalRounds / numWorkers), SINGLE_WORKER_ROUND_THRESHOLD);

        // at every evolution:
        //      gameListSize * roundsPerWorker = NUMBER OF HANDS OF BLACKJACK SIMULATED
        int roundsPerBatch = gameListSize * roundsPerWorker;
        int numBatches = totalRounds / roundsPerBatch;
        int numRoundsRemainder = totalRounds % roundsPerBatch;


        List<Game> gameList = getGameList(gameListSize, roundsPerWorker);
        ExecutorService executor = Executors.newFixedThreadPool(numWorkers);

        ProgressBar pb = new ProgressBarBuilder().setInitialMax(totalRounds)
                                                 .setTaskName("Simulating Rounds of BlackJack")
                                                 .setInitialMax(totalRounds)
                                                 .build();

        for (int i = 0; i < numBatches; i ++) {
            List<Future<Integer>> resultsList = executor.invokeAll(gameList);

            for (Future<Integer> future : resultsList) {
                pb.stepBy(Long.valueOf(future.get()));
                pb.refresh();
            }
        }
        // and now for the +1
        if (numRoundsRemainder > 0) {
            List<Game> gameListRemainder = getGameList(1, numRoundsRemainder);
            List<Future<Integer>> resultsList = executor.invokeAll(gameListRemainder);

            for (Future<Integer> future : resultsList) {
                pb.stepBy(Long.valueOf(future.get()));
                pb.refresh();
            }
        }


        executor.shutdown();




//        v------------ these two are the way -----v
//        https://mkyong.com/logging/logback-different-log-file-for-each-thread/
//
//        https://logback.qos.ch/manual/mdc.html
//
//        https://github.com/ctongfei/progressbar
//
//"
//The MDC class contains only static methods. It lets the developer place information in a diagnostic context
//that can be subsequently retrieved by certain logback components. The MDC manages contextual information on a per
//thread basis. Typically, while starting to service a new client request, the developer will insert pertinent contextual
//information, such as the client id, client's IP address, request parameters etc. into the MDC. Logback components, if
//appropriately configured, will automatically include this information in each log entry.
//"

    }

    /**
     * doing it this way for now because a copy constructor would involve a lot of deep copy work that I
     * don't want to do right now...
     *
     * also - this is wonky AF but I want to be able to report intermediate results and the java future/callable
     * interface doesn't provide that. it wants to frame things in terms of when things are done. given that I
     * may want to spin up a simulation of millions of hands, waiting that long to update the progress bar is not an
     * option.
     *
     * @return
     */
    private static List<Game> getGameList(int gameListSize, int roundsPerWorker) {

        List<Game> gameList = new ArrayList<>();
        for (int i = 0; i < gameListSize; i ++)  {
            NoCountStrategy noCountStrategy = new NoCountStrategy();
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
                                .withNumRounds(roundsPerWorker)
                                .build();

            gameList.add(game);
        }

        return gameList;
    }

}
