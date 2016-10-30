package com.infinityraider.pokernight.cardgame.playingcards;

import com.google.common.collect.ImmutableSet;

import java.util.*;

public class CardDeck {
    private final Random rng;

    private Deque<PlayingCard> deck;
    private Set<PlayingCard> dealt;

    public CardDeck() {
        this.rng = new Random();
        this.shuffle();
        this.dealt = new HashSet<>();
    }

    public Set<PlayingCard> getDeckCards() {
        return ImmutableSet.copyOf(this.deck);
    }

    public Set<PlayingCard> getDealtCards() {
        return ImmutableSet.copyOf(this.dealt);
    }

    public int dealtCardCount() {
        return dealt.size();
    }

    public int deckCardCount() {
        return deck.size();
    }

    public Optional<PlayingCard> dealCard() {
        if(deck.size() > 0) {
            PlayingCard card = deck.pop();
            dealt.add(card);
            return Optional.of(card);
        } else {
            return Optional.empty();
        }
    }

    public CardDeck shuffle() {
        this.deck = new ArrayDeque<>(CardUtils.shuffleCards(PlayingCard.getCards(), this.getRNG()));
        this.dealt.clear();
        return this;
    }

    public Random getRNG() {
        return this.rng;
    }
}
