package com.infinityraider.pokernight.cardgame.poker.hand.types;

import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;
import com.infinityraider.pokernight.cardgame.poker.hand.HandType;
import com.infinityraider.pokernight.cardgame.poker.hand.HandValue;

import java.util.List;
import java.util.Optional;

public class HandTypeStraight extends HandType {
    public static final HandType INSTANCE = new HandTypeStraight();

    private HandTypeStraight() {
        super("straight", 5);
    }

    @Override
    public Optional<HandValue> match(List<PlayingCard> cards) {
        return Optional.empty();
    }
}
