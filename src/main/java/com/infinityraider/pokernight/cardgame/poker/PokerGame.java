package com.infinityraider.pokernight.cardgame.poker;

import com.infinityraider.pokernight.cardgame.playingcards.CardDeck;
import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;

import java.util.ArrayList;
import java.util.List;

public class PokerGame {
    private final IPokerGameProvider gameProvider;
    private final PokerPlayer[] players;
    private final CardDeck deck;

    private List<PlayingCard> openCards;
    private List<PlayingCard> closedCards;

    private GamePhase phase;
    private int playerTurn;

    public PokerGame(IPokerGameProvider provider) {
        this.gameProvider = provider;
        this.players = this.getGameProvider().getPlayers();
        this.deck = new CardDeck();
        this.openCards = new ArrayList<>();
    }

    public IPokerGameProvider getGameProvider() {
        return this.gameProvider;
    }

    protected CardDeck getDeck() {
        return this.deck;
    }
}
