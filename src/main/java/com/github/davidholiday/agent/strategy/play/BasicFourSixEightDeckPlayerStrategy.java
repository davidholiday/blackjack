package com.github.davidholiday.agent.strategy.play;

import com.github.davidholiday.agent.AgentPosition;
import com.github.davidholiday.card.Card;
import com.github.davidholiday.card.CardType;
import com.github.davidholiday.cardcollection.Hand;
import com.github.davidholiday.game.Action;
import com.github.davidholiday.game.ActionToken;
import com.github.davidholiday.game.Rule;
import com.github.davidholiday.game.RuleSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicFourSixEightDeckPlayerStrategy extends PlayerStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(BasicFourSixEightDeckPlayerStrategy.class);

    public static final String NAME = "BASIC_PLAYER_STRATEGY";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Action evaluateHandForInsurance(Hand hand, int count, ActionToken actionToken) {
        return Action.DECLINE_INSURANCE;
    }

    @Override
    public Action evaluateForSurrender(Hand hand, int count, ActionToken actionToken) {
        if (hand.getCardListSize() != 2) { return Action.NONE; }

        RuleSet ruleSet = actionToken.getRuleSet();
        if (ruleSet.contains(Rule.PLAYER_CAN_EARLY_SURRENDER)
                || ruleSet.contains(Rule.PLAYER_CAN_LATE_SURRENDER)) {

            CardType dealerUpCardType = getDealerUpCard(actionToken).getCardType();
            boolean dealerCanHitSoft17 = ruleSet.contains(Rule.DEALER_CAN_HIT_SOFT_17);
            switch (hand.getHandValue()) {
                case 17:
                    if (dealerCanHitSoft17
                            && dealerUpCardType == CardType.ACE) {
                        return Action.SURRENDER;
                    }
                    break;
                case 16:
                    if (dealerUpCardType == CardType.NINE
                            || dealerUpCardType == CardType.TEN
                            || dealerUpCardType == CardType.JACK
                            || dealerUpCardType == CardType.QUEEN
                            || dealerUpCardType == CardType.KING
                            || dealerUpCardType == CardType.ACE) {

                        // a pair of 8s is treated differently than 16 that isn't a pair of 8s
                        if (hand.isPair() == false) {
                            return Action.SURRENDER;
                        }
                        if (hand.isPair() && dealerUpCardType == CardType.ACE) {
                            return Action.SURRENDER;
                        }
                    }
                    break;
                case 15:
                    if (dealerUpCardType == CardType.TEN
                            || dealerUpCardType == CardType.JACK
                            || dealerUpCardType == CardType.QUEEN
                            || dealerUpCardType == CardType.KING) {
                        return Action.SURRENDER;
                    }

                    if (dealerCanHitSoft17 && dealerUpCardType == CardType.ACE) {
                        return Action.SURRENDER;
                    }
            }
        }
        return Action.NONE;
    }

    @Override
    public Action evaluateForSplit(Hand hand, int count, ActionToken actionToken) {
        if (hand.isBust()
                || hand.isPair() == false
                || actionToken.getEvaluatePairForSplit() == false) {
            return Action.NONE;
        }

        RuleSet ruleSet = actionToken.getRuleSet();

        CardType dealerUpCardType = getDealerUpCard(actionToken).getCardType();
        switch (hand.peek(1).get(0).getCardType()) {
            case ACE:
                return Action.SPLIT;
            case KING:
                // fall into QUEEN
            case QUEEN:
                // fall into JACK
            case JACK:
                // fall into TEN
            case TEN:
                return Action.STAND;
            case NINE:
                if (dealerUpCardType == CardType.TWO
                        || dealerUpCardType == CardType.THREE
                        || dealerUpCardType == CardType.FOUR
                        || dealerUpCardType == CardType.FIVE
                        || dealerUpCardType == CardType.SIX
                        || dealerUpCardType == CardType.EIGHT
                        || dealerUpCardType == CardType.NINE) {

                    return Action.SPLIT;
                } else {
                    return Action.NONE;
                }
            case EIGHT:
                return Action.SPLIT;
            case SEVEN:
                if (dealerUpCardType == CardType.TWO
                        || dealerUpCardType == CardType.THREE
                        || dealerUpCardType == CardType.FOUR
                        || dealerUpCardType == CardType.FIVE
                        || dealerUpCardType == CardType.SIX
                        || dealerUpCardType == CardType.SEVEN) {

                    return Action.SPLIT;
                } else {
                    return Action.NONE;
                }
            case SIX:
                if (ruleSet.contains(Rule.PLAYER_CAN_DOUBLE_AFTER_SPLIT)
                        && dealerUpCardType == CardType.TWO) {

                    return Action.SPLIT;
                } else if (dealerUpCardType == CardType.THREE
                        || dealerUpCardType == CardType.FOUR
                        || dealerUpCardType == CardType.FIVE
                        || dealerUpCardType == CardType.SIX) {

                    return Action.SPLIT;
                } else {
                    return Action.NONE;
                }
            case FIVE:
                return Action.NONE;
            case FOUR:
                if (ruleSet.contains(Rule.PLAYER_CAN_DOUBLE_AFTER_SPLIT)
                        && (dealerUpCardType == CardType.FIVE || dealerUpCardType == CardType.SIX)) {

                    return Action.SPLIT;
                } else {
                    return Action.NONE;
                }
            case THREE:
                // fall into TWO
            case TWO:
                if (ruleSet.contains(Rule.PLAYER_CAN_DOUBLE_AFTER_SPLIT)
                        && (dealerUpCardType == CardType.TWO || dealerUpCardType == CardType.THREE)) {

                    return Action.SPLIT;
                } else if (dealerUpCardType == CardType.FOUR
                        || dealerUpCardType == CardType.FIVE
                        || dealerUpCardType == CardType.SIX
                        || dealerUpCardType == CardType.SEVEN) {

                    return Action.SPLIT;
                } else {
                    return Action.NONE;
                }
            default:
                throw new IllegalStateException("in code path that should not be possible to be in");
        }
    }

    @Override
    public Action evaluateForSoft(Hand hand, int count, ActionToken actionToken) {
        // no need to evaluate if this isn't a soft hand or the action token is telling us not to
        //   evaluate this hand for a split. the latter happens when the Player object knows the rules don't
        //   allow things like resplit of ACEs. This method has no awareness of how many hands the player
        //   currently has - so the flag is there to tell this method whether or not it's cool to proceed.
        if (hand.isSoft() == false || actionToken.getEvaluatePairForSplit() == false) { return Action.NONE; }

        // no need to evaluate if the hand is 21 - we're definitely going to STAND.
        if (hand.isBlackJack() || hand.isTwentyOne()) { return Action.STAND; }

        // if we're here it's because the player can't re-split ACES. the SPLIT evaluation happens before the SOFT
        //   evaluation, which this is. Therefore, we're going to HIT regardless of dealer up card as this hand is
        //   valued at 2/12
        if (hand.isPair() && hand.getAceSpecialHandValue() == 12) { return Action.HIT; }

        CardType dealerUpCardType = getDealerUpCard(actionToken).getCardType();
        // ACE values are [1, 11]
        // we need the soft value minus the ACE to make a play determination
        int softValueNoAce = hand.getAceSpecialHandValue() - CardType.ACE.getValues()[1].getValue();

        // we need the number of cards in the hand because in some cases that impacts the correct next play
        int numCardsInHand = hand.getCardListSize();

        switch (softValueNoAce) {
            case 9:
                return Action.STAND;
            case 8:
                if (dealerUpCardType == CardType.SIX && numCardsInHand == 2) {
                    return Action.DOUBLE_DOWN;
                } else {
                    return Action.STAND;
                }
            case 7:
                if (dealerUpCardType == CardType.TWO
                        || dealerUpCardType == CardType.THREE
                        || dealerUpCardType == CardType.FOUR
                        || dealerUpCardType == CardType.FIVE
                        || dealerUpCardType == CardType.SIX) {

                    if (numCardsInHand == 2) {
                        return Action.DOUBLE_DOWN;
                    } else {
                        return Action.STAND;
                    }
                } else if (dealerUpCardType == CardType.SEVEN
                        || dealerUpCardType == CardType.EIGHT) {

                    return Action.STAND;
                } else {
                    return Action.HIT;
                }
            case 6:
                if (dealerUpCardType == CardType.TWO) {
                    return Action.HIT;
                } else if (dealerUpCardType == CardType.THREE
                        || dealerUpCardType == CardType.FOUR
                        || dealerUpCardType == CardType.FIVE
                        || dealerUpCardType == CardType.SIX) {

                    if (numCardsInHand == 2) {
                        return Action.DOUBLE_DOWN;
                    } else {
                        return Action.HIT;
                    }
                } else {
                    return Action.HIT;
                }
            case 5:
                // fall into FOUR
            case 4:
                if (dealerUpCardType == CardType.FOUR
                        || dealerUpCardType == CardType.FIVE
                        || dealerUpCardType == CardType.SIX) {

                    if (numCardsInHand == 2) {
                        return Action.DOUBLE_DOWN;
                    } else {
                        return Action.HIT;
                    }
                } else {
                    return Action.HIT;
                }
            case 3:
                // fall into TWO
            case 2:
                if (dealerUpCardType == CardType.FIVE
                        || dealerUpCardType == CardType.SIX) {

                    if (numCardsInHand == 2) {
                        return Action.DOUBLE_DOWN;
                    } else {
                        return Action.HIT;
                    }
                } else {
                    return Action.HIT;
                }
            default:
                throw new IllegalStateException("in code path that should not be possible to be in");
        }

    }

    @Override
    public Action evaluateForHard(Hand hand, int count, ActionToken actionToken) {
        if (hand.isBust()) { return Action.NONE; }
        RuleSet ruleSet = actionToken.getRuleSet();

        CardType dealerUpCardType = getDealerUpCard(actionToken).getCardType();
        switch (hand.getHandValue()) {
            case 21:
            case 20:
            case 19:
            case 18:
            case 17:
                return Action.STAND;
            case 16:
            case 15:
            case 14:
            case 13:
                if (dealerUpCardType == CardType.TWO
                        || dealerUpCardType == CardType.THREE
                        || dealerUpCardType == CardType.FOUR
                        || dealerUpCardType == CardType.FIVE
                        || dealerUpCardType == CardType.SIX) {
                    return Action.STAND;
                } else {
                    return Action.HIT;
                }
            case 12:
                if (dealerUpCardType == CardType.FOUR
                        || dealerUpCardType == CardType.FIVE
                        || dealerUpCardType == CardType.SIX) {
                    return Action.STAND;
                } else {
                    return Action.HIT;
                }
            case 11:
                if (ruleSet.contains(Rule.PLAYER_CAN_DOUBLE_ON_ANY_FIRST_TWO_CARDS)
                        || ruleSet.contains(Rule.PLAYER_CAN_DOUBLE_ON_NINE_THROUGH_ELEVEN_ONLY)
                        || ruleSet.contains(Rule.PLAYER_CAN_DOUBLE_ON_TEN_ELEVEN_ONLY)) {

                    return Action.DOUBLE_DOWN;
                } else {
                    return Action.HIT;
                }
            case 10:
                if (dealerUpCardType == CardType.TWO
                        || dealerUpCardType == CardType.THREE
                        || dealerUpCardType == CardType.FOUR
                        || dealerUpCardType == CardType.FIVE
                        || dealerUpCardType == CardType.SIX
                        || dealerUpCardType == CardType.SEVEN
                        || dealerUpCardType == CardType.EIGHT
                        || dealerUpCardType == CardType.NINE) {

                    if (ruleSet.contains(Rule.PLAYER_CAN_DOUBLE_ON_ANY_FIRST_TWO_CARDS)
                            || ruleSet.contains(Rule.PLAYER_CAN_DOUBLE_ON_NINE_THROUGH_ELEVEN_ONLY)
                            || ruleSet.contains(Rule.PLAYER_CAN_DOUBLE_ON_TEN_ELEVEN_ONLY)) {

                        return Action.DOUBLE_DOWN;
                    } else {
                        return Action.HIT;
                    }
                } else {
                    return Action.HIT;
                }
            case 9:
                if (dealerUpCardType == CardType.THREE
                        || dealerUpCardType == CardType.FOUR
                        || dealerUpCardType == CardType.FIVE
                        || dealerUpCardType == CardType.SIX) {

                    if (ruleSet.contains(Rule.PLAYER_CAN_DOUBLE_ON_ANY_FIRST_TWO_CARDS)
                            || ruleSet.contains(Rule.PLAYER_CAN_DOUBLE_ON_NINE_THROUGH_ELEVEN_ONLY)) {

                        return Action.DOUBLE_DOWN;
                    } else {
                        return Action.HIT;
                    }
                } else {
                    return Action.HIT;
                }
            default:
                return Action.HIT;
        }

    }

    private Card getDealerUpCard(ActionToken actionToken) {

        Hand dealerHand = actionToken.getPlayerHandMap()
                                     .get(AgentPosition.DEALER);

        if (dealerHand.getCardListSize() != 2) {
            throw new IllegalStateException("dealer should have exactly two cards when player is making plays!");
        }

        if (dealerHand.getAllCards(false).get(0).getCardType() != CardType.HIDDEN) {
            throw new IllegalStateException("dealer hole card should be hidden when player is making plays!");
        }

        if (dealerHand.getAllCards(false).get(1).getCardType() == CardType.HIDDEN) {
            throw new IllegalStateException("dealer up card should not be hidden when player is making plays!");
        }

        return dealerHand.getAllCards(false).get(1);
    }

}
