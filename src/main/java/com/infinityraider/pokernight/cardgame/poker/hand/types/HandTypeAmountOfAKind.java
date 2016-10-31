package com.infinityraider.pokernight.cardgame.poker.hand.types;

public abstract class HandTypeAmountOfAKind extends HandTypeAmountOfTwoKinds {
    protected HandTypeAmountOfAKind(String name, int rank, int amount) {
        super(name, rank, amount, 1);

    }
}
