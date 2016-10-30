package com.infinityraider.pokernight.cardgame.poker.hand.types;

import com.infinityraider.pokernight.cardgame.poker.hand.HandType;

public class HandTypeFourOfAKind extends HandTypeAmountOfAKind {
    public static final HandType INSTANCE = new HandTypeFourOfAKind();

    private HandTypeFourOfAKind() {
        super("four_of_a_kind", 2, 4);
    }
}
