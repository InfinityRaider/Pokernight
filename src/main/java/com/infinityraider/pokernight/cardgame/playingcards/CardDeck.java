package com.infinityraider.pokernight.cardgame.playingcards;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import java.util.*;

public class CardDeck {
    private final ICardDeckProvider provider;
    private final Random rng;

    private Deque<PlayingCard> deck;
    private Set<PlayingCard> dealt;

    public CardDeck() {
        this(PlayingCard::getCards);
    }

    public CardDeck(ICardDeckProvider provider) {
        this.provider = provider;
        this.rng = new Random();
        this.dealt = new HashSet<>();
        this.shuffle();
    }

    public ICardDeckProvider getProvider() {
        return this.provider;
    }

    public Random getRNG() {
        return this.rng;
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
        this.deck = new ArrayDeque<>(CardUtils.shuffleCards(this.getProvider().getDeckCards(), this.getRNG()));
        this.dealt.clear();
        return this;
    }

    public int[] writeToIntArray() {
        int[] array = new int[1 + this.deck.size() + this.dealt.size()];
        array[0] = this.deck.size();
        PlayingCard[] deckArray = this.deck.toArray(new PlayingCard[this.deck.size()]);
        for(int i = 0; i < deckArray.length; i++) {
            array[i + 1] = deckArray[i].getCardIndex();
        }
        PlayingCard[] dealtArray = this.dealt.toArray(new PlayingCard[this.dealt.size()]);
        for(int i = 0; i < dealtArray.length; i++) {
            array[i + deckArray.length + 1] = dealtArray[i].getCardIndex();
        }
        return array;
    }

    public CardDeck readFromIntArray(int[] array) {
        if(array.length != this.deck.size() + this.dealt.size() + 1) {
            return this;
        }
        int deckSize = array[0];
        List<PlayingCard> cards = Lists.newArrayList();
        for(int i = 0; i < deckSize; i++) {
            cards.add(PlayingCard.getCard(array[1 + i]));
        }
        this.deck = new ArrayDeque<>(cards);
        this.dealt.clear();
        for(int i = 1 + deckSize; i < array.length; i++) {
            dealt.add(PlayingCard.getCard(array[i]));
        }
        return this;
    }
}
