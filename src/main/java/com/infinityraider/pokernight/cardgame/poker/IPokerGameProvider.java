package com.infinityraider.pokernight.cardgame.poker;

public interface IPokerGameProvider {
    PokerPlayer[] getPlayers(PokerGame game);

    void onGameCompleted();

    int getBlind();
}
