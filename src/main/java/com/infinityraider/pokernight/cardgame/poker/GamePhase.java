package com.infinityraider.pokernight.cardgame.poker;

public enum GamePhase {
    PRE_FLOP(0),
    FLOP(3),
    TURN(1),
    RIVER(1),
    POST_RIVER(0);

    private final int cardsToDeal;

    GamePhase(int cardsToDeal) {
        this.cardsToDeal = cardsToDeal;
    }
}
