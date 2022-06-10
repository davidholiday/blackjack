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
                    handList = Arrays.asList(new Hand[4]);
                    break;
                case PLAYER_CAN_RESPLIT_TO_THREE_HANDS:
                    handList = Arrays.asList(new Hand[3]);
                    break;
                case PLAYER_CAN_RESPLIT_TO_TWO_HANDS:
                    handList = Arrays.asList(new Hand[2]);
                    break;
                default:
                    throw new IllegalStateException("PLAYER_RESPLIT_RULE present but not processed correctly!");
            }
        } else {
            handList = Arrays.asList(new Hand[1]);
        }


    }

    public void addHand(Hand hand) {
        handList.add(hand);
    }

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
        return rv;
    }


}
