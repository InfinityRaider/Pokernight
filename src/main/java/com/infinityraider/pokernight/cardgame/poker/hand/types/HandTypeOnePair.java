package com.infinityraider.pokernight.cardgame.poker.hand.types;

import com.infinityraider.pokernight.cardgame.poker.hand.HandType;

public class HandTypeOnePair extends HandTypeAmountOfAKind {
    public static final HandType INSTANCE = new HandTypeOnePair();

    private HandTypeOnePair() {
        super("one_pair", 8, 2);
    }

}
