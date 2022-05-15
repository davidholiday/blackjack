package com.github.davidholiday;

import com.github.davidholiday.cardcollection.Deck;
import com.github.davidholiday.cardcollection.Shoe;
import com.github.davidholiday.util.RuntimeInfo;
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

       deck.cut(20);
       msg = MessageFormat.format("deck is: {0}", deck.getAllCards(false));
       LOG.info(msg);

       Shoe shoe = new Shoe(6);
       msg = MessageFormat.format("show is: {0}", shoe.getAllCards(false));
       LOG.info(msg);

       shoe.cut();
       msg = MessageFormat.format("show is: {0}", shoe.getAllCards(false));
       LOG.info(msg);
    }

}
