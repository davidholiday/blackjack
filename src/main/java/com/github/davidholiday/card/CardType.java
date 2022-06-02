package com.github.davidholiday.card;

import java.util.Arrays;

public enum CardType {
    ACE(new CardValue[] {CardValue.ONE, CardValue.ELEVEN}),
    TWO(new CardValue[] {CardValue.TWO}),
    THREE(new CardValue[] {CardValue.THREE}),
    FOUR(new CardValue[] {CardValue.FOUR}),
    FIVE(new CardValue[] {CardValue.FIVE}),
    SIX(new CardValue[] {CardValue.SIX}),
    SEVEN(new CardValue[] {CardValue.SEVEN}),
    EIGHT(new CardValue[] {CardValue.EIGHT}),
    NINE(new CardValue[] {CardValue.NINE}),
    TEN(new CardValue[] {CardValue.TEN}),
    JACK(new CardValue[] {CardValue.TEN}),
    QUEEN(new CardValue[] {CardValue.TEN}),
    KING(new CardValue[] {CardValue.TEN}),
    JOKER(new CardValue[] {CardValue.ZERO}),
    CUT(new CardValue[] {CardValue.ZERO}),
    HIDDEN(new CardValue[] {CardValue.ZERO});

    private final CardValue[] values;
    private CardType(CardValue[] values) {
        this.values = values;
    }

    public CardValue[] getValues() {
        return Arrays.copyOf(this.values, this.values.length);
    }

}
