package com.github.davidholiday.cardcollection;

import com.github.davidholiday.card.Card;
import com.github.davidholiday.game.Rule;
import com.github.davidholiday.game.RuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class HandCollection {

    private static final Logger LOG = LoggerFactory.getLogger(HandCollection.class);

    private final List<Hand> handList;

    private final int maxListSize;

    private final Optional<Rule> resplitToRuleOptional;

    public HandCollection(RuleSet ruleSet) {
        Optional<Rule> tempOptional = Optional.empty();
        boolean resplitRuleFound = false;
        for (Rule rule : Rule.getPlayerCanResplitToRuleSet()) {
            if (ruleSet.contains(rule)) {
                tempOptional = Optional.of(rule);
                resplitRuleFound = true;
                break;
            }
        }

        resplitToRuleOptional = tempOptional.isEmpty() ? tempOptional : Optional.of(tempOptional.get());

        // this will help prevent resplits beyond what the rules say is allowable by making the handList fixed size
        if (resplitToRuleOptional.isPresent()) {
            switch (resplitToRuleOptional.get()) {
                case PLAYER_CAN_RESPLIT_TO_FOUR_HANDS:
                    maxListSize = 4;
                    handList = new ArrayList<>(maxListSize);
                    break;
                case PLAYER_CAN_RESPLIT_TO_THREE_HANDS:
                    maxListSize = 3;
                    handList = new ArrayList<>(maxListSize);
                    break;
                case PLAYER_CAN_RESPLIT_TO_TWO_HANDS:
                    maxListSize = 2;
                    handList = new ArrayList<>(maxListSize);
                    break;
                default:
                    throw new IllegalStateException("PLAYER_RESPLIT_RULE present but not processed correctly!");
            }
        } else {
            maxListSize = 1;
            handList = new ArrayList<>(1);
        }

        // made sure there's always at least one hand object in the collection
        handList.add(new Hand());
    }

    public void addHand(Hand hand) {
        handList.add(hand);
        if (handList.size() > maxListSize) {
            String msg = "player has more hands than the rules allow for!";
            throw new IllegalStateException(msg);
        }
    }

    public Hand getHand(int handIndex) {
        return handList.get(handIndex);
    }

    public Hand popHand(int handIndex) {
        return handList.remove(handIndex);
    }

    public List<Hand> getHandList() { return handList; }

    public void addCardsToHand(List<Card> cardList, int handIndex) {
        handList.get(handIndex)
                .addCards(cardList);
    }

    public void addCardToHand(Card card, int handIndex) {
        handList.get(handIndex)
                .addCards(List.of(card));
    }

    public List<Card> clearHands() {
        List<Card> rv = new ArrayList<>();
        for (Hand hand : handList) {
            rv.addAll(hand.getAllCards(true));
        }
        handList.clear();
        // made sure there's at least one always one hand object in the collection
        handList.add(new Hand());
        return rv;
    }


}
