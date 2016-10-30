package com.infinityraider.pokernight.cardgame.poker.hand;

import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;

import java.util.List;
import java.util.Optional;

public abstract class HandType implements Comparable<HandType> {
    private final String name;
    private final int rank;

    protected HandType(String name, int rank) {
        this.name = name;
        this.rank = rank;
    }

    public final int rank() {
        return this.rank;
    }

    public final String name() {
        return this.name;
    }

    public abstract Optional<HandValue> match(List<PlayingCard> cards);

    @Override
    public int compareTo(HandType other) {
        return this.rank() - other.rank();
    }
}
