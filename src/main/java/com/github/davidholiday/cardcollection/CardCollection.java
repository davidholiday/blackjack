package com.github.davidholiday.cardcollection;

import com.github.davidholiday.card.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.*;

import static com.github.davidholiday.util.MessageTemplates.getCountDeltaErrorMessage;

public abstract class CardCollection {

    private static final Logger LOG = LoggerFactory.getLogger(CardCollection.class);

    private final List<Card> cardList = new ArrayList<>();

    private final SecureRandom secureRandom = new SecureRandom();

    public void shuffle() { Collections.shuffle(cardList, secureRandom); }

    public void shuffle(int count) {
         if (count < 1) {
             String msg = getCountDeltaErrorMessage(
                     ">=1",
                     "value for argument [count]",
                     String.valueOf(count)
             );
             LOG.error(msg);
             throw new IllegalArgumentException(msg);
         }

         for (int i = 0; i < count; i++) { shuffle(); }
    }

    public Card draw() {
        try {
            return cardList.remove(0);
        } catch (IndexOutOfBoundsException e) {
            String msg = "attempt to draw card from empty card collection";
            LOG.error(msg);
            throw new IndexOutOfBoundsException(msg);
        }
    }

    public List<Card> draw(int num) {
        try {
            List<Card> rv = new ArrayList<>(cardList.subList(0, num));
            cardList.subList(0, num).clear();
            return rv;
        } catch (IndexOutOfBoundsException e) {
            String msg = MessageFormat.format(
                    "attempt to draw {0} cards from card collection sized {1}",
                    num,
                    cardList.size()
            );
            LOG.error(msg);
            throw new IndexOutOfBoundsException(msg);
        }
    }

    public void cut(int index) {
        if (index < 0 || index > getCardListSize()) {
            String msg = getCountDeltaErrorMessage(
                    "0 >= [count] <= [card collection size]",
                    "value for argument [count]",
                    String.valueOf(index)
            );
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }

        List<Card> cutTail = new ArrayList<>(cardList.subList(index, getCardListSize()));
        cardList.subList(index, getCardListSize()).clear();
        List<Card> cutHead = getAllCards(true);
        cutTail.addAll(cutHead);
        addCards(cutTail);
    }

    public List<Card> getAllCards(boolean remove) {
        List<Card> rv = List.copyOf(cardList);
        if (remove) { cardList.clear(); }
        return rv;
    }

    public void addCards(List<Card> cardList) { this.cardList.addAll(cardList); }

    public int getCardListSize() { return cardList.size(); }

}