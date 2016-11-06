package com.infinityraider.pokernight.cardgame.poker;

import net.minecraft.tileentity.TileEntity;

import java.util.Collection;

public interface IPokerGameProvider {
    TileEntity getTile();

    PokerGame getCurrentGame();

    Collection<PokerPlayer> getPlayers(PokerGame game);

    void onGameCompleted();

    int getBlind();
}
