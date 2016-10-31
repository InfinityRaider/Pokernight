package com.infinityraider.pokernight.cardgame.poker;

public enum GamePhase {
    PRE_GAME,
    PRE_FLOP(true),
    FLOP(3, true),
    TURN(1, true),
    RIVER(1, true),
    END_GAME;

    private final int cardsToDeal;
    private final boolean doBets;

    GamePhase() {
        this(0);
    }

    GamePhase(boolean doBets) {
        this(0, doBets);
    }

    GamePhase(int cardsToDeal) {
        this(cardsToDeal, false);
    }

    GamePhase(int cardsToDeal, boolean doBets) {
        this.cardsToDeal = cardsToDeal;
        this.doBets = doBets;
    }

    public int getCardsToDeal() {
        return this.cardsToDeal;
    }

    public boolean doBets() {
        return this.doBets;
    }

    public GamePhase nextPhase() {
        if(this == END_GAME) {
            return this;
        }
        else {
            return values()[this.ordinal() + 1];
        }
    }
}
