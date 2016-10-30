package com.infinityraider.pokernight.cardgame.playingcards;

import com.google.common.collect.ImmutableSet;

import java.util.*;

public final class PlayingCard implements Comparable<PlayingCard> {
    private static final Map<EnumCardType, Map<EnumCardValue, PlayingCard>> CARD_MAP = new HashMap<>();
    private static final Set<PlayingCard> CARDS = buildCardListAndMap();

    public static Set<PlayingCard> getCards() {
        return CARDS;
    }

    public static PlayingCard getCard(EnumCardType type, EnumCardValue value) {
        return getCard(value, type);
    }

    public static PlayingCard getCard(EnumCardValue value, EnumCardType type) {
        return CARD_MAP.get(type).get(value);
    }

    private final EnumCardType type;
    private final EnumCardValue value;

    private PlayingCard(EnumCardType type, EnumCardValue value) {
        this.type = type;
        this.value = value;
    }

    public EnumCardType getType() {
        return this.type;
    }

    public EnumCardValue getValue() {
        return this.value;
    }

    public int value() {
        return this.getValue().value();
    }

    @Override
    public int compareTo(PlayingCard other) {
        int delta = this.getValue().compareTo(other.getValue());
        if(delta == 0) {
            delta = this.getType().compareTo(other.getType());
        }
        return delta;
    }

    @Override
    public boolean equals(Object other) {
        if(this == other) {
            return true;
        }
        if(other instanceof PlayingCard) {
            PlayingCard card = (PlayingCard) other;
            return this.getType() == card.getType() && this.getValue() == card.getValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (this.getValue().ordinal() << 2) | (this.getType().ordinal());
    }

    @Override
    public String toString() {
        return "card." + this.getValue().describe() + "." + this.getType().describe();
    }

    private static Set<PlayingCard> buildCardListAndMap() {
        List<PlayingCard> cards = new ArrayList<>();
        for(EnumCardType type : EnumCardType.values()) {
            for(EnumCardValue value : EnumCardValue.values()) {
                PlayingCard card = new PlayingCard(type, value);
                cards.add(card);
                if(!CARD_MAP.containsKey(card.getType())) {
                    CARD_MAP.put(card.getType(), new HashMap<>());
                }
                CARD_MAP.get(card.getType()).put(card.getValue(), card);
            }
        }
        return ImmutableSet.copyOf(cards);
    }
}
