package com.infinityraider.pokernight.cardgame.poker;

import com.infinityraider.pokernight.cardgame.playingcards.PlayingCard;
import org.apache.commons.lang3.tuple.MutablePair;

public abstract class PokerPlayer {
    private final PokerGame game;
    private int id;

    private PlayerState state;
    private int stack;
    private int bet;
    private MutablePair<PlayingCard, PlayingCard> hand;

    public PokerPlayer(PokerGame game) {
        this.game = game;
        this.state = PlayerState.WAITING;
        this.hand = new MutablePair<>();
        this.stack = 0;
        this.bet = 0;
    }

    public PokerGame getGame() {
        return this.game;
    }

    public int getPlayerId() {
        return this.id;
    }

    void setPlayerId(int id) {
        this.id = id;
    }

    PokerPlayer dealCard(PlayingCard card) {
        if(this.hand.getLeft() == null) {
            this.hand.setLeft(card);
        } else if(this.hand.getRight() == null) {
            this.hand.setRight(card);
        }
        return this;
    }

    PokerPlayer setState(PlayerState state) {
        this.state = state;
        return this;
    }

    public PlayerState getState() {
        return this.state;
    }

    public int getStack() {
        return this.stack;
    }

    public void addGains(int amount) {
        this.stack = this.stack + amount;
    }

    public int getCurrentBet() {
        return this.bet;
    }

    int collectBet(){
        int bet = this.getCurrentBet();
        this.bet = 0;
        return bet;
    }

    void placeBlind(int amount) {
        amount = Math.min(amount, this.stack);
        if(this.stack == amount) {
            this.setState(PlayerState.ALL_IN);
        } else {
            this.setState(PlayerState.WAITING);
        }
        this.stack = this.stack - amount;
        this.bet = amount;
    }

    protected void raise(int amount) {
        amount = Math.min(amount, this.stack);
        if(this.stack == amount) {
            this.setState(PlayerState.ALL_IN);
        } else {
            this.setState(PlayerState.RAISED);
        }
        this.stack = this.stack - amount;
        this.bet = this.bet + amount;
        this.getGame().performPlayerAction(this, PlayerAction.RAISE);
    }

    protected void call() {
        int amount = Math.min(this.getGame().getMinimumRaise(), this.stack);
        if(this.stack == amount) {
            this.state = PlayerState.ALL_IN;
        } else {
            this.setState(PlayerState.CALLED);
        }
        this.stack = this.stack - amount;
        this.bet = this.bet + amount;
        this.getGame().performPlayerAction(this, PlayerAction.CALL);
    }

    protected void fold() {
        this.state = PlayerState.FOLDED;
        this.getGame().performPlayerAction(this, PlayerAction.FOLD);
    }

    PokerPlayer returnHand() {
        this.hand.setLeft(null);
        this.hand.setRight(null);
        this.state = PlayerState.WAITING;
        return this;
    }

    public PlayingCard getFirstCard() {
        return this.hand.getLeft();
    }

    public PlayingCard getSecondCard() {
        return this.hand.getRight();
    }
}
