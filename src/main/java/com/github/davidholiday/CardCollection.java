package com.github.davidholiday;

import java.security.SecureRandom;
import java.util.*;

public abstract class CardCollection {

    private final List<Card> cardList = new ArrayList<>();

    private final SecureRandom secureRandom = new SecureRandom();

    public void shuffle() { Collections.shuffle(cardList, secureRandom); }

    public Optional<Card> draw() {
        try {
            return Optional.of(cardList.remove(0));
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    public Optional<List<Card>> draw(int num) {
        try {
            Optional<List<Card>> rv = Optional.of(
                    new ArrayList<>(cardList.subList(0, num))
            );
            cardList.subList(0, num).clear();
            return rv;
        } catch (IndexOutOfBoundsException e) {
            return Optional.empty();
        }
    }

    public boolean cut(int index) {
        Optional<List<Card>> cutHeadOptional = draw(index);
        if (cutHeadOptional.isEmpty()) { return false; }
        List<Card> cutHead = cutHeadOptional.get();
        cardList.addAll(cutHead);
        return true;
    }

    public List<Card> getAllCards(boolean remove) {
        List<Card> rv = List.copyOf(cardList);
        if (remove) { cardList.clear(); }
        return rv;
    }

    public void addCards(List<Card> cardList) { this.cardList.addAll(cardList); }

    public int getCardListSize() { return cardList.size(); }

}
