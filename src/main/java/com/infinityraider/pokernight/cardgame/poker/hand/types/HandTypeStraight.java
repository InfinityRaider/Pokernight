package com.infinityraider.pokernight.cardgame.poker.hand.types;

import com.infinityraider.pokernight.cardgame.playingcards.CardUtils;
import com.infinityraider.pokernight.cardgame.playingcards.EnumCardValue;
import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;
import com.infinityraider.pokernight.cardgame.poker.hand.HandType;
import com.infinityraider.pokernight.cardgame.poker.hand.HandValue;
import com.infinityraider.pokernight.cardgame.poker.hand.PokerHand;

import java.util.List;
import java.util.Optional;

public class HandTypeStraight extends HandType {
    public static final HandType INSTANCE = new HandTypeStraight();

    private HandTypeStraight() {
        super("straight", 5);
    }

    @Override
    public Optional<HandValue> match(List<PlayingCard> cards) {
        List<PlayingCard> sorted = CardUtils.sortCardsForValue(cards);
        int amount = 0;
        EnumCardValue start = null;
        for(PlayingCard card : sorted) {
            if(amount == 0) {
                amount = 1;
                start = card.getValue();
            } else {
                if(start.value() - amount == card.value()) {
                    //if the next card has the same value as the previous card, the card is skipped
                    continue;
                }
                if(start.value() - amount == card.value()) {
                    //if the next card has a value one lower as the previous card, the straight sequence continues
                    amount = amount + 1;
                } else {
                    if(start.value() == 5 && amount == PokerHand.SIZE - 1 && card.getValue() == EnumCardValue.ACE) {
                        //the allowed exception is if there is an ace in the sequence: 5 - 4 - 3 - 2 - ace
                        amount = amount + 1;
                    } else {
                        //the sequence was interrupted
                        start = card.getValue();
                        amount = 1;
                    }
                }
                if(amount >= PokerHand.SIZE) {
                    break;
                }
            }
        }
        HandValue.Builder builder = HandValue.getBuilder();
        if(amount >= PokerHand.SIZE) {
            for(int i = 0; i < PokerHand.SIZE; i++) {
                builder.addValue(start.value() - i);
            }
        }
        return builder.getValue();
    }
}
