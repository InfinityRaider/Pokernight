package com.infinityraider.pokernight.cardgame.poker;

import net.minecraft.entity.player.EntityPlayer;

public class PokerPlayerImpl extends PokerPlayer {
    private final EntityPlayer player;

    public PokerPlayerImpl(PokerGame game, EntityPlayer player) {
        super(game);
        this.player = player;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }
}
