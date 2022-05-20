package com.github.davidholiday.cardcollection;

import com.github.davidholiday.App;
import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardSuit;
import com.github.davidholiday.card.CardType;
import com.github.davidholiday.util.MessageTemplates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.github.davidholiday.util.MessageTemplates.getErrorMessage;

public abstract class CardCollection {

    private static final Logger LOG = LoggerFactory.getLogger(CardCollection.class);

    private final List<Card> cardList = new ArrayList<>();

    private final SecureRandom secureRandom = new SecureRandom();

    void validateDeck(int numDecks, boolean withJokers, List<Card> deck) {
        if (App.RUNTIME_INFO.ASSERTIONS_ENABLED == false) {
            LOG.warn("skipping deck validation because Java was invoked without flag to enable assertions");
            return;
        }

        if (numDecks < 1) { throw new IllegalArgumentException("numDecks must be greater than zero"); }

        Map<CardType, Integer> typeMap = new HashMap<>();
        Map<CardSuit, Integer> suitMap = new HashMap<>();

        for (Card card : deck) {
            if (typeMap.containsKey(card.getCardType()) == false) {
                typeMap.put(card.getCardType(), 1);
            } else {
                typeMap.put(card.getCardType(), typeMap.get(card.getCardType()) + 1);
            }

            if (suitMap.containsKey(card.getCardSuit()) == false) {
                suitMap.put(card.getCardSuit(), 1);
            } else {
                suitMap.put(card.getCardSuit(), suitMap.get(card.getCardSuit()) + 1);
            }
        }

        String errorMessage = "";

        // should be 13 of each suit
        int expected_suit_count = 13 * numDecks;
        errorMessage = getErrorMessage(
                expected_suit_count,
                CardSuit.HEARTS.name(),
                suitMap.get(CardSuit.HEARTS)
        );
        assert suitMap.get(CardSuit.HEARTS) == expected_suit_count: errorMessage;

        errorMessage = getErrorMessage(
                expected_suit_count,
                CardSuit.DIAMONDS.name(),
                suitMap.get(CardSuit.DIAMONDS)
        );
        assert suitMap.get(CardSuit.DIAMONDS) == expected_suit_count: errorMessage;

        errorMessage = getErrorMessage(
                expected_suit_count,
                CardSuit.CLUBS.name(),
                suitMap.get(CardSuit.CLUBS)
        );
        assert suitMap.get(CardSuit.CLUBS) == expected_suit_count: errorMessage;

        errorMessage = getErrorMessage(
                expected_suit_count,
                CardSuit.SPADES.name(),
                suitMap.get(CardSuit.SPADES)
        );
        assert suitMap.get(CardSuit.SPADES) == expected_suit_count: errorMessage;

        // should be four of each type - one per suit
        // we'll deal with jokers in the following block
        int expected_count_by_type = 4 * numDecks;
        for (CardType cardType : CardType.values()) {
            if (cardType == CardType.JOKER || cardType == CardType.CUT) { continue; }
            errorMessage = getErrorMessage(
                    expected_count_by_type,
                    cardType.name(),
                    typeMap.get(cardType)
            );
            assert typeMap.get(cardType) == expected_count_by_type: errorMessage;
        }

        if (withJokers) {
            // should be two jokers
            int expected_joker_count = 2 * numDecks;
            errorMessage = getErrorMessage(
                    expected_joker_count,
                    CardType.JOKER.name(),
                    typeMap.get(CardType.JOKER)
            );
            assert typeMap.get(CardType.JOKER) == expected_joker_count: errorMessage;

            // both of which should be the only cards with NONE as suit
            errorMessage = getErrorMessage(
                    expected_joker_count,
                    CardSuit.NONE.name(),
                    suitMap.get(CardSuit.NONE)
            );
            assert suitMap.get(CardSuit.NONE) == expected_joker_count: errorMessage;

            // 2->A + Jokers = 14 card types
            int expected_unique_types = 14;
            errorMessage = getErrorMessage(
                    expected_unique_types,
                    "unique card types",
                    typeMap.keySet().size()
            );
            assert typeMap.keySet().size() == expected_unique_types: errorMessage;

            // The joker should be the only duplicate card in the deck
            int expected_size_as_set = 53;
            int actual_size_as_set = deck.stream().collect(Collectors.toSet()).size();
            errorMessage = getErrorMessage(
                    expected_size_as_set,
                    "unique cards in deck",
                    actual_size_as_set
            );
            assert actual_size_as_set == expected_size_as_set: errorMessage;

            // Double check deck size
            int expected_deck_size = 54 * numDecks;
            errorMessage = getErrorMessage(
                    expected_deck_size,
                    "cards in deck",
                    deck.size()
            );
            assert deck.size() == expected_deck_size: errorMessage;
        } else {
            // should be nothing in the deck that has a suit of NONE
            int expected_suit_none_count = 0;
            int actual_suit_none_count = suitMap.containsKey(CardSuit.NONE) ? suitMap.get(CardSuit.NONE) : 0;
            errorMessage = getErrorMessage(
                    expected_suit_none_count,
                    CardSuit.NONE.name(),
                    actual_suit_none_count
            );
            assert suitMap.containsKey(CardSuit.NONE) == false: errorMessage;

            // 2->A = 13 card types
            int expected_unique_types = 13;
            errorMessage = getErrorMessage(
                    expected_unique_types,
                    "unique card types",
                    typeMap.keySet().size()
            );
            assert typeMap.keySet().size() == expected_unique_types: errorMessage;

            // every card in the deck should be unique
            int expected_size_as_set = 52;
            int actual_size_as_set = deck.stream().collect(Collectors.toSet()).size();
            errorMessage = getErrorMessage(
                    expected_size_as_set,
                    "unique cards in deck",
                    actual_size_as_set
            );
            assert actual_size_as_set == expected_size_as_set: errorMessage;

            // double check deck size
            int expected_deck_size = 52 * numDecks;
            errorMessage = getErrorMessage(
                    expected_deck_size,
                    "cards in deck",
                    deck.size()
            );
            assert deck.size() == expected_deck_size: errorMessage;
        }
    }

    public void shuffle() { Collections.shuffle(cardList, secureRandom); }

    public void shuffle(int count) {
         if (count < 1) {
             String msg = MessageTemplates.getErrorMessage(
                     ">=1",
                     "value for argument [count]",
                     String.valueOf(count)
             );
             LOG.error(msg);
             throw new IllegalArgumentException(msg);
         }

         for (int i = 0; i < count; i++) { shuffle(); }
    }

    public void insert(Card card, int index) { cardList.add(index, card); }

    public void remove(Card removeCard) {
        int removeIndex = cardList.indexOf(removeCard);
        while (removeIndex > -1) {
            cardList.remove(removeIndex);
            removeIndex = cardList.indexOf(removeCard);
        }
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

    public List<Card> peek(int num) {
        try {
            List<Card> rv = new ArrayList<>(cardList.subList(0, num));
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
            String msg = MessageTemplates.getErrorMessage(
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

    @Override
    public String toString() {
        return getAllCards(false).toString();
    }

}
