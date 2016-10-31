package com.infinityraider.pokernight.cardgame.poker.hand.types;

import com.infinityraider.pokernight.cardgame.playingcards.EnumCardValue;
import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;
import com.infinityraider.pokernight.cardgame.poker.hand.HandType;
import com.infinityraider.pokernight.cardgame.poker.hand.HandValue;
import com.infinityraider.pokernight.cardgame.poker.hand.PokerHand;

import java.util.*;

public abstract class HandTypeAmountOfTwoKinds extends HandType {
    private final int a;
    private final int b;

    protected HandTypeAmountOfTwoKinds(String name, int rank, int a, int b) {
        super(name, rank);
        this.a = Math.max(a, b);
        this.b = Math.min(a, b);
    }

    @Override
    public Optional<HandValue> match(List<PlayingCard> cards) {
        Map<EnumCardValue, Integer> counters = new HashMap<>();
        for(PlayingCard card : cards) {
            if(counters.containsKey(card.getValue())) {
                counters.put(card.getValue(), counters.get(card.getValue()) + 1);
            } else {
                counters.put(card.getValue(), 1);
            }
        }
        EnumCardValue valueA = null;
        EnumCardValue valueB = null;
        List<EnumCardValue> values = new ArrayList<>();
        for(EnumCardValue value : EnumCardValue.values()) {
            if(counters.containsKey(value)) {
                int counter = counters.get(value);
                if(counter >= this.a) {
                    if(valueA == null) {
                        valueA = value;
                    } else if(valueB == null) {
                        valueB = value;
                    } else {
                        values.add(value);
                    }
                } else if(counter >= b) {
                    if(valueB == null) {
                        valueB = value;
                    } else {
                        values.add(value);
                    }
                } else {
                    values.add(value);
                }
            }
        }
        HandValue.Builder builder = HandValue.getBuilder();
        int counter = 0;
        if(valueA != null && valueB != null) {
            for(int i = 0; i < counters.get(valueA); i++) {
                builder.addValue(valueA);
                counter++;
            }
            for(int i = 0; i < counters.get(valueB); i++) {
                builder.addValue(valueB);
                counter++;
            }
            for(int i = counter; i <= PokerHand.SIZE; i++) {
                builder.addValue(values.get(i - counter));
            }
        }
        return builder.getValue();
    }
}
