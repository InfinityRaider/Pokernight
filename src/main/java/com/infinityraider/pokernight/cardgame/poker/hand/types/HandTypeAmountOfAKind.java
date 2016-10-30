package com.infinityraider.pokernight.cardgame.poker.hand.types;

import com.infinityraider.pokernight.cardgame.playingcards.CardUtils;
import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;
import com.infinityraider.pokernight.cardgame.poker.hand.HandType;
import com.infinityraider.pokernight.cardgame.poker.hand.HandValue;
import com.infinityraider.pokernight.cardgame.poker.hand.PokerHand;

import java.util.List;
import java.util.Optional;

public abstract class HandTypeAmountOfAKind extends HandType {
    private final int amount;

    protected HandTypeAmountOfAKind(String name, int rank, int amount) {
        super(name, rank);
        this.amount = amount;
    }

    @Override
    public Optional<HandValue> match(List<PlayingCard> cards) {
        int index = 0;
        int counter = 0;
        //check if the required amount of a kind is there
        List<PlayingCard> sorted = CardUtils.sortCardsForValue(cards);
        for(int i = 0; i < sorted.size(); i++) {
            PlayingCard card = sorted.get(i);
            if(i == 0) {
                counter = 1;
            } else {
                if(card.equals(sorted.get(index))) {
                    counter = counter + 1;
                } else {
                    index = i;
                    counter = 0;
                }
            }
        }
        //calculate value
        HandValue.Builder builder = HandValue.getBuilder();
        if(counter >= amount) {
            for(int i = 0; i < PokerHand.SIZE; i++) {
                if(i < amount) {
                    builder.addValue(sorted.get(i + index));
                } else {
                    int j = i - amount;
                    if(j < index) {
                        builder.addValue(sorted.get(j));
                    } else {
                        builder.addValue(sorted.get(i));
                    }
                }
            }
        }
        return builder.getValue();
    }
}
