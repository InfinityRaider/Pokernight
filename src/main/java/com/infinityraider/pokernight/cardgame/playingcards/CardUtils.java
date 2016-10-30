package com.infinityraider.pokernight.cardgame.playingcards;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class CardUtils {
    private CardUtils() {}

    public static List<PlayingCard> shuffleCards(Collection<PlayingCard> cards, Random rng) {
        List<PlayingCard> list = Lists.newArrayList(cards);
        Collections.shuffle(list, rng);
        return list;
    }

    public static List<PlayingCard> filterCards(Collection<PlayingCard> cards, EnumCardValue value) {
        return filterCards(cards, Filters.match(value));
    }

    public static List<PlayingCard> filterCards(Collection<PlayingCard> cards, EnumCardType type) {
        return filterCards(cards, Filters.match(type));
    }

    public static List<PlayingCard> filterCards(Collection<PlayingCard> cards, Predicate<PlayingCard> filter) {
        return cards.stream().filter(filter).collect(Collectors.toList());
    }

    public static PlayingCard getHighestCard(Collection<PlayingCard> cards) {
        return getHighestCard(cards, Filters.none());
    }

    public static PlayingCard getHighestCard(Collection<PlayingCard> cards, EnumCardType type) {
        return getHighestCard(cards, Filters.match(type));
    }

    public static PlayingCard getHighestCard(Collection<PlayingCard> cards, Predicate<PlayingCard> filter) {
        PlayingCard max = null;
        for(PlayingCard card : cards) {
            if(filter.test(card)) {
                if(max == null || card.compareTo(max) > 0) {
                    max = card;
                }
            }
        }
        return max;
    }

    public static List<PlayingCard> sortCards(Collection<PlayingCard> cards) {
        return cards.stream().sorted().collect(Collectors.toList());
    }

    public static List<PlayingCard> sortCardsForValue(Collection<PlayingCard> cards) {
        return sortCardsForValue(cards, Comparators.valueFirst());
    }

    public static List<PlayingCard> sortCardsForType(Collection<PlayingCard> cards) {
        return sortCardsForValue(cards, Comparators.typeFirst());
    }

    public static List<PlayingCard> sortCardsForValue(Collection<PlayingCard> cards, Comparator<PlayingCard> comparator) {
        return cards.stream().sorted(comparator).collect(Collectors.toList());
    }

    public static class Filters {
        public static Predicate<PlayingCard> none() {
            return card -> true;
        }

        public static Predicate<PlayingCard> match(EnumCardType type) {
            return card -> card.getType() == type;
        }

        public static Predicate<PlayingCard> match(EnumCardValue value) {
            return card -> card.getValue() == value;
        }
    }

    public static class Comparators {
        public static Comparator<PlayingCard> typeFirst() {
            return (card1, card2) -> {
                int delta = card1.getType().compareTo(card2.getType());
                if(delta == 0) {
                    delta = card1.getValue().compareTo(card2.getValue());
                }
                return delta;
            };
        }

        public static Comparator<PlayingCard> valueFirst() {
            return PlayingCard::compareTo;
        }

    }
}
