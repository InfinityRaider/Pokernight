package com.infinityraider.pokernight.cardgame.poker.hand.types;

import com.infinityraider.pokernight.cardgame.playingcards.CardUtils;
import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;
import com.infinityraider.pokernight.cardgame.poker.hand.HandType;
import com.infinityraider.pokernight.cardgame.poker.hand.HandValue;

import java.util.List;
import java.util.Optional;

public class HandTypeHighCard extends HandType {
    public static final HandType INSTANCE = new HandTypeHighCard();

    private HandTypeHighCard() {
        super("high_card", 9);
    }

    @Override
    public Optional<HandValue> match(List<PlayingCard> cards) {
        List<PlayingCard> sorted = CardUtils.sortCardsForValue(cards);
        HandValue.Builder builder = HandValue.getBuilder();
        for(PlayingCard card : sorted) {
            builder.addValue(card);
            if(builder.isComplete()) {
                return builder.getValue();
            }
        }
        return builder.getValue();
    }
}
