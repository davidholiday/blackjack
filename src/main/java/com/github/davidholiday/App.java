package com.github.davidholiday;

import com.github.davidholiday.agent.AgentPosition;
import com.github.davidholiday.agent.Player;
import com.github.davidholiday.agent.strategy.count.NoCountStrategy;
import com.github.davidholiday.agent.strategy.count.SpeedCountCountStrategy;
import com.github.davidholiday.agent.strategy.play.BasicFourSixEightDeckPlayerStrategy;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.game.Rule;
import com.github.davidholiday.game.RuleSet;
import com.github.davidholiday.util.RuntimeInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import me.tongfei.progressbar.*;




public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static final RuntimeInfo RUNTIME_INFO = new RuntimeInfo();

    public static final int SINGLE_WORKER_ROUND_THRESHOLD = 1000;

    public static final int NUMBER_OF_WORKERS = RUNTIME_INFO.AVAILABLE_PROCESSORS * 2;


    public static void main(String[] args ) {

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


        // each "game" object represents a unit of work for a worker
        // what we want is to create between (1) and (RUNTIME_INFO.AVAILABLE_PROCESSORS * 2) game objects
        // for the workers to chew on per evolution
        int gameListSize =
                totalRounds <= SINGLE_WORKER_ROUND_THRESHOLD
                        ? totalRounds
                        : Math.min((totalRounds / SINGLE_WORKER_ROUND_THRESHOLD), RUNTIME_INFO.AVAILABLE_PROCESSORS * 2);
        // to deal with very low values of totalRounds...
        gameListSize = (gameListSize < 1) ? 1 : gameListSize;


        // this is how many rounds of blackjack each game obj will perform
        // also - this is the amount of 'work' each worker will undertake per evolution
        int roundsPerWorker = Math.min((totalRounds / NUMBER_OF_WORKERS), SINGLE_WORKER_ROUND_THRESHOLD);
        // to deal with very low valus of totalRounds...
        roundsPerWorker = roundsPerWorker == 0 ? 1 : roundsPerWorker;

        // at every evolution:
        //      gameListSize * roundsPerWorker = NUMBER OF ROUNDS OF BLACKJACK SIMULATED
        int roundsPerBatch = gameListSize * roundsPerWorker;

        int numBatches = totalRounds / roundsPerBatch;
        int numRoundsRemainder = totalRounds % roundsPerBatch;

        LOG.info("*!* starting run *!*");
        LOG.info("totalRounds: {} " +
                        "gameListSize: {} " +
                        "roundsPerWorker: {} " +
                        "roundsPerBatch: {} " +
                        "numBatches: {} " +
                        "numRoundsRemainder: {}",
                        totalRounds,
                        gameListSize,
                        roundsPerWorker,
                        roundsPerBatch,
                        numBatches,
                        numRoundsRemainder
        );

        Optional<ExecutorService> executorOptional = Optional.empty();

        try {
            List<Game> gameList = getGameList(gameListSize, roundsPerWorker);
            ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_WORKERS);
            executorOptional = Optional.of(executor);

            ProgressBar pb = new ProgressBarBuilder().setInitialMax(totalRounds)
                                                     .setTaskName("Simulating Rounds of BlackJack")
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
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("something went wrong executing the batch job", e);
        }
        finally {
            if (executorOptional.isPresent()) { executorOptional.get().shutdown(); }
            LOG.info("*!* done *!*");
        }

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

        List<AgentPosition> orderedPlayerList = AgentPosition.getPlayerOrderedList();

        List<Game> gameList = new ArrayList<>();
        for (int i = 0; i < gameListSize; i ++)  {

            RuleSet ruleSet = new RuleSet.Builder()
                                         .withRule(Rule.BLACKJACK_PAYS_THREE_TO_TWO)
                                         .withRule(Rule.SIX_DECK_SHOE)
                                         .withRule(Rule.PLAYER_CAN_DOUBLE_ON_ANY_FIRST_TWO_CARDS)
                                         .withRule(Rule.PLAYER_CAN_RESPLIT_TO_FOUR_HANDS)
                                         .withRule(Rule.PLAYER_CAN_LATE_SURRENDER)
                                         .build();

            AgentPosition playerOnePosition = orderedPlayerList.get(0);
            AgentPosition playerTwoPosition = orderedPlayerList.get(1);

            NoCountStrategy noCountStrategy = new NoCountStrategy(ruleSet, 10);
            SpeedCountCountStrategy speedCountStrategy = new SpeedCountCountStrategy(ruleSet, 10);
            BasicFourSixEightDeckPlayerStrategy playerStrategy = new BasicFourSixEightDeckPlayerStrategy();

            Player playerOne = new Player(noCountStrategy,
                                          playerStrategy,
                                          2500,
                                          ruleSet,
                                          playerOnePosition);

            Player playerTwo = new Player(speedCountStrategy,
                                          playerStrategy,
                                   2500,
                                          ruleSet,
                                          playerTwoPosition);

            Game game = new Game.Builder()
                                .withPlayer(playerOne)
                                .withPlayer(playerTwo)
                                .withRuleSet(ruleSet)
                                .withNumRounds(roundsPerWorker)
                                .build();

            gameList.add(game);
        }

        return gameList;
    }

}
