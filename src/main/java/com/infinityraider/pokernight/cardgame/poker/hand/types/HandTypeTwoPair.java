package com.infinityraider.pokernight.cardgame.poker.hand.types;

import com.infinityraider.pokernight.cardgame.poker.hand.HandType;

public class HandTypeTwoPair extends HandTypeAmountOfTwoKinds {
    public static final HandType INSTANCE = new HandTypeTwoPair();

    private HandTypeTwoPair() {
        super("two_pair", 7, 2, 2);
    }
}
