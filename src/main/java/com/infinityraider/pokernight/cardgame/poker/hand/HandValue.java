package com.infinityraider.pokernight.cardgame.poker.hand;

import com.infinityraider.pokernight.cardgame.playingcards.EnumCardValue;
import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;

import java.util.Optional;

public class HandValue implements Comparable<HandValue> {
    public static Builder getBuilder() {
        return new Builder();
    }

    private final int[] values ;

    private HandValue() {
        this.values = new int[PokerHand.SIZE];
    }

    @Override
    public int compareTo(HandValue other) {
        int delta = 0;
        for(int i = 0; i < this.values.length; i++) {
            delta = this.values[i] - other.values[i];
            if(delta != 0) {
                break;
            }
        }
        return delta;
    }

    public static class Builder {
        private final HandValue value;
        private Optional<HandValue> optional;
        private int index;

        private Builder() {
            this.value = new HandValue();
            this.optional = Optional.empty();
            this.index = 0;
        }

        public boolean isComplete() {
            return index >= PokerHand.SIZE;
        }

        public Builder addValue(PlayingCard card) {
            return this.addValue(card.getValue());
        }

        public Builder addValue(EnumCardValue value) {
            return this.addValue(value.value());
        }

        public Builder addValue(int value) {
            if(!this.isComplete()) {
                this.value.values[this.index] = value;
                this.index = this.index + 1;
            }
            if(this.isComplete()) {
                this.optional = Optional.of(this.value);
            }
            return this;
        }

        public Optional<HandValue> getValue() {
            return this.optional;
        }
    }
}
