package com.infinityraider.pokernight.cardgame.poker;

import java.util.Collection;

public interface IPokerGameProvider {
    Collection<PokerPlayer> getPlayers(PokerGame game);

    void onGameCompleted();

    int getBlind();
}
