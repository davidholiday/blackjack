package com.github.davidholiday;

import com.github.davidholiday.agent.AgentPosition;
import com.github.davidholiday.agent.Player;
import com.github.davidholiday.agent.strategy.count.NoCountStrategy;
import com.github.davidholiday.agent.strategy.play.NoOpDealerStrategy;
import com.github.davidholiday.agent.strategy.play.NoOpPlayerStrategy;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.Shoe;
import com.github.davidholiday.game.ActionBroker;
import com.github.davidholiday.game.Game;
import com.github.davidholiday.game.Rule;
import com.github.davidholiday.game.RuleSet;
import com.github.davidholiday.util.RuntimeInfo;
import com.github.davidholiday.util.GeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static final RuntimeInfo RUNTIME_INFO = new RuntimeInfo();

    public static void main( String[] args ) {

        NoCountStrategy noCountStrategy = new NoCountStrategy();
        NoOpPlayerStrategy noOpPlayerStrategy = new NoOpPlayerStrategy();
        Player playerOne = new Player(noCountStrategy, noOpPlayerStrategy, 1000);
        Player playerTwo = new Player(noCountStrategy, noOpPlayerStrategy, 1000);
        RuleSet ruleSet = new RuleSet.Builder()
                                     .withRule(Rule.BLACKJACK_PAYS_THREE_TO_TWO)
                                     .withRule(Rule.SIX_DECK_SHOE)
                                     .withRule(Rule.PLAYER_CAN_DOUBLE_ON_ANY_FIRST_TWO_CARDS)
                                     .withRule(Rule.PLAYER_CAN_RESPLIT_TO_FOUR_HANDS)
                                     .build();

        Game game = new Game.Builder()
                            .withPlayerAtPosition(playerOne, AgentPosition.PLAYER_ONE)
                            .withPlayerAtPosition(playerTwo, AgentPosition.PLAYER_TWO)
                            .withRuleSet(ruleSet)
                            .build();

        game.playRounds(1);

    }

}
