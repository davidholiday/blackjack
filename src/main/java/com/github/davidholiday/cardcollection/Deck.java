package com.github.davidholiday.cardcollection;

import java.text.MessageFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.davidholiday.App;
import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardSuit;
import com.github.davidholiday.card.CardType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.davidholiday.util.MessageTemplates.getCountDeltaErrorMessage;

public class Deck extends CardCollection {

    private final int expectedFullDeckSize;

    private static final Logger LOG = LoggerFactory.getLogger(Deck.class);

    // so the Shoe class can create a deck without making a deck object
    public static List<Card> makeDeck(boolean withJokers) {
        List<Card> rv = new ArrayList<>();

        for (CardSuit cardSuit : CardSuit.values()) {
            if (cardSuit == CardSuit.NONE) { continue; }
            for (CardType cardType : CardType.values()) {
                if (cardType == CardType.CUT || cardType == CardType.JOKER) { continue; }
                Card card = new Card(cardType, cardSuit);
                rv.add(card);
            }
        }

        if (withJokers) {
            rv.addAll(
                    Stream.of(
                            new Card(CardType.JOKER, CardSuit.NONE),
                            new Card(CardType.JOKER, CardSuit.NONE)
                    ).collect(Collectors.toList())
            );
        }

        if (App.RUNTIME_INFO.ASSERTIONS_ENABLED) { validateDeck(withJokers, rv); }
        return rv;
    }

    private static void validateDeck(boolean withJokers, List<Card> deck) {
        if (App.RUNTIME_INFO.ASSERTIONS_ENABLED == false) {
            LOG.warn("skipping deck validation because Java was invoked without flag to enable assertions");
            return;
        }
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
        int expected_suit_count = 13;
        errorMessage =
                getCountDeltaErrorMessage(expected_suit_count, suitMap.get(CardSuit.HEARTS), CardSuit.HEARTS.name());
        assert suitMap.get(CardSuit.HEARTS) == expected_suit_count: errorMessage;

        errorMessage =
                getCountDeltaErrorMessage(expected_suit_count, suitMap.get(CardSuit.DIAMONDS), CardSuit.DIAMONDS.name());
        assert suitMap.get(CardSuit.DIAMONDS) == expected_suit_count: errorMessage;

        errorMessage =
                getCountDeltaErrorMessage(expected_suit_count, suitMap.get(CardSuit.CLUBS), CardSuit.CLUBS.name());
        assert suitMap.get(CardSuit.CLUBS) == expected_suit_count: errorMessage;

        errorMessage =
                getCountDeltaErrorMessage(expected_suit_count, suitMap.get(CardSuit.SPADES), CardSuit.SPADES.name());
        assert suitMap.get(CardSuit.SPADES) == expected_suit_count: errorMessage;

        // should be four of each type - one per suit
        // we'll deal with jokers in the following block
        int expected_count_by_type = 4;
        for (CardType cardType : CardType.values()) {
            if (cardType == CardType.JOKER || cardType == CardType.CUT) { continue; }
            errorMessage =
                    getCountDeltaErrorMessage(expected_count_by_type, typeMap.get(cardType), cardType.name());
            assert typeMap.get(cardType) == expected_count_by_type: errorMessage;
        }

        if (withJokers) {
            // should be two jokers
            int expected_joker_count = 2;
            errorMessage =
                    getCountDeltaErrorMessage(expected_joker_count, typeMap.get(CardType.JOKER), CardType.JOKER.name());
            assert typeMap.get(CardType.JOKER) == expected_joker_count: errorMessage;

            // both of which should be the only cards with NONE as suit
            errorMessage =
                    getCountDeltaErrorMessage(expected_joker_count, suitMap.get(CardSuit.NONE), CardSuit.NONE.name());
            assert suitMap.get(CardSuit.NONE) == expected_joker_count: errorMessage;

            // 2->A + Jokers = 14 card types
            int expected_unique_types = 14;
            errorMessage =
                    getCountDeltaErrorMessage(expected_unique_types, typeMap.keySet().size(), "unique card types");
            assert typeMap.keySet().size() == expected_unique_types: errorMessage;

            // The joker should be the only duplicate card in the deck
            int expected_size_as_set = 53;
            int actual_size_as_set = deck.stream().collect(Collectors.toSet()).size();
            errorMessage =
                    getCountDeltaErrorMessage(expected_size_as_set, actual_size_as_set, "unique cards in deck");
            assert actual_size_as_set == expected_size_as_set: errorMessage;

            // Double check deck size
            int expected_deck_size = 54;
            errorMessage =
                    getCountDeltaErrorMessage(expected_deck_size, deck.size(), "cards in deck");
            assert deck.size() == expected_deck_size: errorMessage;
        } else {
            // should be nothing in the deck that has a suit of NONE
            int expected_suit_none_count = 0;
            int actual_suit_none_count = suitMap.containsKey(CardSuit.NONE) ? suitMap.get(CardSuit.NONE) : 0;
            errorMessage =
                    getCountDeltaErrorMessage(expected_suit_none_count, actual_suit_none_count, CardSuit.NONE.name());
            assert suitMap.containsKey(CardSuit.NONE) == false: errorMessage;

            // 2->A = 13 card types
            int expected_unique_types = 13;
            errorMessage =
                    getCountDeltaErrorMessage(expected_unique_types, typeMap.keySet().size(), "unique card types");
            assert typeMap.keySet().size() == expected_unique_types: errorMessage;

            // every card in the deck should be unique
            int expected_size_as_set = 52;
            int actual_size_as_set = deck.stream().collect(Collectors.toSet()).size();
            errorMessage =
                    getCountDeltaErrorMessage(expected_size_as_set, actual_size_as_set, "unique cards in deck");
            assert actual_size_as_set == expected_size_as_set: errorMessage;

            // double check deck size
            int expected_deck_size = 52;
            errorMessage =
                    getCountDeltaErrorMessage(expected_deck_size, deck.size(), "cards in deck");
            assert deck.size() == expected_deck_size: errorMessage;
        }
    }




    public Deck(boolean withJokers) {
        List<Card> cardList = makeDeck(withJokers);
        if (withJokers) { expectedFullDeckSize = 54; }
        else { expectedFullDeckSize = 52; }
        this.addCards(cardList);
    }

    @Override
    public void addCards(List<Card> cardList) {
        int currentDeckSize = this.getCardListSize();
        int newDeckSize = cardList.size() + currentDeckSize;
        if (newDeckSize > this.expectedFullDeckSize) {
            String msg = MessageFormat.format(
                    "attempt to add too many cards to a deck with a max size of {0}", expectedFullDeckSize);
            LOG.error(msg);
            throw new IllegalArgumentException(msg);
        }
        super.addCards(cardList);
    }

}
