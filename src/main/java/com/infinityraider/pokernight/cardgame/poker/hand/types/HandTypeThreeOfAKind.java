package com.infinityraider.pokernight.cardgame.poker.hand.types;

import com.infinityraider.pokernight.cardgame.poker.hand.HandType;

public class HandTypeThreeOfAKind extends HandTypeAmountOfAKind {
    public static final HandType INSTANCE = new HandTypeThreeOfAKind();

    private HandTypeThreeOfAKind() {
        super("three_of_a_kind", 6, 3);
    }
}
