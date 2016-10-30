package com.infinityraider.pokernight.cardgame.poker.hand.types;

import com.infinityraider.pokernight.cardgame.poker.hand.HandType;

public class HandTypeFiveOfAKind extends HandTypeAmountOfAKind {
    public static final HandType INSTANCE = new HandTypeFiveOfAKind();

    private HandTypeFiveOfAKind() {
        super("five_of_a_kind", 0, 5);
    }
}
