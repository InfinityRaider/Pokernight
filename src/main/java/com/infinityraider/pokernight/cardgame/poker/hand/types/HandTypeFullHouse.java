package com.infinityraider.pokernight.cardgame.poker.hand.types;

import com.infinityraider.pokernight.cardgame.poker.hand.HandType;

public class HandTypeFullHouse extends HandTypeAmountOfTwoKinds {
    public static final HandType INSTANCE = new HandTypeFullHouse();

    private HandTypeFullHouse() {
        super("full_house", 3, 3, 2);
    }
}
