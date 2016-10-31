package com.infinityraider.pokernight.cardgame.poker.hand.types;

import com.infinityraider.pokernight.cardgame.playingcards.CardUtils;
import com.infinityraider.pokernight.cardgame.playingcards.EnumCardType;
import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;
import com.infinityraider.pokernight.cardgame.poker.hand.HandType;
import com.infinityraider.pokernight.cardgame.poker.hand.HandValue;
import com.infinityraider.pokernight.cardgame.poker.hand.PokerHand;

import java.util.List;
import java.util.Optional;

public class HandTypeStraightFlush extends HandType {
    public static final HandType INSTANCE = new HandTypeStraightFlush();

    private HandTypeStraightFlush() {
        super("straight_flush", 1);
    }

    @Override
    public Optional<HandValue> match(List<PlayingCard> cards) {
        for(EnumCardType type : EnumCardType.values()) {
            List<PlayingCard> cardsForType = CardUtils.filterCards(cards, type);
            if(cardsForType.size() >= PokerHand.SIZE) {
                return HandTypeStraight.INSTANCE.match(cardsForType);
            }
        }
        return Optional.empty();
    }
}
