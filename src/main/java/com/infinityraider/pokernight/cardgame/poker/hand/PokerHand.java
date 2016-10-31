package com.infinityraider.pokernight.cardgame.poker.hand;

import com.google.common.collect.ImmutableList;
import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;
import com.infinityraider.pokernight.cardgame.poker.hand.types.*;

import java.util.List;
import java.util.Optional;

public class PokerHand implements Comparable<PokerHand> {
    public static final int SIZE = 5;

    public static final HandType FIVE_OF_A_KIND = HandTypeFiveOfAKind.INSTANCE;
    public static final HandType STRAIGHT_FLUSH = HandTypeStraightFlush.INSTANCE;
    public static final HandType FOUR_OF_A_KIND = HandTypeFourOfAKind.INSTANCE;
    public static final HandType FULL_HOUSE = HandTypeFullHouse.INSTANCE;
    public static final HandType FLUSH = HandTypeFlush.INSTANCE;
    public static final HandType STRAIGHT = HandTypeStraight.INSTANCE;
    public static final HandType THREE_OF_A_KIND = HandTypeThreeOfAKind.INSTANCE;
    public static final HandType TWO_PAIR = HandTypeTwoPair.INSTANCE;
    public static final HandType ONE_PAIR = HandTypeOnePair.INSTANCE;
    public static final HandType HIGH_CARD = HandTypeHighCard.INSTANCE;

    public static final List<HandType> HAND_TYPES = ImmutableList.of(
            FIVE_OF_A_KIND,
            STRAIGHT_FLUSH,
            FOUR_OF_A_KIND,
            FULL_HOUSE,
            FLUSH,
            STRAIGHT,
            THREE_OF_A_KIND,
            TWO_PAIR,
            ONE_PAIR,
            HIGH_CARD
    );

    private HandType type;
    private HandValue value;

    public PokerHand(List<PlayingCard> cards) {
        for(HandType type : HAND_TYPES) {
            Optional<HandValue> value = type.match(cards);
            if(value.isPresent()) {
                this.type = type;
                this.value = value.get();
                break;
            }
        }
    }

    public HandType getType() {
        return this.type;
    }

    public HandValue getValue() {
        return this.value;
    }

    @Override
    public int compareTo(PokerHand other) {
        int delta = this.getType().compareTo(other.getType());
        if(delta == 0) {
            delta = this.getValue().compareTo(other.getValue());
        }
        return delta;
    }
}
