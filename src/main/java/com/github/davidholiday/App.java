package com.github.davidholiday;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardSuit;
import com.github.davidholiday.card.CardType;
import com.github.davidholiday.cardcollection.CardCollection;
import com.github.davidholiday.cardcollection.Deck;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.cardcollection.Shoe;
import com.github.davidholiday.game.RuleSet;
import com.github.davidholiday.util.RuntimeInfo;
import com.github.davidholiday.util.GeneralUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static final RuntimeInfo RUNTIME_INFO = new RuntimeInfo();

    public static void main( String[] args ) {
       Shoe shoe = new Shoe(6);
       shoe.shuffle(10);
       shoe.cut();
       String msg = MessageFormat.format("shoe is: {0}", shoe.getAllCards(false));
       LOG.info(msg);

       for (int i = 0; i < 5; i ++) {
           int randy = GeneralUtils.getRandomIntForRange(2, 6);
           Hand hand = new Hand(shoe.draw(randy));
           LOG.info("hand is: " + hand.toString());
       }

       LOG.info("making ruleset to test validation method");
       RuleSet ruleSet = new RuleSet();
    }

}
