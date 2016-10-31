package com.infinityraider.pokernight.cardgame.poker;

public enum PlayerState {
    WAITING(false, false),
    FOLDED(true, false),
    CALLED(false, true),
    RAISED(false, true),
    ALL_IN(true, true);

    private final boolean ignoreRaises;
    private final boolean inGame;

    PlayerState(boolean ignoreRaise, boolean isInGame) {
        this.ignoreRaises = ignoreRaise;
        this.inGame = isInGame;
    }

    public boolean ignoreRaises() {
        return this.ignoreRaises;
    }

    public boolean isInGame() {
        return this.inGame;
    }
}
