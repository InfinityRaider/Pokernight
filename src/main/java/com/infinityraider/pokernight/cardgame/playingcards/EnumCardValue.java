package com.infinityraider.pokernight.cardgame.playingcards;

public enum EnumCardValue implements Comparable<EnumCardValue> {
    ACE,
    KING,
    QUEEN,
    JACK,
    TEN,
    NINE,
    EIGHT,
    SEVEN,
    SIX,
    FIVE,
    FOUR,
    THREE,
    TWO;

    public String describe() {
        return this.name().toLowerCase();
    }

    public int value() {
        return EnumCardValue.values().length - this.ordinal() + 1;
    }
}
