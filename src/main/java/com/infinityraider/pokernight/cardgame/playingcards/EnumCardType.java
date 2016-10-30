package com.infinityraider.pokernight.cardgame.playingcards;

public enum EnumCardType {
    HEARTS(255 << 16),
    DIAMONDS(255 << 16),
    CLOVERS(0),
    SPADES(0);

    private final int color;

    EnumCardType(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    public String describe() {
        return this.name().toLowerCase();
    }
}
