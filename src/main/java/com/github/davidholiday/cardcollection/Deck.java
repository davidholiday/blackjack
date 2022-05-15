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

import static com.github.davidholiday.util.MessageTemplates.getErrorMessage;

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
        int expected_count_by_type = 4;
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
            int expected_joker_count = 2;
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
            int expected_deck_size = 54;
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
            int expected_deck_size = 52;
            errorMessage = getErrorMessage(
                    expected_deck_size,
                    "cards in deck",
                    deck.size()
            );
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
