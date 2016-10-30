package com.infinityraider.pokernight.cardgame.poker.hand.types;

import com.infinityraider.pokernight.cardgame.playingcards.CardUtils;
import com.infinityraider.pokernight.cardgame.playingcards.EnumCardType;
import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;
import com.infinityraider.pokernight.cardgame.poker.hand.HandType;
import com.infinityraider.pokernight.cardgame.poker.hand.HandValue;
import com.infinityraider.pokernight.cardgame.poker.hand.PokerHand;

import java.util.List;
import java.util.Optional;

public class HandTypeFlush extends HandType {
    public static final HandType INSTANCE = new HandTypeFlush();

    private HandTypeFlush() {
        super("flush", 4);
    }

    @Override
    public Optional<HandValue> match(List<PlayingCard> cards) {
        HandValue.Builder builder = HandValue.getBuilder();
        for(EnumCardType type : EnumCardType.values()) {
            List<PlayingCard> cardsForType = CardUtils.filterCards(cards, type);
            if(cardsForType.size() >= PokerHand.SIZE) {
                cardsForType = CardUtils.sortCardsForValue(cardsForType);
                for(int i = 0; i < PokerHand.SIZE; i++) {
                    builder.addValue(cardsForType.get(i));
                }
            }
        }
        return builder.getValue();
    }
}
