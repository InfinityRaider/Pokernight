package com.infinityraider.pokernight.cardgame.poker.hand.types;

import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;
import com.infinityraider.pokernight.cardgame.poker.hand.HandType;
import com.infinityraider.pokernight.cardgame.poker.hand.HandValue;

import java.util.List;
import java.util.Optional;

public class HandTypeFullHouse extends HandType {
    public static final HandType INSTANCE = new HandTypeFullHouse();

    private HandTypeFullHouse() {
        super("full_house", 3);
    }

    @Override
    public Optional<HandValue> match(List<PlayingCard> cards) {
        return Optional.empty();
    }
}
