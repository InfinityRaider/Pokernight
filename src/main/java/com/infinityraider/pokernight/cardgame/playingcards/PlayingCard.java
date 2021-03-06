package com.infinityraider.pokernight.cardgame.playingcards;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;

public final class PlayingCard implements Comparable<PlayingCard> {
    private static final Map<EnumCardType, Map<EnumCardValue, PlayingCard>> CARD_MAP = new HashMap<>();
    private static final Set<PlayingCard> CARDS = buildCardListAndMap();

    public static Set<PlayingCard> getCards() {
        return CARDS;
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

    public int getCardIndex() {
        return this.getValue().ordinal() + (this.getType().ordinal() * EnumCardValue.values().length);
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
        return this.getCardIndex();
    }

    @Override
    public String toString() {
        return "card." + this.getValue().describe() + "." + this.getType().describe();
    }


    /**
     * Utility methods to retrieve cards
     * ---------------------------------
     */

    /**
     * Gets the card from its index (returned by PlayingCard#getCardIndex() )
     * @param index the card index
     * @return the playing card with this index
     */
    public static PlayingCard getCard(int index) {
        index = index % (EnumCardValue.values().length * EnumCardValue.values().length);
        int type = index / EnumCardValue.values().length;
        int value = index % EnumCardValue.values().length;
        return getCard(EnumCardType.values()[type], EnumCardValue.values()[value]);
    }

    /**
     * Gets the card for a type and value
     * @param type the card type
     * @param value the card value
     * @return the card with this type and value
     */
    public static PlayingCard getCard(EnumCardType type, EnumCardValue value) {
        return getCard(value, type);
    }

    /**
     * Gets the card for a type and value
     * @param value the card value
     * @param type the card type
     * @return the card with this type and value
     */
    public static PlayingCard getCard(EnumCardValue value, EnumCardType type) {
        return CARD_MAP.get(type).get(value);
    }

    /**
     * Serializes a list of cards into an int array
     * @param cards list of cards
     * @return the same list of cards represented as an int array
     */
    public static int[] getIntArrayFromCardList(List<PlayingCard> cards) {
        return ArrayUtils.toPrimitive(cards.stream().map(PlayingCard::getCardIndex).collect(Collectors.toList()).toArray(new Integer[cards.size()]));
    }

    /**
     * Deserializes an int array into a list of cards
     * @param array an int array representing card indices
     * @return the list of cards represented by the passed array
     */
    public static List<PlayingCard> getCardListFromIntArray(int[] array) {
        return Arrays.stream(array).mapToObj(PlayingCard::getCard).collect(Collectors.toList());
    }

    /**
     * private method used to initialize all 52 different cards and store them in the static fields
     * @return a set containing all the 52 cards
     */
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
