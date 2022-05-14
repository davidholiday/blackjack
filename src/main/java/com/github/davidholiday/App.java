package com.github.davidholiday;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static final RuntimeInfo RUNTIME_INFO = new RuntimeInfo();

    public static void main( String[] args ) {
       Deck deck = new Deck(false);
       String msg = MessageFormat.format("deck is: {0}", deck.getAllCards(false));
       LOG.info(msg);

        Deck deckWithJokers = new Deck(true);
        msg = MessageFormat.format("deckWithJokers is: {0}", deckWithJokers.getAllCards(false));
        LOG.info(msg);

        deckWithJokers.shuffle();
        msg = MessageFormat.format("deckWithJokers is now: {0}", deckWithJokers.getAllCards(false));
        LOG.info(msg);
    }

}
